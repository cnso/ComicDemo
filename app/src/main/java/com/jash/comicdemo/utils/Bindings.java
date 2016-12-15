package com.jash.comicdemo.utils;

import android.app.Application;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Animatable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jash.comicdemo.BaseApplication;
import com.jash.comicdemo.entities.Picture;

import rx.subjects.Subject;

public class Bindings {
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
//    @BindingAdapter(value = {"imageURI", "picture"}, requireAll = false)
//    public static void loadImage(DraweeView view, String uri, Picture picture) {
//        Subject<Object, Object> subject = ((BaseApplication) view.getContext().getApplicationContext()).getSubject();
//        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setUri(uri)
//                .setControllerListener(new BaseControllerListener<ImageInfo>(){
//                    @Override
//                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
//                        if (picture != null && imageInfo.getQualityInfo().isOfFullQuality()) {
//                            picture.setWidth(imageInfo.getWidth());
//                            picture.setHeight(imageInfo.getHeight());
//                            picture.getAspect().set((float)imageInfo.getWidth() / imageInfo.getHeight());
//                            subject.onNext(picture);
//                        }
//                    }
//                })
//                .setOldController(view.getController())
//                .build();
//        view.setController(controller);
//    }
}
