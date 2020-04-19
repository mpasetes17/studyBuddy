// This is a Student class model 

package com.google.sps.models.entitymodels;
import com.google.sps.models.authmodels.AuthInfo;

public final class Student{
    private String nickname;
    private String firstName;
    private String lastName;
    private String email;
    private String school;
    private String subject;
    private long privacy_level;
    private long timestamp;

    public Student(){};
 
    public Student(String nickname, String firstName, String lastName, String email, String school, String subject, long privacy_level, long timestamp){
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.school = school;
        this.subject = subject;
        this.timestamp = timestamp;
        this.privacy_level = privacy_level;
    }

    public String getNickname(){return this.nickname;}

    public String getEmail(){return this.email;}

    public String getFirstName(){return this.firstName;}

    public String getLastName(){return this.lastName;}

    public String getSchool(){return this.school;}

    public String getSubject(){return this.subject;}

    public long getTimestamp(){return this.timestamp;}

    public long getPrivacyLevel(){return this.privacy_level;}
}