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
import com.dovaldev.boludacoursetracker.database.tools.databaseFunctions
import com.dovaldev.boludacoursetracker.database.tools.databaseInstaller
import com.dovaldev.boludacoursetracker.dovaltools.*
import com.dovaldev.boludacoursetracker.onlinegetter.JsoupGetter

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

    private fun loadDownloadedCourses(fav: Boolean) {
        val recyclerView = findViewById<RecyclerView>(R.id.courseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CoursesListAdapter(this, object : CoursesListAdapterListener {
            override fun onClick(coursesEntity: CoursesEntity, position: Int) {
                Log.i("load", "course-> ${coursesEntity.nombreCurso}")
                goToActivity<CourseChapterListActivity> { putExtra(course, coursesEntity.URLCurso) }
            }

            override fun onClickWatched(coursesEntity: CoursesEntity, position: Int) {
                doAsync { databaseFunctions(this@CourseListActivity).setCursoVisto(coursesEntity) }

            }

            override fun onClickFav(coursesEntity: CoursesEntity, position: Int) {
                doAsync { databaseFunctions(this@CourseListActivity).setCursoFav(coursesEntity) }
            }


        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // load viewmodel

        courseViewModel.getCoursesList(fav)?.observe(this, Observer { course ->
            // Update the cached copy of the words in the adapter.
            course?.let { adapter.setCourses(it) }

        })

        courseViewModel.get("")

    }

    private fun downloadOnlineCourses() {
        // get the favourites and watched chapters
        val dAfterInstall = dialogDownloadingCourses("Obteniendo", "Información de la base de datos, favoritos y capítulos vistos...")
        doAsync {
            databaseInstaller(this@CourseListActivity).afterInstall()
            uiThread {
                // cancel dialog doing
                dAfterInstall.cancel()
                // get the list and install the courses
                val dInstalling = dialogDownloadingCourses()
                dInstalling.show()
                doAsync {
                    // delete courses
                    courseViewModel.deleteAll()
                    // get and intall courses
                    JsoupGetter(this@CourseListActivity).getCourseList(web_boluda)
                    uiThread {
                        // cancel dialog install courses
                        dInstalling.cancel()
                        // update the list with last viewed courses and favourites
                        val dBeforeInstall = dialogDownloadingCourses("Actualizando", "Información de la base de datos, favoritos y capítulos vistos...")
                        doAsync {
                            databaseInstaller(this@CourseListActivity).beforeInstall()
                            uiThread {
                                dBeforeInstall.cancel()
                                showToast("Todos los datos se han actualizado correctamente...")
                            }
                        }

                    }
                }
            }
        }
    }


    //=================================== MENU ==================================================================
    //      MENU
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
            R.id.action_fav -> {
                fav = !fav
                loadDownloadedCourses(fav)
                true
            }

            R.id.action_intranet -> {
                loadURL(web_boluda_intranet)
                true
            }
            R.id.action_reload_courses -> {
                downloadOnlineCourses()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

}