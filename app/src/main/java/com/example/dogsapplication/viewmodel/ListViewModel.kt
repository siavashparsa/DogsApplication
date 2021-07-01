package com.example.dogsapplication.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogsapplication.model.DogBreed
import com.example.dogsapplication.model.DogDataBase
import com.example.dogsapplication.model.DogsApiService
import com.example.dogsapplication.util.NotificationsHelper
import com.example.dogsapplication.util.SharedPreferencesHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L   //nano second

    private val dogsService = DogsApiService()
    private val disposable =CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>?>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {

        checkedCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime <refreshTime){

            fetchFromDatabase()

        }else{
            fetchFromRemote()
        }

    }

    private fun checkedCacheDuration() {

        val cachePreference = prefHelper.getCacheDuration()

        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times( 1000 * 1000 * 1000L )

        }catch (e:NumberFormatException){

            e.printStackTrace()
        }

    }

    fun refreshByPassCache(){

        fetchFromRemote()

    }

    private fun fetchFromDatabase(){

        loading.value = true

        launch {

            val dogs = DogDataBase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
        }
    }

    private fun fetchFromRemote(){

        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {

                        storeDogsLocally(dogList)
                        NotificationsHelper(getApplication()).createNotification()

                    }

                    override fun onError(e: Throwable?) {

                        dogsLoadError.value = true
                        loading.value = false
                        e?.printStackTrace()

                    }
                })
        )
    }

    private fun dogsRetrieved (dogList:List<DogBreed>){

        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false

    }

    private fun storeDogsLocally(list:List<DogBreed>){

        launch {

            val dao= DogDataBase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*list.toTypedArray())
            var i = 0
            while (i<list.size){

                list[i].uuid = result[i].toInt()
                i++
            }

            dogsRetrieved(list)
        }

        prefHelper.saveUpdateTime(System.nanoTime())
    }



    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}