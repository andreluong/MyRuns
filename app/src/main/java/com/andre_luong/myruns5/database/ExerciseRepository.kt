package com.andre_luong.myruns5.database

import com.andre_luong.myruns5.model.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseRepository(private val exerciseDatabaseDao: ExerciseDatabaseDao) {
    val allExercises: Flow<List<Exercise>> = exerciseDatabaseDao.getAllExercises()

    fun getExerciseById(id: Long): Flow<Exercise?> {
        return exerciseDatabaseDao.getExerciseById(id)
    }

    fun insert(exercise: Exercise) {
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.insertExercise(exercise)
        }
    }

    fun delete(exercise: Exercise) {
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.deleteExercise(exercise)
        }
    }

    fun update(exercise: Exercise) {
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.updateExercise(exercise)
        }
    }
}