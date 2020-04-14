package com.example.chuckoverflow;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.chuckoverflow.Entities.Joke;

import java.util.List;

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeViewHolder> {
    private List<Joke> mJokes;
    private RecyclerViewClickListener mListener;
    private Joke joke;
    private Context context;

    public JokeAdapter(List<Joke> jokes, RecyclerViewClickListener listener) {
        mJokes = jokes;
        mListener = listener;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public static class JokeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mQuote;
        private ImageButton mImageBtn;
        private RecyclerViewClickListener mListener;

        public JokeViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            v.setOnClickListener(this);
            mQuote = v.findViewById(R.id.tv_quote);
            mImageBtn = v.findViewById(R.id.ib_btn);
        }

        @Override
        public void onClick(View view) {
        }
    }

    @Override
    public JokeAdapter.JokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.joke_list_view, parent, false);
        context = v.getContext();
        return new JokeViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull JokeViewHolder holder, int position) {
        joke = mJokes.get(position);
        holder.mQuote.setText(joke.getValue());
        holder.mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteJoke().execute();
                mJokes.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    private class DeleteJoke extends AsyncTask<Void, Void, Joke> {
        @Override
        protected Joke doInBackground(Void... voids) {
            JokeDatabase db = Room.databaseBuilder(context, JokeDatabase.class, "chuck-database").build();
            db.jokeDao().deleteJoke(joke);
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return mJokes.size();
    }

    public void setJokes(List<Joke> jokes) {
        mJokes.clear();
        mJokes.addAll(jokes);
    }

    public void clearJokes() {
        mJokes.clear();
    }
}
