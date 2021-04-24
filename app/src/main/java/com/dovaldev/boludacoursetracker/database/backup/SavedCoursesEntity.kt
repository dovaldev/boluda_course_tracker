package com.dovaldev.boludacoursetracker.database.backup

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * data class to manage the chapters saved in the app.
 */
@Entity(tableName = "cursos_saved_table")
data class SavedCoursesEntity(var captituloCursoURL: String ="",
                              var capituloVisto: Boolean = false, var cursoFavorito: Boolean = false){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}
