package com.example.chuckoverflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chuckoverflow.Entities.Joke;
import com.example.chuckoverflow.Entities.JokeService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MainActivity activity = this;
    private BottomNavigationView mNavigation;
    private TextView mTitle;
    private TextView mJoke;
    private ImageView mRefresh;
    private ImageView mFavourite;
    private ProgressBar mProgress;

    private JokeDatabase db;
    private Joke joke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavHelper bnh = new BottomNavHelper();
        mNavigation = bnh.getBottomNav(this, R.id.navigation);
        db = Room.databaseBuilder(getApplicationContext(), JokeDatabase.class, "chuck-database").build();

        //Link XML elements to their respective Java class variables
        mTitle = findViewById(R.id.tv_prompt);
        mJoke = findViewById(R.id.tv_joke);
        mFavourite = findViewById(R.id.iv_favourite);
        mRefresh = findViewById(R.id.iv_refresh);
        mProgress = findViewById(R.id.pb_quote);

        //This sets an onClickListener for the refresh button
        mRefresh.setOnClickListener(v -> loadingQuote());

        loadingQuote();
    }

    protected void loadingQuote() {
        mFavourite.setVisibility(View.INVISIBLE);
        mRefresh.setVisibility(View.INVISIBLE);
        mJoke.setText(""); //before quote is loaded, set text to blank
        mProgress.setVisibility(View.VISIBLE); //before quote is loaded, show progress spinner
        mProgress.setIndeterminate(true); //make progress spinner indeterminate
        new GetJokeTask().execute(); //execute API functions
    }

    // Handles a GetJokeTask on an AsyncTask --> more reliable + code is more readable
    private class GetJokeTask extends AsyncTask<Void, Void, Joke> {
        @Override
        protected Joke doInBackground(Void... voids) {
            //Try-catch is used as the try block executes code to retrieve an online API
            try {
                //Retrofit is used to convert the Json class from the API
                mFavourite.setOnClickListener(null);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.chucknorris.io/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                JokeService service = retrofit.create(JokeService.class);
                Call<Joke> jokeCall = service.getJoke(); //call method
                Response<Joke> jokeResponse = jokeCall.execute(); //response method
                joke = jokeResponse.body(); //setting joke equal to Json from the API response
                return joke;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Joke joke){
            //Post-execute method handles rendering after API is called
            mProgress.setIndeterminate(false); //ends indeterminate loading
            mProgress.clearAnimation(); //stops loading animation
            mProgress.setVisibility(View.GONE); //makes progress bar body invisible
            if(joke != null){
                //If API function worked this is executed
                mTitle.setText("Did You Know...");
                mJoke.setText(joke.getValue()); //Quote sentence
                mFavourite.setVisibility(View.VISIBLE);
            }
            else {
                //If API function failed to retrieve quote, this is executed
                mTitle.setText("Failed to get quote!"); //Alerts user to failed quote process
                mJoke.setText("An error occurred! Please refresh!"); //Prompts user how to recover
            }
            new CheckJokeFavourited().execute();
            mRefresh.setVisibility(View.VISIBLE);
        }
    }

    //Handles a CheckJokeFavourited via an AsyncTask
    private class CheckJokeFavourited extends AsyncTask<Void, Void,  List<Joke>> {
        @Override
        protected List<Joke> doInBackground(Void... voids) {
            return db.jokeDao().checkJokeExist(joke.getId());
        }

        @Override
        protected void onPostExecute(List<Joke> jokeList) {
            if(jokeList.size() == 0){
                mFavourite.setImageResource(R.mipmap.heart_uf);
                mFavourite.setOnClickListener(v -> new SaveJoke().execute());
            }
            else {
                mFavourite.setImageResource(R.mipmap.heart_f);
                mFavourite.setOnClickListener(v -> new DeleteJoke().execute());
            }
        }
    }

    private class SaveJoke extends AsyncTask<Void, Void, Joke> {
        @Override
        protected Joke doInBackground(Void... voids) {
            db.jokeDao().insertJoke(joke);
            return null;
        }

        @Override
        protected void onPostExecute(Joke joke) {
            mFavourite.setImageResource(R.mipmap.heart_f);
            mFavourite.setOnClickListener(v -> new DeleteJoke().execute());
        }
    }

    private class DeleteJoke extends AsyncTask<Void, Void, Joke> {
        @Override
        protected Joke doInBackground(Void... voids) {
            db.jokeDao().deleteJoke(joke);
            new CheckJokeFavourited();
            return null;
        }

        @Override
        protected void onPostExecute(Joke joke) {
            mFavourite.setImageResource(R.mipmap.heart_uf);
            mFavourite.setOnClickListener(v -> new SaveJoke().execute());
        }
    }

}
