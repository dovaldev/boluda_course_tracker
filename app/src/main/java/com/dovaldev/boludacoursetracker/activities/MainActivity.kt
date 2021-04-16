package com.dovaldev.boludacoursetracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.dovaltools.goToActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGoToCourses.setOnClickListener { goToActivity<CourseListActivity>() }
    }
}