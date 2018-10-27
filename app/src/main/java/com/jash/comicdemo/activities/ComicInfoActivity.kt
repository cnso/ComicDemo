package com.jash.comicdemo.activities

import android.content.ContentUris
import android.databinding.DataBindingUtil
import android.support.v4.app.ActivityCompat
import android.support.v4.text.TextUtilsCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast

import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.jash.comicdemo.BR
import com.jash.comicdemo.BaseApplication
import com.jash.comicdemo.R
import com.jash.comicdemo.databinding.InfoBinding
import com.jash.comicdemo.entities.Chapter
import com.jash.comicdemo.entities.ChapterDao
import com.jash.comicdemo.entities.Comic
import com.jash.comicdemo.utils.CommentAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


class ComicInfoActivity : AppCompatActivity() {
    private var binding: InfoBinding? = null
    private var application: BaseApplication? = null
    private lateinit var comic_subscribe: Disposable
    private var comic: Comic? = null
    private var adapter: CommentAdapter<Chapter>? = null
    private lateinit var chapter_subscribe: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = getApplication() as BaseApplication
        binding = DataBindingUtil.setContentView<InfoBinding>(this, R.layout.activity_comic_info)
        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        var comicId = intent.getLongExtra("comicId", -1)
        if (comicId == -1L) {
            comicId = ContentUris.parseId(intent.data)
        }
        comic = application!!.session!!.comicDao.load(comicId)
        binding!!.comic = comic
        comic_subscribe = application!!.subject!!
                .ofType(Comic::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding!!.comic = it }
        val chapters = application!!.session!!
                .chapterDao
                .queryBuilder()
                .where(ChapterDao.Properties.ComicId.eq(comicId))
                .list()
        adapter = CommentAdapter(this, chapters, R.layout.item_chapter, BR.chapter)
        binding!!.infoList.adapter = adapter
        binding!!.infoSwipe.setOnRefreshListener { this.loadComic() }
        chapter_subscribe = application!!.subject!!.ofType(Chapter::class.java)
                .filter { chapter -> chapter.comicId == comic!!.id }
                .filter { chapter -> !adapter!!.contains(chapter) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adapter!!.add(it) }, { it.printStackTrace() })
        if (TextUtils.isEmpty(comic!!.info)) {
            binding!!.infoSwipe.isRefreshing = true
            loadComic()
        }
    }

    private fun loadComic() {
        val a = application!!.service!!
                .getComic(comic!!.id)
                .map { it.data }
                .doOnNext { application!!.subject!!.onNext(it) }
                .map { it.chapters }
                .flatMap { Observable.fromIterable(it) }
                .doOnNext { application!!.subject!!.onNext(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { t ->
                    binding!!.infoSwipe.isRefreshing = false
                    t.printStackTrace()
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                }, { binding!!.infoSwipe.isRefreshing = false })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!comic_subscribe.isDisposed) {
            comic_subscribe.dispose()
        }
        if (!chapter_subscribe.isDisposed) {
            chapter_subscribe.dispose()
        }
    }
}
