package com.example.acg.onlyvoiceapp;

public class Likes {

    private String userKey;
    private String postKey;

    public Likes(){

    }

    public Likes(String userKey, String postKey) {
        this.userKey = userKey;
        this.postKey = postKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getPostKey() {
        return postKey;
    }
}
