package com.example.chuckoverflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chuckoverflow.Entities.Joke;
import com.example.chuckoverflow.Entities.JokeService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MainActivity activity = this;
    private ConstraintLayout mLayout;
    private TextView mTitle;
    private TextView mJoke;
    private ImageView mRefresh;
    private ImageView mFavourite;
    private ProgressBar mProgress;

    private Joke joke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link XML elements to their respective Java class variables
        mTitle = findViewById(R.id.tv_prompt);
        mJoke = findViewById(R.id.tv_joke);
        mFavourite = findViewById(R.id.iv_favourite);
        mRefresh = findViewById(R.id.iv_refresh);
        mProgress = findViewById(R.id.pb_quote);

        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saves to database we cant see yet :)
            }
        });

        //This sets an onClickListener for the refresh button
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Handles the generation of new quotes
                mJoke.setText(""); //before quote is loaded, set text to blank
                mProgress.setVisibility(View.VISIBLE); //before quote is loaded, show progress spinner
                mProgress.setIndeterminate(true); //make progress spinner indeterminate
                new GetJokeTask().execute(); //execute API functions
            }
        });

        //Initial loading of the first quote
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
        public void onPostExecute(Joke joke){
            //Post-execute method handles rendering after API is called
            mProgress.setIndeterminate(false); //ends indeterminate loading
            mProgress.clearAnimation(); //stops loading animation
            mProgress.setVisibility(View.GONE); //makes progress bar body invisible
            if(joke != null){
                //If API function worked this is executed
                mTitle.setText("Did You Know...");
                mJoke.setText(joke.getValue()); //Quote sentence
                Toast toast = Toast.makeText(activity, "New Quote Generated!", Toast.LENGTH_SHORT); //UI notification
                toast.show(); //shows notification
            }
            else {
                //If API function failed to retrieve quote, this is executed
                mTitle.setText("Failed to get quote!"); //Alerts user to failed quote process
                mJoke.setText("An error occurred! Please refresh!"); //Prompts user how to recover
                Toast toast = Toast.makeText(activity, "Quote Failed To Generate!", Toast.LENGTH_SHORT); //UI notification
                toast.show(); //shows notification
            }
        }
    }


}
