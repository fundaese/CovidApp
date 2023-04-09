package com.example.covidapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.covidapp.model.Country
import com.example.covidapp.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CovidListViewModel
@Inject constructor(
    private val apiRepository: ApiRepository, application: Application
) : BaseViewModel(application) {

    private val _response = MutableLiveData<List<Country>>()
    val response: LiveData<List<Country>>
        get() = _response

    private val _moviesError = MutableLiveData<Boolean>()
    val moviesError: LiveData<Boolean>
        get() = _moviesError

    private val _moviesLoading = MutableLiveData<Boolean>()
    val moviesLoading: LiveData<Boolean>
        get() = _moviesLoading

    fun getCovidList() {
        _moviesLoading.value = true

        launch {
            val covidListResponse = apiRepository.getCovidListFromRepo()
            if (covidListResponse.isSuccessful) {
                covidListResponse.body()!!.Countries.let {
                    _moviesError.value = false
                    _moviesLoading.value = false
                    _response.postValue(it)
                }
            } else {
                _moviesError.value = true
                _moviesLoading.value = false
            }
        }
    }

    fun refreshData() {
        getCovidList()
    }

    fun filterResults(searchTerm: String) {
        _response.value?.let { list ->
            val filteredList = list.filter { country ->
                country.Country.replace("\\s".toRegex(), "").contains(searchTerm.replace("\\s".toRegex(), ""), ignoreCase = true)
            }
            _response.postValue(filteredList)
        }
    }

}