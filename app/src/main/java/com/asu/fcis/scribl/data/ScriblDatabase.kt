package com.asu.fcis.scribl.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CategoryEntity::class, NoteEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScriblDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: ScriblDatabase? = null

        fun getDatabase(context: Context): ScriblDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScriblDatabase::class.java,
                    "scribl_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
