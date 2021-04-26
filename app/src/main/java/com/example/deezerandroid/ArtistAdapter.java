package com.example.deezerandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deezerandroid.model.Artist;
import com.squareup.picasso.Picasso;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private static final String TAG = "ArtistAdapter";
    private OnArtistClickListener onArtistClickListener;

    private final Artist[] listArtist;

    public ArtistAdapter(Artist[] listArtist, OnArtistClickListener onArtistClickListener) {
        this.listArtist = listArtist;
        this.onArtistClickListener = onArtistClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.artist_raw, parent, false);

        return new ViewHolder(itemView, onArtistClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = listArtist[position];
        Picasso.get().load(artist.getPictureBig()).into(holder.imageView);
        holder.textViewTitle.setText(artist.getName());
        holder.textViewTitleSecondary.setText(artist.getNbFan() + " fans");
    }

    @Override
    public int getItemCount() {
        return listArtist.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewTitle;
        private final TextView textViewTitleSecondary;
        private final ImageView imageView;

        private OnArtistClickListener onArtistClickListener;

        public ViewHolder(View view, OnArtistClickListener onArtistClickListener) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewTitleSecondary = view.findViewById(R.id.textViewTitleSecondary);
            this.onArtistClickListener = onArtistClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onArtistClickListener.onArtistClick(getAdapterPosition());
        }
    }

    public interface OnArtistClickListener {
        void onArtistClick(int position);
    }
}
