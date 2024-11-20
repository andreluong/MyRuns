package com.andre_luong.myruns5.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.andre_luong.myruns5.model.Exercise

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    val allExercisesLiveData : LiveData<List<Exercise>> = repository.allExercises.asLiveData()

    fun getExerciseById(id: Long): LiveData<Exercise?> {
        return repository.getExerciseById(id).asLiveData()
    }

    fun insert(exercise: Exercise) {
        repository.insert(exercise)
    }

    fun delete(exercise: Exercise) {
        repository.delete(exercise)
    }

    fun update(exercise: Exercise) {
        repository.update(exercise)
    }
}