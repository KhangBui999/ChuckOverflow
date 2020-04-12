package com.example.chuckoverflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.chuckoverflow.Entities.Joke;

import java.util.List;

@Dao
public interface JokeDao {
    @Query("SELECT * FROM joke ORDER BY date")
    List<Joke> getAll();

    @Query("SELECT * FROM joke WHERE id = :quoteId")
    List<Joke> checkJokeExist(String quoteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJoke(Joke joke);

    @Delete
    void deleteJoke(Joke joke);
}
