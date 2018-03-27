package com.example.alex.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alex.popularmovies.data.TrailerJson;
import com.example.alex.popularmovies.data.TrailersJson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private TrailersJson mTrailers;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void OnClick(TrailerJson trailer);
    }

    public TrailersAdapter(Context context, TrailersJson trailers, OnClickListener onClickListener) {
        super();
        mTrailers = trailers;
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, int position) {
        String imagePath = mTrailers.getResults().get(position).getThumbnail();
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
        return mTrailers == null ? 0 : mTrailers.getResults().size();
    }

    public void setNewData(TrailersJson trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ProgressBar progressBar;

        TrailerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progress_bar);
            itemView.setOnClickListener(v -> {
                if (mOnClickListener == null) return;
                int position = this.getAdapterPosition();
                mOnClickListener.OnClick(mTrailers.getResults().get(position));
            });
        }

    }
}