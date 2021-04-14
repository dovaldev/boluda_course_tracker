package com.dovaldev.boludacoursetracker.database.base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(CoursesEntity::class), version = 1, exportSchema = false)
public abstract class CoursesRoomDatabase : RoomDatabase() {

    abstract fun coursesDao(): CoursesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: CoursesRoomDatabase? = null

        fun getDatabase(context: Context): CoursesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoursesRoomDatabase::class.java,
                    "cursos_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}