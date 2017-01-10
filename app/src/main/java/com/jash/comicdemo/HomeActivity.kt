package com.jash.comicdemo

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.jash.comicdemo.activities.SearchActivity
import com.jash.comicdemo.databinding.HomeBinding
import com.jash.comicdemo.entities.Comic
import com.jash.comicdemo.entities.ComicDao
import com.jash.comicdemo.utils.CommentAdapter

import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class HomeActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var application: BaseApplication? = null
    private var subscribe: Subscription? = null
    private var binding: HomeBinding? = null
    private var adapter: CommentAdapter<Comic>? = null
    private var item: MenuItem? = null
    private var isNight: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<HomeBinding>(this, R.layout.activity_home)
        application = getApplication() as BaseApplication
        val list = application!!.session!!
                .comicDao
                .queryBuilder()
                .orderDesc(ComicDao.Properties.UpdateTime)
                .limit(24)
                .list()
        isNight = savedInstanceState is Bundle &&  savedInstanceState.getInt("appcompat:local_night_mode") == AppCompatDelegate.MODE_NIGHT_YES
        adapter = CommentAdapter(this, list, R.layout.item_home, BR.comic)
        setSupportActionBar(binding!!.toolbar)
        subscribe = application!!.subject!!
                .ofType(Comic::class.java)
                .filter { comic -> !adapter!!.contains(comic) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adapter!!.add(it) }, { it.printStackTrace() })
        binding!!.homeSwipe.setOnRefreshListener( { this.refresh() })
        binding!!.homeGrid.adapter = adapter
        if (adapter!!.itemCount == 0) {
            binding!!.homeSwipe.isRefreshing = true
            refresh()
        }
    }

    private fun refresh() {
        adapter!!.clear()
        application!!.service!!
                .home
                .map({ it.data })
                .flatMap( { Observable.from(it) })
                .doOnNext({ application!!.subject!!.onNext(it) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ comic -> }, { throwable ->
                    binding!!.homeSwipe.isRefreshing = false
                    throwable.printStackTrace()
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }, { binding!!.homeSwipe.isRefreshing = false })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        item = menu!!.findItem(R.id.search)
        val search = MenuItemCompat.getActionView(item) as SearchView
        search.isSubmitButtonEnabled = true
        search.setOnQueryTextListener(this)
        menu.findItem(R.id.night_mode).isChecked = isNight
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.night_mode -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!subscribe!!.isUnsubscribed) {
            subscribe!!.unsubscribe()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val intent = Intent(this, SearchActivity::class.java)
//        intent.putExtra("keyword", "金田一")
        intent.putExtra("keyword", query)
        startActivity(intent)
        MenuItemCompat.collapseActionView(item)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}
