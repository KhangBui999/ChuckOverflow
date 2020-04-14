package com.example.chuckoverflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.chuckoverflow.Entities.Joke;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private BottomNavigationView mNavigation;
    private RecyclerView mRecyclerView;
    private JokeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayout;
    private List<Joke> mJokes;
    private TextView mClear, mStatus;
    private JokeDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        BottomNavHelper bnh = new BottomNavHelper();
        mNavigation = bnh.getBottomNav(this, R.id.navigation);
        mClear = findViewById(R.id.tv_clear);
        mStatus = findViewById(R.id.tv_noload);
        mRecyclerView = findViewById(R.id.rvList);
        mRecyclerView.setHasFixedSize(true);
        mLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayout);
        JokeAdapter.RecyclerViewClickListener listener = new JokeAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
        };
        mAdapter = new JokeAdapter(new ArrayList<Joke>(), listener);
        db = Room.databaseBuilder(getApplicationContext(), JokeDatabase.class, "chuck-database").build();
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClearJokeList().execute();
            }
        });
        new LoadJokeList().execute();
    }

    private class LoadJokeList extends AsyncTask<Void, Void, List<Joke>> {
        @Override
        protected List<Joke> doInBackground(Void... voids) {
            mJokes = db.jokeDao().getAll();
            return mJokes;
        }

        @Override
        protected void onPostExecute(List<Joke> jokes) {
            mAdapter.setJokes(jokes);
            mRecyclerView.setAdapter(mAdapter);
            if(jokes.size() == 0){
                mStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ClearJokeList extends AsyncTask<Void, Void, List<Joke>> {
        @Override
        protected List<Joke> doInBackground(Void... voids) {
            db.clearAllTables();
            return null;
        }

        @Override
        protected void onPostExecute(List<Joke> jokes) {
            mAdapter.clearJokes();
            mAdapter.notifyDataSetChanged();
            mStatus.setVisibility(View.VISIBLE);
        }
    }
}
