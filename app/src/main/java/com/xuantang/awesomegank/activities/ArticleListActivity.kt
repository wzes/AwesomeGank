package com.xuantang.awesomegank.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.xuantang.awesomegank.R
import com.xuantang.basemodule.extentions.setDarkStatusIcon
import com.xuantang.awesomegank.fragments.list.ArticleListFragment

@Route(path = "/article/list")
class ArticleListActivity : AppCompatActivity() {

    private val category by lazy(LazyThreadSafetyMode.NONE) {
        intent.getStringExtra("category")
    }

    private val title by lazy(LazyThreadSafetyMode.NONE) {
        intent.getStringExtra("title")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusIcon(true)
        setContentView(R.layout.activity_article_list)
        supportFragmentManager.beginTransaction()
            .add(R.id.article_fragment, ArticleListFragment(category))
            .commit()
    }
}