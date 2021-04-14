package com.dovaldev.boludacoursetracker.database.base

import android.os.AsyncTask
import androidx.lifecycle.LiveData

class CoursesRepository(private val coursesDao: CoursesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    //  ==========> GET ALL ENTITIES
    val allEntities: LiveData<List<CoursesEntity>> = coursesDao.all


    val allEntities_cursos: LiveData<List<CoursesEntity>> = coursesDao.all_cursos


    fun getCourseSearched(course: String, fav: Boolean): LiveData<List<CoursesEntity>>{
        return if(course.isNotEmpty()){
            coursesDao.getCursoSearched("%${course}%")
        } else if(fav){
            coursesDao.getCursoSearchedFav()
        } else{
            coursesDao.all_cursos
        }

    }

    // get course selected
    fun getCourseSelected(course: String): LiveData<List<CoursesEntity>>{
        return if(course.isNotEmpty()){
            coursesDao.getCursoChapter(course)
        } else{
            coursesDao.all_cursos
        }

    }

    // get item selected
    fun getItemSelected(id: Long): CoursesEntity {
      return coursesDao.getItemSelected(id)
    }



    //  ==========> INSERT
    suspend fun insert(coursesEntity: CoursesEntity) {
        insertTask(coursesDao, coursesEntity).execute()
    }

    class insertTask(val coursesDao: CoursesDao, val coursesEntity: CoursesEntity) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            coursesDao.insert(coursesEntity)
            return null
        }
    }


    //  ==========> UPDATE
    fun update(coursesEntity: CoursesEntity) {
        updateTask(coursesDao, coursesEntity).execute()
    }

    class updateTask(val coursesDao: CoursesDao, val coursesEntity: CoursesEntity) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            coursesDao.update(coursesEntity)
            return null
        }
    }

    //  ==========>  DELETE

    // delete by id
    fun deleteById(id: Long){
        coursesDao.deleteById(id)
    }

    // delete all
    fun deleteAll(){
        deleteTask(coursesDao, null).execute()
    }

    fun delete(itemEntity: CoursesEntity){
        deleteTask(coursesDao, itemEntity).execute()
    }

    class deleteTask(val coursesDao: CoursesDao, val coursesEntity: CoursesEntity?) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            when (coursesEntity){
                null -> coursesDao.deleteAll()
                else -> coursesDao.deleteById(coursesEntity.id)
            }
            return null
        }
    }

    //fun getLastID() = timeDao.getLastId()






}