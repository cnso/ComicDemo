package com.jash.comicdemo.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jash.comicdemo.BaseApplication;
import com.jash.comicdemo.R;
import com.jash.comicdemo.BR;
import com.jash.comicdemo.databinding.PictureBinding;
import com.jash.comicdemo.entities.Chapter;
import com.jash.comicdemo.entities.Picture;
import com.jash.comicdemo.entities.PictureDao;
import com.jash.comicdemo.utils.CommentAdapter;
import com.jash.comicdemo.utils.CustomScaleType;
import com.jash.comicdemo.utils.Parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class PictureActivity extends AppCompatActivity {
    private PictureBinding binding;
    private BaseApplication application;
    private Chapter chapter;
    private CommentAdapter<Picture> adapter;
    private Subscription subscribe;
    private List<DataSource<?>> sources = new ArrayList<>();
    private Comparator<Picture> comparator = (p1, p2) -> {
        int i = (int) (p1.getId() - p2.getId());
        if (i == 0) {
            i = p1.getScaleType() == CustomScaleType.CLIP_START ? -1 : 1;
        }
        return i;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_picture);
        application = ((BaseApplication) getApplication());
        long chapterId = getIntent().getLongExtra("chapterId", 0);
        chapter = application.getSession().getChapterDao().load(chapterId);
        adapter = new CommentAdapter<>(this, new ArrayList<>(), R.layout.item_picture, BR.pic);
        application.getSession()
                .getPictureDao()
                .queryBuilder()
                .where(PictureDao.Properties.ChapterId.eq(chapterId))
                .rx()
                .oneByOne()
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addPicture, Throwable::printStackTrace);
        subscribe = application.getSubject()
                .ofType(Picture.class)
                .filter(pic -> !adapter.contains(pic))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addPicture);
        if (chapter.getPicCount() == 0 || adapter.getItemCount() < chapter.getPicCount()) {
            application.getService().getPicture(chapter.getComicId(), chapter.getId())
                    .map(Parser::parse)
                    .map(doc -> doc.select("td[valign]").first())
                    .map(ele -> Parser.parsePicture(ele, chapter))
                    .flatMap(Observable::from)
                    .filter(pair -> application.getSession().getPictureDao().load(chapter.getId() << 10 | pair.first) == null)
                    .flatMap(pair -> Observable.concat(application.getService().tryPicture(pair.second).map(Response::code), Observable.just(pair)).toList())
                    .map(list -> Pair.create((int)list.get(0), (Pair<Integer, String>)list.get(1)))
                    .flatMap(pair -> {
                        if (pair.first == 200) {
                            return Observable.just(pair.second);
                        } else {
                            return Observable.concat(Observable.just(pair.second.first),
                                        application.getService().getPicture(chapter.getComicId(), chapter.getId(), pair.second.first)
                                            .map(Parser::parse)
                                            .map(doc -> doc.select("td[valign] > script:eq(3)").first())
                                            .map(ele -> Parser.parsePicUrl(ele).first))
                                        .toList()
                                        .map(list -> Pair.create((Integer)list.get(0), (String)list.get(1)));
                        }
                    })
                    .map(pair -> new Picture(chapter.getId() << 10 | pair.first, chapter.getId(), pair.second, 0, 0))
                    .onBackpressureBuffer()
                    .doOnNext(application.getSubject()::onNext)
                    .subscribe(pic ->{}, Throwable::printStackTrace);
        }
        binding.pictureList.setAdapter(adapter);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
        for (DataSource<?> source : sources) {
            if (!source.isClosed()) {
                source.close();
            }
        }
    }
    private void addPicture(Picture pic) {
        if (pic.getWidth() == 0) {
            DataSource<CloseableReference<CloseableImage>> source = Fresco.getImagePipeline()
                    .fetchDecodedImage(ImageRequest.fromUri(pic.getUrl()), null);
            sources.add(source);
            source.subscribe(new BaseBitmapDataSubscriber() {
                        @Override
                        protected void onNewResultImpl(Bitmap bitmap) {
                            pic.setWidth(bitmap.getWidth());
                            pic.setHeight(bitmap.getHeight());
                            pic.getAspect().set((float) bitmap.getWidth() / bitmap.getHeight());
                            if (pic.getAspect().get() > 1) {
                                adapter.remove(pic);
                                application.getSubject().onNext(pic);
                            }
                            application.getSession().getPictureDao().insertOrReplace(pic);
                        }

                        @Override
                        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        }
                    }, CallerThreadExecutor.getInstance());
        } else {
            sources.add(Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(pic.getUrl()), null));
        }
        if (pic.getAspect().get() > 1) {
            Picture clone = pic.clone();
            pic.setScaleType(CustomScaleType.CLIP_START);
            clone.setScaleType(CustomScaleType.CLIP_END);
            adapter.add(pic, comparator);
            adapter.add(clone, comparator);

        } else {
            adapter.add(pic, comparator);
        }
    }
}
