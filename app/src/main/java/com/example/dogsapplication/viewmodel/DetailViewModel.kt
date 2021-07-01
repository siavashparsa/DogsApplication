package com.example.dogsapplication.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogsapplication.model.DogBreed
import com.example.dogsapplication.model.DogDataBase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) :BaseViewModel(application) {

    val dogLiveData = MutableLiveData<DogBreed>()

    fun fetch(uuid:Int){

        launch {
            val dog = DogDataBase(getApplication()).dogDao().getDog(uuid)

            dogLiveData.value = dog

        }


    }

}