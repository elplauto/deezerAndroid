package com.example.deezerandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.deezerandroid.model.Album;
import com.example.deezerandroid.model.DataSearchAlbum;
import com.example.deezerandroid.model.DataSearchTrack;
import com.example.deezerandroid.model.Track;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackListActivity extends AppCompatActivity implements TrackAdapter.OnTrackClickListener {

    private static String TAG = "TrackListActivity";
    private RecyclerView recyclerView;
    private List<Track> tracks;
    final TrackAdapter.OnTrackClickListener onTrackClickListener = this;
    private static final int PROGRESS_PRECISION = 10000;
    Timer timer = null;

    ImageButton imageButtonPlayPause = null;
    TextView textViewTrackPlaying = null;
    ProgressBar progressBar = null;
    MediaPlayer mediaPlayer = null;

    int imagePlay = R.drawable.baseline_play_arrow_white_36;
    int imagePause = R.drawable.baseline_pause_white_36;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Titres");
        }

        final ImageView imageViewAlbumCover = findViewById(R.id.imageViewAlbumCover);

        recyclerView = findViewById(R.id.recycler_view_track);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle b = getIntent().getExtras();
        int albumId = b.getInt("albumId");
        searchTracks(albumId);

        String albumCover = b.getString("albumCover");
        Picasso.get().load(albumCover).into(imageViewAlbumCover);

        imageButtonPlayPause = findViewById(R.id.imageButtonPlayPause);
        imageButtonPlayPause.setImageResource(imagePlay);
        imageButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageButtonPlayPause.setImageResource(imagePlay);
                } else {
                    mediaPlayer.start();
                    imageButtonPlayPause.setImageResource(imagePause);
                }
            }
        });

        textViewTrackPlaying = findViewById(R.id.textViewTrackPlaying);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageButtonPlayPause.setImageResource(imagePlay);
                mediaPlayer.seekTo(0);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_PRECISION);

        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                    float progress = (float)mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration() * PROGRESS_PRECISION;
                    progressBar.setProgress((int)progress);
                }
            }
        }, 0, 10);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchTracks(int albumId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.deezer.com/album/" + albumId + "/tracks";
        Log.d(TAG, "searchTracks url : " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                // Run view-related code back on the main thread
                TrackListActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            Log.d(TAG, "JSON : " + json.toString());

                            Gson gson = new Gson();
                            DataSearchTrack dataSearchTrack = gson.fromJson(json.toString(),
                                    DataSearchTrack.class);

                            tracks = dataSearchTrack.getTracks();
                            Track[] trackArray = new Track[tracks.size()];
                            tracks.toArray(trackArray);
                            TrackAdapter mAdapter = new TrackAdapter(trackArray, onTrackClickListener);
                            recyclerView.setAdapter(mAdapter);

                            loadSong(0);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void loadSong(int position) {
        Track trackToPlay = tracks.get(position);
        textViewTrackPlaying.setText(trackToPlay.getTitle());
        imageButtonPlayPause.setImageResource(imagePlay);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(trackToPlay.getPreview());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackClick(int position) {
        loadSong(position);
        mediaPlayer.start();
        imageButtonPlayPause.setImageResource(imagePause);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imageButtonPlayPause.setImageResource(imagePlay);
        }
    }
}