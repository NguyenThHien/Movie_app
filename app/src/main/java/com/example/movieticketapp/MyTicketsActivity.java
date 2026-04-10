package com.example.movieticketapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvMyTickets;
    private TicketAdapter ticketAdapter;
    private List<Ticket> ticketList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvMyTickets = findViewById(R.id.rvMyTickets);
        ticketList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(ticketList);

        rvMyTickets.setLayoutManager(new LinearLayoutManager(this));
        rvMyTickets.setAdapter(ticketAdapter);

        loadMyTickets();
    }

    private void loadMyTickets() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("tickets")
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ticketList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ticket ticket = document.toObject(Ticket.class);
                            ticketList.add(ticket);
                        }
                        ticketAdapter.notifyDataSetChanged();
                        if (ticketList.isEmpty()) {
                            Toast.makeText(this, "Bạn chưa có vé nào!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("MyTickets", "Error: " + task.getException().getMessage());
                    }
                });
    }
}