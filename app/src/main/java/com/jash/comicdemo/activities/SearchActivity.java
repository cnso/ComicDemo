package com.jash.comicdemo.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jash.comicdemo.BR;
import com.jash.comicdemo.BaseApplication;
import com.jash.comicdemo.R;
import com.jash.comicdemo.databinding.SearchBinding;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.utils.CommentAdapter;
import com.jash.comicdemo.utils.Parser;

import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    private CommentAdapter<Comic> adapter;
    private BaseApplication application;
    private String keyword;
    private SearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setSupportActionBar(binding.toolbar);
        String extra = getIntent().getStringExtra("keyword");
        setTitle(extra);
        adapter = new CommentAdapter<>(this, new ArrayList<>(), R.layout.item_home, BR.comic);
        binding.searchGrid.setAdapter(adapter);
        application = ((BaseApplication) getApplication());
        try {
            this.keyword = URLEncoder.encode(extra, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        application.getService()
                .searchComic(1, keyword)
                .map(Parser::parse)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(doc -> {
                    Element element = doc.select("#comicmain > div").first();
                    if (element != null) {
                        binding.loading.setText(element.ownText());
                    } else {
                        binding.loading.setVisibility(View.GONE);
                    }
                })
                .observeOn(Schedulers.io())
                .map(doc -> doc.select("#comicmain > dd"))
                .flatMap(Observable::from)
                .map(ele -> Parser.parseComicFromList(ele, application.getSession().getComicDao()))
                .doOnNext(application.getSubject()::onNext)
                .filter(comic -> !adapter.contains(comic))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::add, throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });

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
}
