package com.dovaldev.boludacoursetracker.database.backup

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cursos_saved_table")
data class SavedCoursesEntity(var captituloCursoURL: String ="",
                              var capituloVisto: Boolean = false, var cursoFavorito: Boolean = false){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}
