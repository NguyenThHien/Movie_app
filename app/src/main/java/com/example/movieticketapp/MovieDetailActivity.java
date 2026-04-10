package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticketapp.models.Movie;
import com.example.movieticketapp.models.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvTitle, tvGenre, tvDescription;
    private RecyclerView rvShowtimes;
    private ShowtimeAdapter showtimeAdapter;
    private List<Showtime> showtimeList;
    private FirebaseFirestore db;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movie = (Movie) getIntent().getSerializableExtra("movie");
        db = FirebaseFirestore.getInstance();

        ivPoster = findViewById(R.id.ivDetailPoster);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvGenre = findViewById(R.id.tvDetailGenre);
        tvDescription = findViewById(R.id.tvDetailDescription);
        rvShowtimes = findViewById(R.id.rvShowtimes);

        if (movie != null) {
            tvTitle.setText(movie.getTitle());
            tvGenre.setText(movie.getGenre());
            tvDescription.setText(movie.getDescription());
            Glide.with(this).load(movie.getPosterUrl()).into(ivPoster);
            
            loadShowtimes(movie.getId());
        }

        showtimeList = new ArrayList<>();
        showtimeAdapter = new ShowtimeAdapter(showtimeList, showtime -> {
            Intent intent = new Intent(MovieDetailActivity.this, BookingActivity.class);
            intent.putExtra("showtime", showtime);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });

        rvShowtimes.setLayoutManager(new GridLayoutManager(this, 3));
        rvShowtimes.setAdapter(showtimeAdapter);
    }

    private void loadShowtimes(String movieId) {
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showtimeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Showtime st = document.toObject(Showtime.class);
                            st.setId(document.getId());
                            showtimeList.add(st);
                        }
                        showtimeAdapter.notifyDataSetChanged();
                        
                        if (showtimeList.isEmpty()) {
                            seedShowtimes(movieId);
                        }
                    }
                });
    }

    private void seedShowtimes(String movieId) {
        Showtime s1 = new Showtime(null, movieId, "theater1", "19:30", 85000);
        Showtime s2 = new Showtime(null, movieId, "theater1", "21:00", 85000);
        db.collection("showtimes").add(s1);
        db.collection("showtimes").add(s2).addOnSuccessListener(ref -> loadShowtimes(movieId));
    }
}