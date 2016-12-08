package com.jash.comicdemo.entities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.jash.comicdemo.activities.ComicInfoActivity;
import com.jash.comicdemo.databinding.ItemHomeBinding;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by majie on 16/10/25.
 */
@Entity
public class Comic {
    @Id
    private long id;
    private String text;
    private String img;
    private int width;
    private int height;
    private Date updateTime;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    @Generated(hash = 8208301)
    public Comic(long id, String text, String img, int width, int height,
            Date updateTime) {
        this.id = id;
        this.text = text;
        this.img = img;
        this.width = width;
        this.height = height;
        this.updateTime = updateTime;
    }

    @Generated(hash = 1347984162)
    public Comic() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getAspectRatio() {
        return (float) width / height;
    }

    public void click(View view) {
        Context context = view.getContext();
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ComicInfoActivity.class);
        intent.putExtra("comicId", id);
        ItemHomeBinding binding = DataBindingUtil.getBinding(view);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, binding.comicIcon, "comic_icon");

        Bundle bundle = options.toBundle();
        ContextCompat.startActivity(context, intent, bundle);
//        context.startActivity(intent);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getUpdateString() {
        return SDF.format(updateTime);
    }
}
