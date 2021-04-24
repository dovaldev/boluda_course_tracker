package com.dovaldev.boludacoursetracker.database.tools

import android.content.Context
import android.util.Log
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.base.CoursesRoomDatabase
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync

class databaseFunctions(c: Context) {

    private val coursesDao = CoursesRoomDatabase.getDatabase(c).coursesDao()

    // when you click on chapter viewed is marked with a line ------
    fun onClickChapterView(entity: CoursesEntity) {
        //Log.i("captitulo-visto", "${entity.capituloVisto}")
        doAsync {
            val newEntity = entity

            when (entity.capituloVisto) {
                true -> { newEntity.capituloVisto = false }
                else -> { newEntity.capituloVisto = true }
            }
            coursesDao.update(newEntity)
            //Log.i("captitulo-visto", "${entity.capituloVisto} - ${newEntity.capituloVisto}")
        }
    }


    // dont used function
    fun onClickCourseFavorite(entity: CoursesEntity){
        when(entity.cursoFavorito){
            true -> {
                setCursoNoFavorito(entity)
            }
            else -> {
                setCursoFavorito(entity)
            }
        }
    }

    // when you click on view the course all chapters will apears as viewed
    fun setCursoVisto(entity: CoursesEntity) {
        val list = coursesDao.getCursoChapter_nolive(entity.URLCurso)
        var cursoVisto = true
        list.forEach {
            if(!it.capituloVisto){
                cursoVisto = false
            }
        }
        list.forEach {
            it.capituloVisto = !cursoVisto
            coursesDao.update(it)
        }
    }

    fun getCursoVisto(entity: CoursesEntity): Boolean{
        val list = coursesDao.getCursoChapter_nolive(entity.URLCurso)
        var cursoVisto = true
        list.forEach {
            if(!it.capituloVisto){
                cursoVisto = false
            }
        }
        return cursoVisto
    }

    fun getCursoFavorito(entity: CoursesEntity): Boolean {
        val list = coursesDao.getCursoChapter_nolive(entity.URLCurso)
        var cursoFav = true
        list.forEach {
            if(!it.cursoFavorito){
                cursoFav = false
            }
        }
        return cursoFav
    }

    fun setCursoFav(entity: CoursesEntity) {
        val list = coursesDao.getCursoChapter_nolive(entity.URLCurso)
        var cursoFav = true
        list.forEach {
            if(!it.cursoFavorito){
                cursoFav = false
            }
        }
        list.forEach {
            it.cursoFavorito = !cursoFav
            coursesDao.update(it)
        }
    }


    private fun setCursoFavorito(entity: CoursesEntity) {
        val list = coursesDao.getCursoChapter_nolive(entity.URLCurso)
        list.forEach {
            it.cursoFavorito = true
            coursesDao.update(it)
        }
    }

    private fun setCursoNoFavorito(entity: CoursesEntity) {
        val list = coursesDao.getCursoChapter_nolive(entity.URLCurso)
        list.forEach {
            it.cursoFavorito = false
            coursesDao.update(it)
        }
    }


}

