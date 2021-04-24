package com.dovaldev.boludacoursetracker.database.tools

import android.app.Activity
import android.content.Context
import android.util.Log
import com.dovaldev.boludacoursetracker.database.backup.SavedCoursesEntity
import com.dovaldev.boludacoursetracker.database.backup.SavedCoursesRoomDatabase
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.base.CoursesRoomDatabase
import com.dovaldev.boludacoursetracker.database.base.CoursesViewModel
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync
import com.dovaldev.boludacoursetracker.dovaltools.anko.uiThread
import com.dovaldev.boludacoursetracker.dovaltools.dialogInstallCourses
import com.dovaldev.boludacoursetracker.dovaltools.notNull

class databaseInstaller(var a: Activity) {

    private val coursesDao = CoursesRoomDatabase.getDatabase(a).coursesDao()
    private val savedCoursesDao = SavedCoursesRoomDatabase.getDatabase(a).coursesDao()


    // check if database is empty and show a dialog to install
    fun checkIfIsEmpty(courseViewModel: CoursesViewModel) {
        doAsync {
            // get if there are some course installed
            val courses: Long = coursesDao.getFirstRow()
            //Log.i("courses", "---> $courses")
            uiThread {
                if (courses == 0L) {
                    a.notNull {
                        a.dialogInstallCourses {
                            Downloader(a, courseViewModel).downloadOnlineCoursesV2()
                        }
                    }
                }
            }
        }
    }


    // before install
    fun beforeInstall() {
        createBackupViewedAndFav()
    }


    // create a database viewed backup
    private fun createBackupViewedAndFav() {
        // get the actual list of courses
        val actualList = coursesDao.all_nolive
        // delete the backup
        savedCoursesDao.deleteAll()
        // create a new backup
        for (item in actualList) {
            if (item.capituloVisto || item.cursoFavorito) {
                savedCoursesDao.insert(
                    SavedCoursesEntity(
                        item.captituloCursoURL,
                        item.capituloVisto,
                        item.cursoFavorito
                    )
                )
            }
        }
    }

    // update the favs and chapters viewed
    fun afterInstall() {
        updateFromBackupViewedAndFav()
    }

    // update the database with saved values
    private fun updateFromBackupViewedAndFav() {
        // get saved information
        val backupList = savedCoursesDao.all_cursos
        // update the items
        for (savedItem in backupList) {
            val curso = coursesDao.getChapter(savedItem.captituloCursoURL)
            if (curso.isNotEmpty()) {

                val chapterEntity = curso.get(0)
                chapterEntity.capituloVisto = savedItem.capituloVisto
                chapterEntity.cursoFavorito = savedItem.cursoFavorito
                coursesDao.update(chapterEntity)
            }
        }
    }

    // find equals course and update image and indfo
    fun installImagesAndInfo(basicEntity: CoursesEntity) {
        val curso = coursesDao.getUrlCursoNoLive(basicEntity.URLCurso)
        curso.forEach {
            it.imgCurso = basicEntity.imgCurso
            it.infoCurso = basicEntity.infoCurso
            directUpdateInDatabase(it)
        }
    }


    // install the database while is beeing downloaded
    fun directInstallInDatabase(course: CoursesEntity) {
        coursesDao.insert(course)
        //Log.i("installing", "${course.nombreCurso} | ${course.capituloCurso}")
    }

    // update the database while is beein downloaded
    fun directUpdateInDatabase(course: CoursesEntity) {
        //Log.i("updating-course", "${course.nombreCurso} -  ${course.imgCurso}")
        coursesDao.update(course)
    }
}