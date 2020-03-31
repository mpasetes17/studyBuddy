package com.google.sps.servlets;

import com.google.gson.Gson; 
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.models.authmodels.AuthInfo;

// This Servlets takes care of user authentication
@WebServlet("/login")
public class LoginServlet extends HttpServlet{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException{

        // Sets up response type and userService variable   
        response.setContentType("application/json"); 
        UserService userService = UserServiceFactory.getUserService(); 


        if(userService.isUserLoggedIn()){
            // Grabs and return logout information in JSON

            String email = userService.getCurrentUser().getEmail();
            String logout_url = userService.createLogoutURL("/");
            final String LOGIN_INFO = convertToJson(new AuthInfo(true, logout_url, email));    
            response.getWriter().println(LOGIN_INFO);
        }else{
            // Grabs and return login information in JSON

            String login_url = userService.createLoginURL("/");
            final String LOGIN_INFO = convertToJson(new AuthInfo(login_url));
            response.getWriter().println(LOGIN_INFO);
        }
    }

/*************************************************
Takes an ArrayList and returns a Json String
based on the Arraylist.
************************************************/
  private String convertToJson(AuthInfo info){
      Gson gson = new Gson();
      return gson.toJson(info);
  }
}
