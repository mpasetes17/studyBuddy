package com.google.sps.models.entitymodels;

public final class Profile {
    private String firstName;
    private String lastName;
    private String school;
    private String email;
    private long timestamp;

    public Profile( String firstName,
                    String lastName,
                    String school,
                    String email,
                    long timestamp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
        this.email = email;
        this.timestamp = timestamp;
    }
}