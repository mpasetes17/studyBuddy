// This is a Student class model 

package com.google.sps.models.entitymodels;
import com.google.sps.models.authmodels.AuthInfo;

public final class Student{
    private String nickname;
    private String email;
    private String school;
    private String subject;
    private int privacy_level;
    private long timestamp;

    public Student(){};
 
    public Student(String nickname, String email, String school, String subject, int privacy_level, long timestamp){
        this.nickname = nickname;
        this.email = email;
        this.school = school;
        this.subject = subject;
        this.timestamp = timestamp;
        this.privacy_level = privacy_level;
    }

    public String getNickname(){return this.nickname;}

    public String getEmail(){return this.email;}

    public String getSchool(){return this.school;}

    public String getSubject(){return this.subject;}

    public long getTimestamp(){return this.timestamp;}

    public int getPrivacyLevel(){return this.privacy_level;}
}