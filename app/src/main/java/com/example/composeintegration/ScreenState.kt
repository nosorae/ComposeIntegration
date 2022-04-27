package com.example.composeintegration

sealed class ScreenState {
    object Loading: ScreenState()
    object Success: ScreenState()
    object Error: ScreenState()
}