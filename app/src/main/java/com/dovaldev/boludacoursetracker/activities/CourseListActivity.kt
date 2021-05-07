package com.dovaldev.boludacoursetracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.dovaldev.boludacoursetracker.database.tools.DatabaseFunctions
import com.dovaldev.boludacoursetracker.database.tools.databaseInstaller
import com.dovaldev.boludacoursetracker.dovaltools.*
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync
import kotlinx.android.synthetic.main.activity_course_chapter_list.*
import kotlinx.android.synthetic.main.activity_course_list.*

/* This activity show the courses list */
class CourseListActivity : AppCompatActivity() {

    private lateinit var courseViewModel: CoursesViewModel
    private var fav: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        // load viewmodel
        courseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        // check if the courses are installed
        databaseInstaller(this@CourseListActivity).checkIfIsEmpty(courseViewModel)

        // load the list of all courses
        loadDownloadedCourses(fav)

    }

    // the courses are loaded into the adapter
    private fun loadDownloadedCourses(fav: Boolean) {
        // set favourite or full list message
        when (fav) {
            true -> {
                tvListTitle.text = getString(R.string.course_list_lista_fav)
                tvListTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fav_yes, 0, 0, 0)
            }
            false -> {
                tvListTitle.text = getString(R.string.course_list_lista_completa)
                tvListTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fav_no, 0, 0, 0)
            }
        }

        // load the recycler and show in adapter
        val recyclerView = findViewById<RecyclerView>(R.id.courseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CoursesListAdapter(this, object : CoursesListAdapterListener {
            override fun onClick(coursesEntity: CoursesEntity, position: Int) {
                //Log.i("load", "course-> ${coursesEntity.nombreCurso}")
                startActivity(intentFor<CourseChapterListActivity>(
                        course to coursesEntity.URLCurso,
                        courseTitle to coursesEntity.nombreCurso
                ))
            }

            // set the course watched or not
            override fun onClickWatched(coursesEntity: CoursesEntity, position: Int) {
                doAsync { DatabaseFunctions(this@CourseListActivity).setCursoVisto(coursesEntity) }

            }

            // add the course to favs
            override fun onClickFav(coursesEntity: CoursesEntity, position: Int) {
                doAsync { DatabaseFunctions(this@CourseListActivity).setCursoFav(coursesEntity) }
            }

            override fun onClickTime(coursesEntity: CoursesEntity, position: Int) {
                // not implemented
            }


        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // load viewmodel

        courseViewModel.getCoursesList(fav)?.observe(this, Observer<List<CoursesEntity>> { course ->
            // Update the cached copy of the words in the adapter.
            course?.let { adapter.setCourses(course) }

        })

        courseViewModel.get("")

    }

    // get the advanced search status
    private fun advancedSearch(): Boolean = Prefs(this).advanced_search


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

            editext.setOnLongClickListener {
                if(Prefs(this).advanced_search_switch()){
                    editext.hint = getString(R.string.action_bar_search_chapter)
                } else {
                    editext.hint = getString(R.string.action_bar_search)
                }
                true
            }

            when(advancedSearch()){
                true -> editext.hint = getString(R.string.action_bar_search_chapter)
                false -> editext.hint = getString(R.string.action_bar_search)
            }


            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    //Log.i("onQueryTextSubmit", query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //Log.i("onQueryTextChange", newText?:"no text")
                    if (newText!!.isNotEmpty()) {
                        courseViewModel.get(newText, advancedSearch())
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
            R.id.action_ir_a -> {
                dialogGoTo()
                true
            }

            // install courses with version 2 (more fast-testing)
            R.id.action_reload_courses_v2 -> {
                dialogInstallCourses {
                    Downloader(this@CourseListActivity, courseViewModel).downloadOnlineCoursesV2()
                }

                true
            }

            // install courses with version 1 (more slow)
            R.id.action_reload_courses -> {
                dialogInstallCourses {
                    Downloader(this@CourseListActivity, courseViewModel).downloadOnlineCourses()
                }

                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

}