package com.jash.comicdemo.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.jash.comicdemo.BaseApplication;
import com.jash.comicdemo.R;
import com.jash.comicdemo.databinding.InfoBinding;
import com.jash.comicdemo.entities.Comic;

public class ComicInfoActivity extends AppCompatActivity {
    private InfoBinding binding;
    private BaseApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_info);
        application = ((BaseApplication) getApplication());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comic_info);
        long comicId = getIntent().getLongExtra("comicId", 0);
        Comic comic = application.getSession().getComicDao().load(comicId);
        binding.setComic(comic);
    }
}
