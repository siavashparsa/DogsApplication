package com.example.dogsapplication.model

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface DogsApi {

    @GET("devtides/dogsapi/master/dogs.json")
    fun getDogs():Single<List<DogBreed>>


}