package com.example.movieticketapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticketapp.models.Ticket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.tvMovie.setText(ticket.getMovieTitle());
        holder.tvSeats.setText("Ghế: " + ticket.getSeatNumber());
        holder.tvPrice.setText("Tổng tiền: " + String.format("%,.0f VNĐ", ticket.getTotalPrice()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText("Ngày đặt: " + sdf.format(new Date(ticket.getTimestamp())));
        
        Glide.with(holder.itemView.getContext())
                .load(ticket.getMoviePoster())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovie, tvSeats, tvPrice, tvDate;
        ImageView ivPoster;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovie = itemView.findViewById(R.id.tvTicketMovie);
            tvSeats = itemView.findViewById(R.id.tvTicketSeats);
            tvPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvDate = itemView.findViewById(R.id.tvTicketDate);
            ivPoster = itemView.findViewById(R.id.ivTicketPoster);
        }
    }
}