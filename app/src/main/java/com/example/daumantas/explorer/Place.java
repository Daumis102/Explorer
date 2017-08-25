package com.example.daumantas.explorer;

import java.util.ArrayList;

public class Place {
    private String title, thumbnailUrl;
    private double rating;
    private ArrayList<String> goodFor;

    public Place() {
    }

    public Place(String name, String thumbnailUrl, double rating,
                 ArrayList<String> goodFor) {
        this.title = name;
        this.goodFor = goodFor;
        this.thumbnailUrl = thumbnailUrl;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getGoodFor() {
        return goodFor;
    }

    public void setGoodFor(ArrayList<String> goodFor) {
        this.goodFor = goodFor;
    }

}