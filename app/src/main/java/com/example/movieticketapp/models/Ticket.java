package com.example.movieticketapp.models;

public class Ticket {
    private String id;
    private String userId;
    private String showtimeId;
    private String movieTitle;
    private String moviePoster;
    private String seatNumber;
    private double totalPrice;
    private long timestamp;

    public Ticket() {}

    public Ticket(String id, String userId, String showtimeId, String movieTitle, String moviePoster, String seatNumber, double totalPrice, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.seatNumber = seatNumber;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getMoviePoster() { return moviePoster; }
    public void setMoviePoster(String moviePoster) { this.moviePoster = moviePoster; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}