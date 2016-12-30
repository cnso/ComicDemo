package com.jash.comicdemo.entities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.facebook.binaryresource.ByteArrayBinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.jash.comicdemo.R;
import com.jash.comicdemo.activities.ComicInfoActivity;
import com.jash.comicdemo.databinding.ItemHomeBinding;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by majie on 16/10/25.
 */
@Entity
public class Comic {
    @Id
    private long id;
    private String title;
    private String img;
    private int width;
    private int height;
    private String author;
    private String status;
    private Date updateTime;
    private String info;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private static final Uri COMIC_URI = Uri.parse("comic://org.jash.comic");



    @Generated(hash = 1711210716)
    public Comic(long id, String title, String img, int width, int height, String author, String status, Date updateTime, String info) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.width = width;
        this.height = height;
        this.author = author;
        this.status = status;
        this.updateTime = updateTime;
        this.info = info;
    }

    @Generated(hash = 1347984162)
    public Comic() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void showInfo(View view) {
        Context context = view.getContext();
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ComicInfoActivity.class);
        intent.putExtra("comicId", id);
        ItemHomeBinding binding = DataBindingUtil.getBinding(view);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, binding.comicIcon, "comic_icon");
        ContextCompat.startActivity(context, intent, options.toBundle());
//        context.startActivity(intent);
    }

    public void createShortcut(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager manager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
            ImageRequest request = ImageRequest.fromUri(img);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(request, null);
            FileBinaryResource resource = (FileBinaryResource) ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            Bitmap icon = BitmapFactory.decodeFile(resource.getFile().getAbsolutePath());
            ShortcutInfo info = new ShortcutInfo.Builder(context, String.valueOf(id))
                    .setIntent(new Intent(Intent.ACTION_VIEW, ContentUris.withAppendedId(COMIC_URI, id)))
                    .setIcon(Icon.createWithBitmap(icon))
                    .setShortLabel(title)
                    .build();
            manager.addDynamicShortcuts(Collections.singletonList(info));
            Toast.makeText(context, "长按Icon看看", Toast.LENGTH_SHORT).show();
        }
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
        String ret = "未知";
        if (updateTime != null) {
            ret = SDF.format(updateTime);
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comic comic = (Comic) o;

        return id == comic.id;

    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
