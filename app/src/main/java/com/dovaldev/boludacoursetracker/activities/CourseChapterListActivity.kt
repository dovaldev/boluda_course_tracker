package com.dovaldev.boludacoursetracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
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
import com.dovaldev.boludacoursetracker.database.tools.DatabaseFunctions
import com.dovaldev.boludacoursetracker.dovaltools.*
import kotlinx.android.synthetic.main.activity_course_chapter_list.*

/* This activity show the chapter list of each course */
class CourseChapterListActivity : AppCompatActivity() {

    private lateinit var courseViewModel: CoursesViewModel
    private lateinit var course2load: String
    private lateinit var courseTitle2load: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_chapter_list)

        // load course selected
        val urlCourse: String? = intent.getStringExtra(course)
        urlCourse?.let { course2load = it }

        // load course selected
        val titleCourse: String? = intent.getStringExtra(courseTitle)
        titleCourse?.let { courseTitle2load = it }

        // set title
        tvCourseTitle.text = courseTitle2load


        // load viewmodel
        courseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        // load the list of all courses
        loadDownloadedCourses()

    }


    // load the courses into the adapter
    private fun loadDownloadedCourses() {
        val recyclerView = findViewById<RecyclerView>(R.id.courseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CourseChaptersListAdapter(this, object : CoursesListAdapterListener {
            override fun onClick(coursesEntity: CoursesEntity, position: Int) {
                loadURL(coursesEntity.captituloCursoURL)
            }

            // the course will be setted as you have selected watched option
            override fun onClickWatched(coursesEntity: CoursesEntity, position: Int) {
                DatabaseFunctions(this@CourseChapterListActivity).onClickChapterView(coursesEntity)
            }

            // I'm getting the default listener and this function dont work in this adapter
            override fun onClickFav(coursesEntity: CoursesEntity, position: Int) {}

            override fun onClickTime(coursesEntity: CoursesEntity, position: Int) {
                DatabaseFunctions(this@CourseChapterListActivity).onClickSaveTime(coursesEntity, this@CourseChapterListActivity)
            }


        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // load viewmodel
        courseViewModel.getCourseChapterList()?.observe(this, Observer<List<CoursesEntity>> { course ->
            // Update the cached copy of the words in the adapter.
            course?.let {
                adapter.setCourses(it)
            }
        })

        courseViewModel.getCourseChapter(course2load)
    }


}

