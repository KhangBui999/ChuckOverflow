package com.example.chuckoverflow.Entities;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * JokeService interface for Retrofit API calls
 */
public interface JokeService {
    @GET("jokes/random?category=dev")
    Call<Joke> getJoke();
}
