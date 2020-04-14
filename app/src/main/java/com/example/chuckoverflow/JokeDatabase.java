package com.example.chuckoverflow;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.chuckoverflow.Entities.Joke;

/**
 * Abstract class that acts as the main object of the Database class
 */
@Database(entities = {Joke.class}, version = 1)
public abstract class JokeDatabase extends RoomDatabase {
    public abstract JokeDao jokeDao();
}
