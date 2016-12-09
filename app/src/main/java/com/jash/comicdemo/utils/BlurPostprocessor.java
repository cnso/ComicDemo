package com.jash.comicdemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.facebook.imagepipeline.request.BasePostprocessor;

public class BlurPostprocessor extends BasePostprocessor {
    private Context context;
    private float radius;

    public BlurPostprocessor(Context context, float radius) {
        this.context = context;
        this.radius = radius;
    }

    @Override
    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, sourceBitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(destBitmap);

        rs.destroy();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
