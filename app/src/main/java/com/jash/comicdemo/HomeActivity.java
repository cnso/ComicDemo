package com.jash.comicdemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jash.comicdemo.activities.SearchActivity;
import com.jash.comicdemo.databinding.HomeBinding;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.entities.ComicDao;
import com.jash.comicdemo.utils.CommentAdapter;
import com.jash.comicdemo.utils.Parser;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private BaseApplication application;
    private Subscription subscribe;
    private HomeBinding binding;
    private CommentAdapter<Comic> adapter;
    private MenuItem item;
    private boolean isNight;

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
        if (savedInstanceState != null) {
            isNight = savedInstanceState.getInt("appcompat:local_night_mode") == AppCompatDelegate.MODE_NIGHT_YES;
        }
        adapter = new CommentAdapter<>(this, list, R.layout.item_home, BR.comic);
        setSupportActionBar(binding.toolbar);
        subscribe = application.getSubject()
                .ofType(Comic.class)
                .filter(comic -> !adapter.contains(comic))
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
                .map(ele -> Parser.parseComicFromList(ele, application.getSession().getComicDao()))
                .doOnNext(application.getSubject()::onNext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comic -> {}, throwable -> {
                    binding.homeSwipe.setRefreshing(false);
                    throwable.printStackTrace();
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }, () -> binding.homeSwipe.setRefreshing(false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        item = menu.findItem(R.id.search);
        SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setSubmitButtonEnabled(true);
        search.setOnQueryTextListener(this);
        menu.findItem(R.id.night_mode).setChecked(isNight);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.night_mode:
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
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
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
//        intent.putExtra("keyword", "火影");
        intent.putExtra("keyword", query);
        startActivity(intent);
        MenuItemCompat.collapseActionView(item);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
