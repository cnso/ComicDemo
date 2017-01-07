package com.jash.comicdemo.utils;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class Bindings {
    public static final String TAG = Bindings.class.getSimpleName();
    @BindingAdapter(value = {"imageBlurURI", "blurRadius"}, requireAll = false)
    public static void imageBlurURI(DraweeView view, String uri, float radius) {
        radius = Math.max(radius, 0f);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setPostprocessor(new BlurPostprocessor(view.getContext(), radius))
                .build();
        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .build();
        view.setController(controller);
    }

    @BindingAdapter("hasProgress")
    public static void hasProgress(GenericDraweeView view, boolean flag) {
        GenericDraweeHierarchy hierarchy = view.getHierarchy();
        if (flag) {
            hierarchy.setProgressBarImage(new ProgressBarDrawable());
        } else {
            hierarchy.setProgressBarImage(null);
        }
    }

    @BindingAdapter("actualImageScaleType")
    public static void setScaleType(GenericDraweeView view, ScalingUtils.ScaleType scaleType) {
        if (scaleType != null) {
            view.getHierarchy().setActualImageScaleType(scaleType);
        } else {
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        }
    }

    @InverseBindingAdapter(attribute = "topPosition")
    public static int getRecyclerTopPosition(RecyclerView view) {
        return view.getChildAdapterPosition(view.getChildAt(0));
    }

    @BindingAdapter("topPositionAttrChanged")
    public static void setListener(RecyclerView view, final InverseBindingListener listener) {
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0) {
                    listener.onChange();
                }
            }
        });
    }

    @BindingAdapter("topPosition")
    public static void setTopPosition(RecyclerView view, int topPosition) {
        if (view.getChildAdapterPosition(view.getChildAt(0)) != topPosition) {
            view.scrollToPosition(topPosition);
        }
    }
}
