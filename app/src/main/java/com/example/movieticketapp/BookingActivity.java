package com.example.movieticketapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.movieticketapp.models.Movie;
import com.example.movieticketapp.models.Showtime;
import com.example.movieticketapp.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private TextView tvMovie, tvTime, tvSelectedSeat, tvTotalPrice;
    private GridLayout glSeats;
    private Button btnConfirm;
    private List<String> selectedSeatsList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Movie movie;
    private Showtime showtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        movie = (Movie) getIntent().getSerializableExtra("movie");
        showtime = (Showtime) getIntent().getSerializableExtra("showtime");

        tvMovie = findViewById(R.id.tvBookingMovie);
        tvTime = findViewById(R.id.tvBookingTime);
        tvSelectedSeat = findViewById(R.id.tvSelectedSeat);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        glSeats = findViewById(R.id.glSeats);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        if (movie != null) tvMovie.setText(movie.getTitle());
        if (showtime != null) tvTime.setText("Suất chiếu: " + showtime.getTime());

        setupSeats();

        btnConfirm.setOnClickListener(v -> {
            if (selectedSeatsList.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
            } else {
                saveTicket();
            }
        });
    }

    private void setupSeats() {
        for (int i = 0; i < 20; i++) {
            Button seatBtn = new Button(this);
            String seatName = (char)('A' + (i / 4)) + String.valueOf((i % 4) + 1);
            seatBtn.setText(seatName);
            seatBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.card_background));
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            seatBtn.setLayoutParams(params);

            seatBtn.setOnClickListener(v -> {
                if (selectedSeatsList.contains(seatName)) {
                    selectedSeatsList.remove(seatName);
                    seatBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.card_background));
                } else {
                    selectedSeatsList.add(seatName);
                    seatBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
                }
                updateUI();
            });
            glSeats.addView(seatBtn);
        }
    }

    private void updateUI() {
        String selectedStr = String.join(", ", selectedSeatsList);
        tvSelectedSeat.setText("Ghế: " + (selectedStr.isEmpty() ? "Chưa chọn" : selectedStr));
        double total = selectedSeatsList.size() * (showtime != null ? showtime.getPrice() : 0);
        tvTotalPrice.setText("Tổng: " + String.format("%,.0f VNĐ", total));
    }

    private void saveTicket() {
        if (mAuth.getCurrentUser() == null) return;

        String ticketId = db.collection("tickets").document().getId();
        Ticket ticket = new Ticket(
                ticketId,
                mAuth.getCurrentUser().getUid(),
                showtime.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                String.join(", ", selectedSeatsList),
                selectedSeatsList.size() * showtime.getPrice(),
                new Date().getTime()
        );

        db.collection("tickets").document(ticketId).set(ticket)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BookingActivity.this, "Đặt vé thành công!", Toast.LENGTH_LONG).show();
                    sendConfirmationNotification();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendConfirmationNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "movie_notifications";
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Thông báo phim", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Đặt vé thành công! 🍿")
                .setContentText("Phim " + movie.getTitle() + " lúc " + showtime.getTime() + ". Xem vui vẻ!")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}