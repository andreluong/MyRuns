package com.andre_luong.myruns5.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.andre_luong.myruns5.model.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDatabaseDao {
    @Insert
    suspend fun insertExercise(exercise: Exercise)

    @Query("SELECT * FROM exercise_table")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise_table WHERE id == :id")
    fun getExerciseById(id: Long): Flow<Exercise?>

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Update
    suspend fun updateExercise(exercise: Exercise)
}