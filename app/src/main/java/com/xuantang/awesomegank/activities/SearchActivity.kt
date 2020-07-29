package com.xuantang.awesomegank.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.extentions.setDarkStatusIcon

@Route(path = "/search/")
class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusIcon(true)
        setContentView(R.layout.activity_search)
    }
}