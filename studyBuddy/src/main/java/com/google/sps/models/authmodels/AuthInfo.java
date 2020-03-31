/* 
This is class models information needed for a user to login and logout of to the site.

Note: The Login Servlet returns a JSON response modeled like this class. 

TODO: Add a method that generates the students's school from their email.       
*/

package com.google.sps.models.authmodels;

public final class AuthInfo{

    private Boolean isLoggedIn;
    private String url;
    private String email;

    // Constructor
    public AuthInfo(){
        this.isLoggedIn = false;
        this.url = "N/A";
        this.email = "N/A";
    }

    // Copy constructor
    public AuthInfo(Boolean isLoggedIn, String url, String email){
        this.isLoggedIn = isLoggedIn;
        this.url = url;
        this.email = email;
    }

    // An additional constructor (Should only be called if the user is logged out)
    // Takes the login url as an argument 
    public AuthInfo(String loginURL){
        this.isLoggedIn = false;
        this.email = "N/A";
        this.url = loginURL;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }

    public void setLoginState(Boolean loginState){
        this.isLoggedIn = loginState;
    }

    public void setUrl(String url){
        this.url = url;
    }
}