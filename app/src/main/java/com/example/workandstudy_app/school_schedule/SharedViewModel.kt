package com.example.workandstudy_app.school_schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workandstudy_app.data_school_schedule.DataSubject

class SharedViewModel: ViewModel() {
    private val _selectedSubject= MutableLiveData<DataSubject?>()
    val selectedSubject: LiveData<DataSubject?> = _selectedSubject

    fun setSelectedSubject(subject: DataSubject){
        _selectedSubject.value=subject
    }

    fun clearSelectedSubject(){
        _selectedSubject.value=null
    }
}