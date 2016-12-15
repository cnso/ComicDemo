package com.jash.comicdemo.entities;

import android.databinding.ObservableFloat;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Picture {
    @Id
    private long id;
    private long chapterId;
    private String url;
    private int width;
    private int height;
    @Transient
    private ObservableFloat aspect = new ObservableFloat(1);
    @Generated(hash = 490436120)
    public Picture(long id, long chapterId, String url, int width, int height) {
        this.id = id;
        this.chapterId = chapterId;
        this.url = url;
        this.width = width;
        this.height = height;
    }
    @Generated(hash = 1602548376)
    public Picture() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getWidth() {
        return this.width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public ObservableFloat getAspect() {
        if (width != 0 && aspect.get() == 1) {
            aspect.set((float) width / height);
        }
        return aspect;
    }

    public void setAspect(ObservableFloat aspect) {
        this.aspect = aspect;
    }
    public long getChapterId() {
        return this.chapterId;
    }
    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Picture picture = (Picture) o;

        return id == picture.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
