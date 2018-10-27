package com.jash.comicdemo

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import android.transition.ChangeBounds
import android.util.Log
import android.widget.Toast

import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.GsonBuilder
import com.jash.comicdemo.entities.Chapter
import com.jash.comicdemo.entities.Comic
import com.jash.comicdemo.entities.DaoMaster
import com.jash.comicdemo.entities.DaoSession
import com.jash.comicdemo.entities.Picture
import com.jash.comicdemo.utils.ComicService
import com.jash.comicdemo.utils.update
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BaseApplication : Application() {
    var service: ComicService? = null
    var session: DaoSession? = null
    var subject: Subject<Any>? = null

    override fun onCreate() {
        super.onCreate()
        val gson = GsonBuilder()
                .setDateFormat("yyyy/M/d HH:mm:ss")
                .create()
        val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build()
        service = Retrofit.Builder()
                .baseUrl("http://jash.us-2.evennode.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(ComicService::class.java)
        Fresco.initialize(this)
        session = DaoMaster.newDevSession(this, "comic-db")
        subject = PublishSubject.create<Any>()
        subject!!.ofType(Comic::class.java)
                .observeOn(Schedulers.io())
                .doOnNext { it.update(session!!.comicDao.load(it.id)) }
                .subscribe({ session!!.comicDao.insertOrReplace(it) }, { it.printStackTrace() })
        subject!!.ofType(Chapter::class.java)
                .observeOn(Schedulers.io())
                .doOnNext { it.update(session!!.chapterDao.load(it.id)) }
                .subscribe({ session!!.chapterDao.insertOrReplace(it) }, { it.printStackTrace() })
        subject!!.ofType(Picture::class.java)
                .observeOn(Schedulers.io())
                .doOnNext { it.update(session!!.pictureDao.load(it.id)) }
                .subscribe({ session!!.pictureDao.insertOrReplace(it) }, { it.printStackTrace() })
    }

    companion object {
        init {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }
    }
}
