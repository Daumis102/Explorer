package com.example.daumantas.explorer;

import java.util.ArrayList;

public class Place {
    private String title, thumbnailUrl;
    private double rating;
    private ArrayList<String> goodFor;
    private String description, lat, lng, hint1 = "", hint2 = "", hint3 = "", name, id, imageFolder;

    public Place() {
    }

    public Place(String name, String thumbnailUrl, double rating,
                 ArrayList<String> goodFor) {
        this.title = name;
        this.goodFor = goodFor;
        this.thumbnailUrl = thumbnailUrl;
        this.rating = rating;
    }


    public String getGoodForString(){
        StringBuilder builder = new StringBuilder();
        for(String s : goodFor) {
            builder.append(s);
        }
        String str = builder.toString();
        return str;
    }
    public String getHint1() {
        return hint1;
    }

    public String getHint2() {
        return hint2;
    }

    public String getHint3() {
        return hint3;
    }

    public String getName() {
        return name;
    }

    public String getImageFolder(){ return imageFolder; }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setImageFolder(String url) { this.imageFolder = url; }

    public void setLng(String lng) {this.lng = lng;}

    public void setHint1(String hint1) {
        this.hint1 = hint1;
    }

    public void setHint2(String hint2) {
        this.hint2 = hint2;
    }

    public void setHint3(String hint3) {
        this.hint3 = hint3;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
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