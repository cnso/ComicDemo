package com.jash.comicdemo.entities;

import android.content.Context;
import android.content.Intent;

import com.jash.comicdemo.activities.PictureActivity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Chapter {
    @Id
    private long id;
    private long comicId;
    private String name;
    private int picCount;
    @Generated(hash = 1832262859)
    public Chapter(long id, long comicId, String name, int picCount) {
        this.id = id;
        this.comicId = comicId;
        this.name = name;
        this.picCount = picCount;
    }
    @Generated(hash = 393170288)
    public Chapter() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getComicId() {
        return this.comicId;
    }
    public void setComicId(long comicId) {
        this.comicId = comicId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chapter chapter = (Chapter) o;

        return id == chapter.id;

    }

    public void showPicture(Context context) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra("chapterId", id);
        context.startActivity(intent);
    }
    public int getPicCount() {
        return this.picCount;
    }
    public void setPicCount(int picCount) {
        this.picCount = picCount;
    }
}
