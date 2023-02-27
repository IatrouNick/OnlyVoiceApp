package com.example.acg.onlyvoiceapp;

public class Users {
    private String email;
    private String firstName;
    private String lastName;

    // Default constructor required for calls to DataSnapshot.getValue(Users.class)
    public Users() {}

    public Users(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
