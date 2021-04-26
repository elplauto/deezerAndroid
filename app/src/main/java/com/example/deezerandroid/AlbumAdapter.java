package com.example.deezerandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deezerandroid.model.Album;
import com.squareup.picasso.Picasso;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private static final String TAG = "AlbumAdapter";
    private OnAlbumClickListener onAlbumClickListener;

    private final Album[] listAlbums;

    public AlbumAdapter(Album[] listAlbums, OnAlbumClickListener onAlbumClickListener) {
        this.listAlbums = listAlbums;
        this.onAlbumClickListener = onAlbumClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.album_raw, parent, false);

        return new ViewHolder(itemView, onAlbumClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = listAlbums[position];
        Picasso.get().load(album.getCoverBig()).into(holder.imageView);
        holder.textViewTitle.setText(album.getTitle());
        holder.textViewTitleSecondary.setText(album.getReleaseDate());
    }

    @Override
    public int getItemCount() {
        return listAlbums.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewTitle;
        private final TextView textViewTitleSecondary;
        private final ImageView imageView;

        private OnAlbumClickListener onAlbumClickListener;

        public ViewHolder(View view, OnAlbumClickListener onAlbumClickListener) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewTitleSecondary = view.findViewById(R.id.textViewTitleSecondary);
            this.onAlbumClickListener = onAlbumClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAlbumClickListener.onAlbumClick(getAdapterPosition());
        }
    }

    public interface OnAlbumClickListener {
        void onAlbumClick(int position);
    }
}
