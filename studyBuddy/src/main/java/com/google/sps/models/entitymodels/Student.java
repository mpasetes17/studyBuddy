// This is a Student class model 

package com.google.sps.models.entitymodels;

public final class Student{
    private String firstName;
    private String lastName;
    private String school;
    private String subject;
    private long timestamp;
 
    public Student(String firstName, String lastName, String school, String subject, long timestamp){
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
        this.subject = subject;
        this.timestamp = timestamp;
    }
}