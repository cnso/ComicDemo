package com.jash.comicdemo;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.jash.comicdemo.databinding.HomeBinding;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.entities.ComicDao;
import com.jash.comicdemo.utils.CommentAdapter;
import com.jash.comicdemo.utils.Parser;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    private BaseApplication application;
    private Subscription subscribe;
    private HomeBinding binding;
    private CommentAdapter<Comic> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        application = ((BaseApplication) getApplication());
        List<Comic> list = application.getSession()
                .getComicDao()
                .queryBuilder()
                .orderDesc(ComicDao.Properties.UpdateTime)
                .limit(24)
                .list();
        adapter = new CommentAdapter<>(this, list, R.layout.item_home, BR.comic);
        setSupportActionBar(binding.toolbar);
        subscribe = application.getSubject()
                .ofType(Comic.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::add, Throwable::printStackTrace);
        binding.homeSwipe.setOnRefreshListener(this::refresh);
        binding.homeGrid.setAdapter(adapter);
        if (adapter.getItemCount() == 0) {
            binding.homeSwipe.setRefreshing(true);
            refresh();
        }
    }

    private void refresh() {
        adapter.clear();
        application.getService()
                .getHome()
                .map(Parser::parse)
                .map(doc -> doc.select("#comicmain:eq(3) > dd"))
                .flatMap(Observable::from)
                .map(Parser::parseComicFromList)
                .doOnNext(application.getSubject()::onNext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comic -> {}, throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }, () -> binding.homeSwipe.setRefreshing(false));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }
}
