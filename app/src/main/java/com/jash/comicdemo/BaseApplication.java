package com.jash.comicdemo;

import android.app.Application;
import android.transition.ChangeBounds;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jash.comicdemo.entities.Chapter;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.entities.DaoMaster;
import com.jash.comicdemo.entities.DaoSession;
import com.jash.comicdemo.entities.Picture;
import com.jash.comicdemo.utils.ComicService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class BaseApplication extends Application {
    private ComicService service;
    private DaoSession session;
    private Subject<Object, Object> subject;

    @Override
    public void onCreate() {
        super.onCreate();
        service = new Retrofit.Builder()
                .baseUrl("http://kukudm.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(ComicService.class);
        Fresco.initialize(this);
        session = DaoMaster.newDevSession(this, "comic-db");
        subject = PublishSubject.create();
        subject.ofType(Comic.class)
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(session.getComicDao()::insertOrReplace, Throwable::printStackTrace);
        subject.ofType(Chapter.class)
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(session.getChapterDao()::insertOrReplace, Throwable::printStackTrace);
        subject.ofType(Picture.class)
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(session.getPictureDao()::insertOrReplace, Throwable::printStackTrace);
    }

    public ComicService getService() {
        return service;
    }

    public DaoSession getSession() {
        return session;
    }

    public Subject<Object, Object> getSubject() {
        return subject;
    }
}
