package com.andre_luong.myruns5.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andre_luong.myruns5.model.Exercise
import com.andre_luong.myruns5.util.TypeConverter

@Database(entities = [Exercise::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class ExerciseDatabase : RoomDatabase() {
    abstract val exerciseDatabaseDao: ExerciseDatabaseDao

    companion object {
        @Volatile
        private var instance: ExerciseDatabase? = null

        fun getInstance(context: Context) : ExerciseDatabase {
            synchronized(this) {
                var instance = instance
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ExerciseDatabase::class.java,
                        "exercise_table"
                    ).build()
                    Companion.instance = instance
                }
                return instance
            }
        }
    }
}