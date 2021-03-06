package com.jash.comicdemo.activities

import android.databinding.DataBindingUtil
import android.databinding.ObservableInt
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.MenuItem

import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.jash.comicdemo.BaseApplication
import com.jash.comicdemo.R
import com.jash.comicdemo.BR
import com.jash.comicdemo.databinding.PictureBinding
import com.jash.comicdemo.entities.Chapter
import com.jash.comicdemo.entities.Picture
import com.jash.comicdemo.entities.PictureDao
import com.jash.comicdemo.utils.CommentAdapter
import com.jash.comicdemo.utils.CustomScaleType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

import java.util.ArrayList

class PictureActivity : AppCompatActivity() {
    private var binding: PictureBinding? = null
    private var application: BaseApplication? = null
    private var adapter: CommentAdapter<Picture>? = null
    private lateinit var subscribe: Disposable
    private val sources = ArrayList<DataSource<*>>()
    private var pictures: MutableList<Picture>? = null
    private val count = ObservableInt(0);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<PictureBinding>(this, R.layout.activity_picture)
        application = getApplication() as BaseApplication
        val chapterId = intent.getLongExtra("chapterId", 0)
        val chapter = application!!.session!!.chapterDao.load(chapterId)
        adapter = CommentAdapter(this, ArrayList<Picture>(), R.layout.item_picture, BR.pic)
        pictures = application!!.session!!
                .pictureDao
                .queryBuilder()
                .where(PictureDao.Properties.ChapterId.eq(chapterId))
                .list()
//                .rx()
//                .oneByOne()
//                .onBackpressureBuffer()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ this.addPicture(it) }, { it.printStackTrace() })
        pictures!!.forEach { this.addPicture(it) }
        subscribe = application!!.subject!!
                .ofType(Picture::class.java)
                .filter { it.chapterId == chapterId }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { this.addPicture(it) }
        binding!!.pictureList.adapter = adapter
        binding!!.chapter = chapter
        binding!!.count = count
        if (chapter!!.picCount == 0 || pictures!!.size < chapter.picCount - 1) {
            loadPicture()
        }
        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadPicture() {
        if (subscribe.isDisposed) {
            return
        }
        val b = application!!.service!!.getPicture(binding!!.chapter!!.comicId, binding!!.chapter!!.id, pictures!!.size + 1, 2)
                .map { it.data }
                .doOnNext { application!!.subject!!.onNext(it) }
                .flatMap { Observable.fromIterable(it.pictures) }
                .doOnNext { application!!.subject!!.onNext(it) }
                .subscribe({ }, {
                    loadPicture()
                    it.printStackTrace()
                }, { if (pictures!!.size < binding!!.chapter!!.picCount - 1) loadPicture()})
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!subscribe.isDisposed) {
            subscribe.dispose()
        }
        sources.filterNot { it.isClosed }
                .forEach { it.close() }
        application!!.subject!!.onNext(binding!!.chapter!!)
    }

    private fun addPicture(pic: Picture) {
        if (!pictures!!.contains(pic)){
            pictures!!.add(pic)
        }
        val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(pic.url))
                .setRequestPriority(Priority.LOW)
                .build()
        sources.add(Fresco.getImagePipeline().prefetchToDiskCache(request, null))
        if (pic.aspect > 1) {
            val clone = pic.clone()
            pic.scaleType = CustomScaleType.CLIP_START
            clone.scaleType = CustomScaleType.CLIP_END
            adapter!!.add(pic)
            adapter!!.add(clone)
        } else {
            if (!adapter!!.contains(pic)) {
                adapter!!.add(pic)
            }
        }
        count.set(adapter!!.itemCount)
    }
}
