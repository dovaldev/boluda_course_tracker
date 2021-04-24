package com.dovaldev.boludacoursetracker.database.base

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CoursesDao{


    // get all items
    @get:Query("SELECT * FROM cursos_table ORDER BY nombreCurso ASC")
    val all: LiveData<List<CoursesEntity>>

    // get all items dates
    @get:Query("SELECT * FROM cursos_table GROUP BY nombreCurso ORDER BY nombreCurso ASC")
    val all_cursos: LiveData<List<CoursesEntity>>

    // get all courses
    @Query("SELECT * FROM cursos_table where nombreCurso = :curso ORDER BY nombreCurso ASC")
    fun getCurso(curso: String?): LiveData<List<CoursesEntity>>

    // get all courses
    @Query("SELECT * FROM cursos_table where nombreCurso LIKE :curso GROUP BY nombreCurso ORDER BY nombreCurso ASC")
    fun getCursoSearched(curso: String?): LiveData<List<CoursesEntity>>

    @Query("SELECT * FROM cursos_table where cursoFavorito = 1 GROUP BY nombreCurso ORDER BY nombreCurso ASC")
    fun getCursoSearchedFav(): LiveData<List<CoursesEntity>>

    // get all courses
    @Query("SELECT * FROM cursos_table where URLCurso = :Urlcurso  ORDER BY capituloCursoNumber ASC")
    fun getCursoChapter(Urlcurso: String?): LiveData<List<CoursesEntity>>

    // get all courses nolive
    @Query("SELECT * FROM cursos_table where URLCurso = :Urlcurso  ORDER BY capituloCursoNumber ASC")
    fun getCursoChapter_nolive(Urlcurso: String?): List<CoursesEntity>


    // get all items NO LIVE
    @get: Query("SELECT * FROM cursos_table  ORDER BY nombreCurso ASC")
    val all_nolive: List<CoursesEntity>

    // get the chapter
    @Query("SELECT * FROM cursos_table where captituloCursoURL = :urlChapter")
    fun getChapter(urlChapter: String?): List<CoursesEntity>

    // get all courses
    @Query("SELECT * FROM cursos_table where URLCurso = :urlCurso ORDER BY URLCurso ASC")
    fun getUrlCursoNoLive(urlCurso: String?): List<CoursesEntity>



    // get item selected
    @Query("SELECT * FROM cursos_table where id = :id")
    fun getItemSelected(id: Long?): CoursesEntity

    // get item selected
    @Query("SELECT * FROM cursos_table where URLCurso = :courseURL")
    fun getFirstItemSelected(courseURL: String?): CoursesEntity

    // table size
    @Query ("SELECT count(*) from cursos_table")
    fun getTotalRows(): Long


    // Insert items
    @Insert
    fun insert(coursesEntity: CoursesEntity)

    // Update items
    @Update
    fun update(coursesEntity: CoursesEntity)

    // delete all items
    @Query("DELETE FROM cursos_table")
    fun deleteAll()

    // delete by item id
    @Query("DELETE FROM cursos_table WHERE id = :id")
    fun deleteById(id: Long)

    // delete by item streamer
    @Query("DELETE FROM cursos_table WHERE URLCurso = :courseUrl")
    fun deleteByStreamer(courseUrl: String)

    // delete duplicates
    @Query("DELETE FROM cursos_table WHERE id not in (SELECT min(id) from cursos_table group by URLCurso, captituloCursoURL, capituloCurso)")
    fun deleteDuplicates()


    // comparator
    @Query("SELECT * FROM cursos_table where nombreCurso = :courseTitle and URLCurso = :courseUrl and captituloCursoURL = :chapterUrl")
    fun getDuplicates(courseTitle: String?, courseUrl: String?, chapterUrl: String): List<CoursesEntity>
}