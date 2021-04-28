package com.dovaldev.boludacoursetracker.database.backup

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(SavedCoursesEntity::class), version = 2, exportSchema = false)
abstract class SavedCoursesRoomDatabase : RoomDatabase() {

    abstract fun coursesDao(): SavedCoursesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SavedCoursesRoomDatabase? = null

        fun getDatabase(context: Context): SavedCoursesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedCoursesRoomDatabase::class.java,
                    "cursos_saved_database"
                ).addMigrations(MIGRATION_1_2)
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}