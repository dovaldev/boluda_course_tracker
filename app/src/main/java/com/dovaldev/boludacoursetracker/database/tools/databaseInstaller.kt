package com.dovaldev.boludacoursetracker.database.tools

import android.content.Context
import android.util.Log
import com.dovaldev.boludacoursetracker.database.backup.SavedCoursesEntity
import com.dovaldev.boludacoursetracker.database.backup.SavedCoursesRoomDatabase
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.base.CoursesRoomDatabase
import com.dovaldev.boludacoursetracker.dovaltools.doAsync

class databaseInstaller(var c: Context) {

    private val coursesDao = CoursesRoomDatabase.getDatabase(c).coursesDao()
    private val savedCoursesDao = SavedCoursesRoomDatabase.getDatabase(c).coursesDao()

    // install database after download
    /* Not used function...
    private fun installInDatabase(list: List<CoursesEntity>) {
        if (list.isNotEmpty()) {
            for (item in list) {
                if (installIfNotExist(item)) {
                    coursesDao.insert(item)
                    Log.i("installing", "${item.nombreCurso} | ${item.capituloCurso}")
                } else {
                    Log.i("installing", "This item exist")
                }
            }
        }
    }
    */

    // check if the row exist
    /* Not used function...
    private fun installIfNotExist(item: CoursesEntity): Boolean {
        val actualList = coursesDao.all_nolive
        if (actualList.contains(item)) {
            return false
        }
        return true
    }
    */


    fun afterInstall(){
       createBackupViewedAndFav()
    }


    // create a database viewed backup
    private fun createBackupViewedAndFav(){
        // get the actual list of courses
        val actualList = coursesDao.all_nolive
        // delete the backup
        savedCoursesDao.deleteAll()
        // create a new backup
        for(item in actualList){
            if(item.capituloVisto || item.cursoFavorito){
                savedCoursesDao.insert(SavedCoursesEntity(item.captituloCursoURL, item.capituloVisto, item.cursoFavorito))
            }
        }
    }

    fun beforeInstall(){
        updateFromBackupViewedAndFav()
    }

    // update the database with saved values
    private fun updateFromBackupViewedAndFav(){
        // get saved information
        val backupList = savedCoursesDao.all_cursos
        // update the items
        for(savedItem in backupList){
            val curso = coursesDao.getChapter(savedItem.captituloCursoURL)
            if(curso.isNotEmpty()){
                val chapterEntity = curso.get(1)
                chapterEntity.capituloVisto = savedItem.capituloVisto
                chapterEntity.cursoFavorito = savedItem.cursoFavorito
                coursesDao.update(chapterEntity)
            }
        }
    }


    // install the database while is beeing downloaded
    fun directInstallInDatabase(course: CoursesEntity) {
        coursesDao.insert(course)
        Log.i("installing", "${course.nombreCurso} | ${course.capituloCurso}")
    }
}