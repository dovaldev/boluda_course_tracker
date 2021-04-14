package com.dovaldev.boludacoursetracker.database.base

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cursos_table")
data class CoursesEntity(var nombreCurso: String = "", var URLCurso: String = "",
                         var infoCurso: String = "", var imgCurso: String = "",
                         var capituloCursoNumber: Int = 0, var capituloCurso: String = "", var captituloCursoURL: String ="",
                         var capituloVisto: Boolean = false, var cursoFavorito: Boolean = false,
                         var info1: String = "", var info2: String = ""){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}
