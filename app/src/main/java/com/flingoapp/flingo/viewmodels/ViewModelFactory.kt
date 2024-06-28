package com.flingoapp.flingo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flingoapp.flingo.viewmodels.main.MainViewModel

class ViewModelFactory() :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainViewModel() as T
}