package com.dovaldev.boludacoursetracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.database.*
import com.dovaldev.boludacoursetracker.database.base.CourseChaptersListAdapter
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.base.CoursesListAdapterListener
import com.dovaldev.boludacoursetracker.database.base.CoursesViewModel
import com.dovaldev.boludacoursetracker.database.tools.databaseFunctions
import com.dovaldev.boludacoursetracker.dovaltools.*

class CourseChapterListActivity : AppCompatActivity() {

    private lateinit var courseViewModel: CoursesViewModel
    private lateinit var course2load: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        // load course selected
        val list: String? = intent.getStringExtra(course)
        list?.let { course2load = it }

        // load viewmodel
        courseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        // load the list of all courses
        loadDownloadedCourses()

    }

    private fun loadDownloadedCourses() {
        val recyclerView = findViewById<RecyclerView>(R.id.courseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CourseChaptersListAdapter(this, object : CoursesListAdapterListener {
            override fun onClick(coursesEntity: CoursesEntity, position: Int) {
                loadURL(coursesEntity.captituloCursoURL)
            }

            override fun onClickWatched(coursesEntity: CoursesEntity, position: Int) {
                databaseFunctions(this@CourseChapterListActivity).onClickChapterView(coursesEntity)
            }


            override fun onClickFav(coursesEntity: CoursesEntity, position: Int) {}


        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // load viewmodel

        courseViewModel.getCourseChapterList()?.observe(this, Observer { course ->
            // Update the cached copy of the words in the adapter.
            course?.let { adapter.setCourses(it) }

        })

        courseViewModel.getCourseChapter(course2load)

    }

}