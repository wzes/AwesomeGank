package com.xuantang.awesomegank.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.xuantang.awesomegank.R
import com.xuantang.basemodule.extentions.setDarkStatusIcon
import com.xuantang.basemodule.extentions.setStatusTransAndDarkIcon
import com.xuantang.awesomegank.fragments.fuli.FuLiFragment
import com.xuantang.awesomegank.fragments.list.ArticleListFragment

@Route(path = "/fuli/")
class FuliActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusTransAndDarkIcon(Color.TRANSPARENT)
        setContentView(R.layout.activity_fuli)
        supportFragmentManager.beginTransaction()
            .add(R.id.fuli_fragment, FuLiFragment())
            .commit()
    }
}