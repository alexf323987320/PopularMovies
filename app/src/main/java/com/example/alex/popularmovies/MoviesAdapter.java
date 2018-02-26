package com.example.alex.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alex.popularmovies.data.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private ArrayList<Movie> mMovies;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void OnClick(Movie movie);
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies, OnClickListener onClickListener) {
        super();
        mMovies = movies;
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder holder, int position) {
        String imagePath = mMovies.get(position).getPoster();
        Picasso.with(mContext).load(imagePath).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        //holder.imageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    public void setNewData(ArrayList<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public ProgressBar progressBar;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_iv);
            progressBar = itemView.findViewById(R.id.progress_bar_pb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener == null) return;
                    int position = MoviesViewHolder.this.getAdapterPosition();
                    mOnClickListener.OnClick(mMovies.get(position));
                }
            });
        }

    }
}