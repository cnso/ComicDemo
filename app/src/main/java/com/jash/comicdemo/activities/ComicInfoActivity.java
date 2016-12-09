package com.jash.comicdemo.activities;

import android.databinding.DataBindingUtil;
import android.support.v4.app.ActivityCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.jash.comicdemo.BR;
import com.jash.comicdemo.BaseApplication;
import com.jash.comicdemo.R;
import com.jash.comicdemo.databinding.InfoBinding;
import com.jash.comicdemo.entities.Chapter;
import com.jash.comicdemo.entities.ChapterDao;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.utils.CommentAdapter;
import com.jash.comicdemo.utils.Parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ComicInfoActivity extends AppCompatActivity {
    private InfoBinding binding;
    private BaseApplication application;
    private Subscription comic_subscribe;
    private Comic comic;
    private CommentAdapter<Chapter> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ((BaseApplication) getApplication());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comic_info);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        long comicId = getIntent().getLongExtra("comicId", 0);
        comic = application.getSession().getComicDao().load(comicId);
        binding.setComic(comic);
        comic_subscribe = application.getSubject()
                .ofType(Comic.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(binding::setComic);
        List<Chapter> chapters = application.getSession()
                .getChapterDao()
                .queryBuilder()
                .where(ChapterDao.Properties.ComicId.eq(comicId))
                .list();
        adapter = new CommentAdapter<>(this, chapters, R.layout.item_chapter, BR.chapter);
        binding.infoList.setAdapter(adapter);
        binding.infoSwipe.setOnRefreshListener(this::loadComic);
        application.getSubject().ofType(Chapter.class)
                .onBackpressureBuffer()
                .filter(chapter -> chapter.getComicId() == comic.getId())
                .filter(chapter -> !adapter.contains(chapter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::add, Throwable::printStackTrace);
        if (TextUtils.isEmpty(comic.getInfo())) {
            binding.infoSwipe.setRefreshing(true);
            loadComic();
        }
    }

    private void loadComic() {
        application.getService()
                .getComic(comic.getId())
                .map(Parser::parse)
                .doOnNext(doc -> {
                    Parser.updateComicFromInfo(doc, comic);
                    application.getSubject().onNext(comic);
                })
                .map(doc -> doc.select("#comiclistn > dd > a:eq(0)"))
                .flatMap(Observable::from)
                .map(Parser::parseChapter)
                .doOnNext(application.getSubject()::onNext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chapter -> {}, t -> {
                    binding.infoSwipe.setRefreshing(false);
                    t.printStackTrace();
                    Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }, () -> binding.infoSwipe.setRefreshing(false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!comic_subscribe.isUnsubscribed()) {
            comic_subscribe.unsubscribe();
        }
    }
}
