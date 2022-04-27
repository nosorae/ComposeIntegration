package com.example.composeintegration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _stateFlow = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val stateFlow: StateFlow<ScreenState> = _stateFlow
    private val _liveData = MutableLiveData<ScreenState>(ScreenState.Loading)
    val liveData: LiveData<ScreenState> = _liveData

    init {
        changeStateFlow()
        changeLiveData()
    }

    fun changeStateFlow() = viewModelScope.launch {
        delay(1000)
        _stateFlow.value = ScreenState.Success
        delay(1000)
        _stateFlow.value = ScreenState.Error
    }

    fun changeLiveData() = viewModelScope.launch {
        delay(1000)
        _liveData.value = ScreenState.Success
        delay(1000)
        _liveData.value = ScreenState.Error
    }
}