package com.jash.comicdemo.activities

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.jash.comicdemo.BR
import com.jash.comicdemo.BaseApplication
import com.jash.comicdemo.R
import com.jash.comicdemo.databinding.SearchBinding
import com.jash.comicdemo.entities.Comic
import com.jash.comicdemo.utils.CommentAdapter
import com.jash.comicdemo.utils.Parser

import org.jsoup.nodes.Element

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.ArrayList

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SearchActivity : AppCompatActivity() {
    private var adapter: CommentAdapter<Comic>? = null
    private var application: BaseApplication? = null
    private var keyword: String? = null
    private var binding: SearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<SearchBinding>(this, R.layout.activity_search)
        setSupportActionBar(binding!!.toolbar)
        val extra = intent.getStringExtra("keyword")
        title = extra
        adapter = CommentAdapter(this, ArrayList<Comic>(), R.layout.item_home, BR.comic)
        binding!!.searchGrid.adapter = adapter
        application = getApplication() as BaseApplication
        this.keyword = extra

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        application!!.service!!
                .searchComic(1, keyword)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {

                    if (it.code != 200) {
                        binding!!.loading.text = it.message
                    } else {
                        binding!!.loading.visibility = View.GONE
                    }
                }
                .flatMap({ Observable.from(it.data) })
                .doOnNext({ application!!.subject!!.onNext(it) })
                .filter({ comic -> !adapter!!.contains(comic) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adapter!!.add(it) }, { throwable ->
                    throwable.printStackTrace()
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                })

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
