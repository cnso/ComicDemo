package com.jash.comicdemo.entities;

import android.databinding.ObservableFloat;

import com.facebook.drawee.drawable.ScalingUtils;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Picture  implements Cloneable {
    @Id
    @SerializedName("id")
    private long id;
    @SerializedName("chapterId")
    private long chapterId;
    @SerializedName("img")
    private String url;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @Transient
    private ScalingUtils.ScaleType scaleType;
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

    public float getAspect() {
        return (float) width / height;
    }

    public long getChapterId() {
        return this.chapterId;
    }
    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public ScalingUtils.ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Picture picture = (Picture) o;

        if (id != picture.id) return false;
        return scaleType != null ? scaleType.equals(picture.scaleType) : picture.scaleType == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (scaleType != null ? scaleType.hashCode() : 0);
        return result;
    }

    @Override
    public Picture clone() {
        try {
            return (Picture) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
