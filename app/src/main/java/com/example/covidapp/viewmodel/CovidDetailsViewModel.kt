package com.example.covidapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.covidapp.view.CovidDetailsFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CovidDetailsViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle, application: Application
) : BaseViewModel(application) {

    private val _countryName = MutableLiveData<String>()
    val countryName: LiveData<String> = _countryName

    private val _confirmed = MutableLiveData<Float>()
    val confirmed: LiveData<Float> = _confirmed

    private val _recovered = MutableLiveData<Float>()
    val recovered: LiveData<Float> = _recovered

    private val _death = MutableLiveData<Float>()
    val death: LiveData<Float> = _death

    init {
        val args = CovidDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)
        _countryName.value = args.countryName
        _confirmed.value = args.totalConfirmed.toFloat()
        _death.value = args.totalDeaths.toFloat()
        _recovered.value = 10000F
    }

}