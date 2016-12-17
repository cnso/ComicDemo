package com.jash.comicdemo.utils;

import android.graphics.Matrix;
import android.graphics.Rect;

import com.facebook.drawee.drawable.ScalingUtils;

public class CustomScaleType {
    public static final ScalingUtils.ScaleType CLIP_END = new ScalingUtils.AbstractScaleType() {
        @Override
        public void getTransformImpl(Matrix outTransform, Rect parentRect, int childWidth, int childHeight, float focusX, float focusY, float scaleX, float scaleY) {
            float scale = Math.max(scaleX, scaleY);
            float dx = parentRect.left;
            float dy = parentRect.top;
            outTransform.setScale(scale, scale);
            outTransform.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        }
    };
    public static final ScalingUtils.ScaleType CLIP_START = new ScalingUtils.AbstractScaleType() {
        @Override
        public void getTransformImpl(Matrix outTransform, Rect parentRect, int childWidth, int childHeight, float focusX, float focusY, float scaleX, float scaleY) {
            float scale = Math.max(scaleX, scaleY);
            float dx = parentRect.left + (parentRect.width() - childWidth * scale);
            float dy = parentRect.top + (parentRect.height() - childHeight * scale);
            outTransform.setScale(scale, scale);
            outTransform.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        }
    };
}
