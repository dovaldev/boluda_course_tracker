package com.dovaldev.boludacoursetracker.database.backup

import androidx.room.*
@Dao
interface SavedCoursesDao{

    // get all courses nolive
    @get:Query("SELECT * FROM cursos_saved_table  ORDER BY captituloCursoURL ASC")
    val all_cursos: List<SavedCoursesEntity>

    // Insert items
    @Insert
    fun insert(savedCoursesEntity: SavedCoursesEntity)

    // Update items
    @Update
    fun update(savedCoursesEntity: SavedCoursesEntity)

    // delete all items
    @Query("DELETE FROM cursos_saved_table")
    fun deleteAll()

    // delete by item id
    @Query("DELETE FROM cursos_saved_table WHERE id = :id")
    fun deleteById(id: Long)


    // delete duplicates
    @Query("DELETE FROM cursos_saved_table WHERE id not in (SELECT min(id) from cursos_saved_table group by captituloCursoURL, capituloVisto, cursoFavorito)")
    fun deleteDuplicates()

}