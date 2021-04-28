package com.dovaldev.boludacoursetracker.database.base

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "cursos_table")
data class CoursesEntity(
        var nombreCurso: String = "",
        var URLCurso: String = "",
        var infoCurso: String = "",
        var imgCurso: String = "",
        var capituloCursoNumber: Int = 0,
        var capituloCurso: String = "",
        var captituloCursoURL: String = "",
        var capituloVisto: Boolean = false,
        var cursoFavorito: Boolean = false,
        var info1: String = "",
        var info2: String = "",
        var tiempoGuardado: String = "" // added in version 2

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}

// Migration from 1 to 2, Room 2.1.0
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                "ALTER TABLE cursos_table ADD COLUMN tiempoGuardado TEXT NOT NULL DEFAULT ''")
    }
}
