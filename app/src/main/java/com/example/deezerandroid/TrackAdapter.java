package com.example.deezerandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deezerandroid.model.Track;
import com.squareup.picasso.Picasso;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private static final String TAG = "TrackAdapter";
    private OnTrackClickListener OnTrackClickListener;

    private final Track[] listTrack;

    public TrackAdapter(Track[] listTrack, OnTrackClickListener OnTrackClickListener) {
        this.listTrack = listTrack;
        this.OnTrackClickListener = OnTrackClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
               inflate(R.layout.track_raw, parent, false);

        return new ViewHolder(itemView, OnTrackClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = listTrack[position];
        holder.textViewTitle.setText(track.getTitle());
        int t = track.getDuration();
        String duration = String.format("%02d:%02d", (t % 3600) / 60, (t % 60));
        holder.textViewTitleSecondary.setText(duration);
    }

    @Override
    public int getItemCount() {
        return listTrack.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewTitle;
        private final TextView textViewTitleSecondary;

        private OnTrackClickListener OnTrackClickListener;

        public ViewHolder(View view, OnTrackClickListener OnTrackClickListener) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewTitleSecondary = view.findViewById(R.id.textViewTitleSecondary);
            this.OnTrackClickListener = OnTrackClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnTrackClickListener.onTrackClick(getAdapterPosition());
        }
    }

    public interface OnTrackClickListener {
        void onTrackClick(int position);
    }
}
