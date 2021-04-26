package com.example.deezerandroid;

import android.content.Intent;
import android.os.Bundle;

import com.example.deezerandroid.model.Artist;
import com.example.deezerandroid.model.DataSearchArtist;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.Menu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ArtistAdapter.OnArtistClickListener {

    private static String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private List<Artist> artists;
    final ArtistAdapter.OnArtistClickListener onArtistClickListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Recherche Artistes");
        }

        recyclerView = findViewById(R.id.recycler_view_artist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit:" + query);
                searchArtists(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange:" + newText);
                searchArtists(newText);
                return false;
            }
        });

        return true;
    }

    private void searchArtists(final String artist) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.deezer.com/search/artist?q=" + artist;
        Log.d(TAG, "searchArtist url : " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {



                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            Log.d(TAG, "JSON : " + json.toString());

                            Gson gson = new Gson();
                            DataSearchArtist dataSearchArtist = gson.fromJson(json.toString(),
                                    DataSearchArtist.class);

                            artists = dataSearchArtist.getArtists();
                            if (artists != null && artists.size() > 0) {
                                Artist[] artistArray = new Artist[artists.size()];
                                artists.toArray(artistArray);
                                ArtistAdapter mAdapter = new ArtistAdapter(artistArray, onArtistClickListener);
                                recyclerView.setAdapter(mAdapter);
                            } else {
                                recyclerView.setAdapter(null);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

    @Override
    public void onArtistClick(int position) {
        Artist selectedArtist = artists.get(position);
        Integer artistId = selectedArtist.getId();
        Intent intent = new Intent(this, AlbumListActivity.class);
        Bundle b = new Bundle();
        b.putInt("artistId", artistId);
        b.putString("artistName", selectedArtist.getName());
        intent.putExtras(b);
        startActivity(intent);
    }
}