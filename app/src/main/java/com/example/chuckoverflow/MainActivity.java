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

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoke.setText("");
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setIndeterminate(true);
                new GetJokeTask().execute();
            }
        });

        mProgress.setVisibility(View.VISIBLE);
        mProgress.setIndeterminate(true);
        new GetJokeTask().execute();
    }

    private class GetJokeTask extends AsyncTask<Void, Void, Joke> {
        @Override
        protected Joke doInBackground(Void... voids) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.chucknorris.io/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                JokeService service = retrofit.create(JokeService.class);
                Call<Joke> jokeCall = service.getJoke();
                Response<Joke> jokeResponse = jokeCall.execute();
                joke = jokeResponse.body();
                return joke;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(Joke joke){
            mProgress.setIndeterminate(false);
            mProgress.clearAnimation();
            mProgress.setVisibility(View.GONE);
            if(joke != null){
                mTitle.setText("Did You Know...");
                mJoke.setText(joke.getValue());
                Toast toast = Toast.makeText(activity, "New Joke Generated!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                mTitle.setText("Failed to get quote!");
                mJoke.setText("An error occurred! Please refresh!");
                Toast toast = Toast.makeText(activity, "Joke Failed To Generate!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


}
