package com.dovaldev.boludacoursetracker.database.backup

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * data class to manage the chapters saved in the app.
 */
@Entity(tableName = "cursos_saved_table")
data class SavedCoursesEntity(
        var captituloCursoURL: String = "",
        var capituloVisto: Boolean = false,
        var cursoFavorito: Boolean = false,
        var tiempoGuardado: String = "" // added in version 2
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}

// Migration from 1 to 2, Room 2.1.0
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                "ALTER TABLE cursos_saved_table ADD COLUMN tiempoGuardado TEXT NOT NULL DEFAULT ''")
    }
}
