package com.example.deezerandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.deezerandroid.model.Album;
import com.example.deezerandroid.model.Artist;
import com.example.deezerandroid.model.DataSearchAlbum;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlbumListActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumClickListener {

    private static String TAG = "AlbumListActivity";
    private RecyclerView recyclerView;
    private List<Album> albums;
    final AlbumAdapter.OnAlbumClickListener onAlbumClickListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);

        recyclerView = findViewById(R.id.recycler_view_album);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle b = getIntent().getExtras();
        String artistName = b.getString("artistName");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Albums de " + artistName);
        }

        int artistId = b.getInt("artistId");
        searchAlbums(artistId);
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

    private void searchAlbums(int artistId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.deezer.com/artist/" + artistId + "/albums";
        Log.d(TAG, "searchAlbums url : " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                // Run view-related code back on the main thread
                AlbumListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            Log.d(TAG, "JSON : " + json.toString());

                            Gson gson = new Gson();
                            DataSearchAlbum dataSearchAlbum= gson.fromJson(json.toString(),
                                    DataSearchAlbum.class);

                            albums = dataSearchAlbum.getAlbums();
                            Album[] albumArray = new Album[albums.size()];
                            albums.toArray(albumArray);
                            AlbumAdapter mAdapter = new AlbumAdapter(albumArray, onAlbumClickListener);
                            recyclerView.setAdapter(mAdapter);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onAlbumClick(int position) {
        Album selectedAlbum = albums.get(position);
        Integer albumId = selectedAlbum.getId();
        Intent intent = new Intent(this, TrackListActivity.class);
        Bundle b = new Bundle();
        b.putInt("albumId", albumId);
        b.putString("albumCover", selectedAlbum.getCoverBig());
        intent.putExtras(b);
        startActivity(intent);
    }
}