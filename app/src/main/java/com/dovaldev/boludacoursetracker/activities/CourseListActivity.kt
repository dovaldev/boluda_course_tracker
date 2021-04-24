package com.dovaldev.boludacoursetracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.database.*
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.base.CoursesListAdapter
import com.dovaldev.boludacoursetracker.database.base.CoursesListAdapterListener
import com.dovaldev.boludacoursetracker.database.base.CoursesViewModel
import com.dovaldev.boludacoursetracker.database.tools.Downloader
import com.dovaldev.boludacoursetracker.database.tools.databaseFunctions
import com.dovaldev.boludacoursetracker.database.tools.databaseInstaller
import com.dovaldev.boludacoursetracker.dovaltools.*
import com.dovaldev.boludacoursetracker.onlinegetter.JsoupGetter

/* This activity show the courses list */
class CourseListActivity : AppCompatActivity() {

    private lateinit var courseViewModel: CoursesViewModel
    private var fav: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        // load viewmodel
        courseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        // load the list of all courses
        loadDownloadedCourses(fav)

    }

    // the courses are loaded into the adapter
    private fun loadDownloadedCourses(fav: Boolean) {
        val recyclerView = findViewById<RecyclerView>(R.id.courseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CoursesListAdapter(this, object : CoursesListAdapterListener {
            override fun onClick(coursesEntity: CoursesEntity, position: Int) {
                Log.i("load", "course-> ${coursesEntity.nombreCurso}")
                startActivity(intentFor<CourseChapterListActivity>(
                    course to coursesEntity.URLCurso,
                    courseTitle to coursesEntity.nombreCurso
                ))
            }

            // set the course watched or not
            override fun onClickWatched(coursesEntity: CoursesEntity, position: Int) {
                doAsync { databaseFunctions(this@CourseListActivity).setCursoVisto(coursesEntity) }

            }
            // add the course to favs
            override fun onClickFav(coursesEntity: CoursesEntity, position: Int) {
                doAsync { databaseFunctions(this@CourseListActivity).setCursoFav(coursesEntity) }
            }


        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // load viewmodel

        courseViewModel.getCoursesList(fav)?.observe(this, { course ->
            // Update the cached copy of the words in the adapter.
            course?.let { adapter.setCourses(it) }

        })

        courseViewModel.get("")

    }


    // =================================== MENU ==================================================================
    // MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_courses, menu)
        // search item
        val searchItem = menu.findItem(R.id.action_course_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            val editext = searchView.findViewById<EditText>(R.id.search_src_text)

            editext.hint = "Buscar un curso"

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.i("onQueryTextChange", newText?:"no text")
                    if (newText!!.isNotEmpty()) {
                        courseViewModel.get(newText)
                    } else {
                        courseViewModel.get("")
                    }
                    return true
                }

            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            // on click in fav button
            R.id.action_fav -> {
                fav = !fav
                loadDownloadedCourses(fav)
                true
            }
            // load intranet in explorer
            R.id.action_intranet -> {
                loadURL(web_boluda_intranet)
                true
            }

            // install courses with version 1 (more slow)
            R.id.action_reload_courses -> {
                Downloader(this@CourseListActivity, courseViewModel).downloadOnlineCourses()
                true
            }

            // install courses with version 2 (more fast-testing)
            R.id.action_reload_courses_v2 -> {
                Downloader(this@CourseListActivity, courseViewModel).downloadOnlineCoursesV2()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}