package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "CineflexApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        rvMovies = findViewById(R.id.rvMovies);
        ImageView ivLogout = findViewById(R.id.ivLogout);
        ImageView ivMyTickets = findViewById(R.id.ivMyTickets);
        TextView tvCategory = findViewById(R.id.tvCategory);

        tvCategory.setText(R.string.now_playing);

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(movieList, movie -> {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });

        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(movieAdapter);

        ivLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        ivMyTickets.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MyTicketsActivity.class));
        });

        loadMovies();
    }

    private void loadMovies() {
        db.collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        movieList.clear();
                        if (task.getResult().isEmpty()) {
                            seedData();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Movie movie = document.toObject(Movie.class);
                                movie.setId(document.getId());
                                movieList.add(movie);
                            }
                            movieAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Lỗi Firestore: " + task.getException().getMessage());
                    }
                });
    }

    private void seedData() {
        Movie m1 = new Movie(null, "Avengers: Hồi Kết", "Sau các sự kiện thảm khốc của Infinity War, các siêu anh hùng còn sống sót cố gắng xoay chuyển tình thế.", "https://image.tmdb.org/t/p/w500/or06vSsbTkaft7v03vUvNQpST1O.jpg", "Hành động", 181);
        Movie m2 = new Movie(null, "Vua Sư Tử", "Câu chuyện về cuộc đời của chú sư tử trẻ Simba trên hành trình giành lại ngai vàng.", "https://image.tmdb.org/t/p/w500/dzBTnmoqz2wc6Wp6slS6e0BPnS9.jpg", "Hoạt hình", 118);
        
        db.collection("movies").add(m1).addOnSuccessListener(documentReference -> {
            db.collection("movies").add(m2).addOnSuccessListener(docRef -> loadMovies());
        });
    }
}