package com.example.acg.onlyvoiceapp.Posts;

public class PostLikes {

    private String userKey;
    private String postKey;

    public PostLikes(){

    }

    public PostLikes(String userKey, String postKey) {
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
