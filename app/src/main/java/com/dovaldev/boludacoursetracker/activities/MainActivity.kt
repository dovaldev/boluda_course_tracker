package com.dovaldev.boludacoursetracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.dovaltools.dovaldev
import com.dovaldev.boludacoursetracker.dovaltools.goToActivity
import com.dovaldev.boludacoursetracker.dovaltools.loadURL
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // go to list activity
        btnGoToCourses.setOnClickListener { goToActivity<CourseListActivity>() }
        // go to developer web
        tvDeveloped.setOnClickListener { loadURL(dovaldev) }
    }
}