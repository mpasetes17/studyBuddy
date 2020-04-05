/* 
This is class models information needed for a user to login and logout of to the site.

Note: The Login Servlet returns a JSON response modeled like this class. 

TODO down the road: Add some exception handling for some methods.       
*/

package com.google.sps.models.authmodels;

import java.lang.String;

public final class AuthInfo{

    private Boolean isLoggedIn;
    private String url;
    private String email;
    private String school;

    // Constructor
    public AuthInfo(){
        this.isLoggedIn = false;
        this.url = "N/A";
        this.email = "N/A";
        this.school = "N/A";
    }

    // Copy constructor
    public AuthInfo(AuthInfo authInfo){
        this.isLoggedIn = authInfo.getIsLoggedIn();
        this.url = authInfo.getUrl();
        this.email = authInfo.getEmail();
        this.school = authInfo.getSchool();
    }

    // Additional constructor that takes specific arguments.
    public AuthInfo(Boolean isLoggedIn, String url, String email){
        if(!validEmail(email)){return;}
        this.isLoggedIn = isLoggedIn;
        this.url = url.trim();
        this.email = email.trim();
        this.school = email.substring(email.indexOf("@"), email.indexOf(".")).trim();
    }

    // An additional constructor (Should only be called if the user is logged out)
    // Takes the login url as an argument 
    public AuthInfo(String loginURL){
        this.isLoggedIn = false;
        this.url = loginURL;
    }

    public void setEmail(String email){
        if(!validEmail(email)){return;}
        this.email = email;
        this.school = email.substring(email.indexOf("@"), email.indexOf(".")).trim();
    }
    public String getEmail(){
        return this.email;
    }

    public void setIsLoggedIn(Boolean isLoggedIn){
        this.isLoggedIn = isLoggedIn;
    }
    public Boolean getIsLoggedIn(){
        return this.isLoggedIn;
    } 

    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }

    public String getSchool(){
        return this.school;
    }

    private Boolean validEmail(String email){
        return email.contains("@") && email.contains(".");
    }
}