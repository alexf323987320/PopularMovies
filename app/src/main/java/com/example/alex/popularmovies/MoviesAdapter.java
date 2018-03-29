package com.example.alex.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alex.popularmovies.data.MovieJson;
import com.example.alex.popularmovies.data.MoviesJson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private MoviesJson mMovies;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void OnClick(MovieJson movie);
    }

    public MoviesAdapter(Context context, MoviesJson movies, OnClickListener onClickListener) {
        super();
        mMovies = movies;
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_main, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        String imagePath = mMovies.getResults()[position].getPoster();
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
    }

    @Override
    public int getItemCount() {
        return mMovies == null || mMovies.getResults() == null ? 0 : mMovies.getResults().length;
    }

    public void setNewData(MoviesJson movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public ProgressBar progressBar;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progress_bar);
            itemView.setOnClickListener(v -> {
                if (mOnClickListener == null) return;
                int position = this.getAdapterPosition();
                mOnClickListener.OnClick(mMovies.getResults()[position]);
            });
        }

    }
}