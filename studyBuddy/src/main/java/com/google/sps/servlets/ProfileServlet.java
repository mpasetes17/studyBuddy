package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson; 

import com.google.sps.models.entitymodels.Student;
import com.google.sps.models.authmodels.AuthInfo;

@WebServlet("/profile")
public class DataServlet extends HttpServlet {

    private String QueryDataStore(String email, DatastoreService datastore) {
        Filter email_filter = new FilterPredicate("email", FilterOperator.EQUAL, email);
        Query query = Query.newEntityQueryBuilder()
                                .setKind("Profile")
                                .setLimit(1)
                           .build();
        query.setFilter(email_filter);
        query.addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        if(results == null)
            return null;

        Profile profile = createProfileFromEntity(results.asSingleEntity());
        Gson gson = new Gson();
        return gson.toJson(profile);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String firstName = getParameter(request, "first_name", "null"); 
        String lastName = getParameter(request, "last_name", "null");                   
        String school = getParameter(request, "school", "null");
        String action = getParameter(request, "action", "findProfile");

        if(action = "createProfile")
            createProfile(request, response);
        else
            findProfile(response, response);
    }

    private void findProfile(HttpServletRequest request, HttpServletResponse response) {
        UserService userService = UserServiceFactory.getUserService();
        if(userService.isUserLoggedIn()){

            String email = userService.getCurrentUser().getEmail();
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            String existingProfile = QueryDataStore(email, datastore);
            if (existingProfile != null) {
                response.setContentType("application/json;");
                response.getWriter().println(existingProfile);
                return;
            }
            response.getWriter().println("no profile found");
        }
        else
            response.getWriter().println("need to be logged in");    
    }

    private void createProfile(HttpServletRequest request, HttpServletResponse response) {
        UserService userService = UserServiceFactory.getUserService();
        if(userService.isUserLoggedIn()){

            String email = userService.getCurrentUser().getEmail();
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            String existingProfile = QueryDataStore(email, datastore);
            if (existingProfile != null) {
                response.setContentType("application/json;");
                response.getWriter().println(existingProfile);
                return;
            }
        
            AuthInfo loginInfo = new AuthInfo();
            loginInfo.setEmail(email);
            Entity profile = createProfileEntity(subject, loginInfo);
            datastore.put(profile);
            String createdProfile = QueryDataStore(email, datastore);
            response.setContentType("application/json;");
            response.getWriter().println(createdProfile);
        }
        else
            response.getWriter().println("need to be logged in");  
    }

    private Entity createProfileEntity( String firstName,
                                        String lastName,
                                        String school,
                                        AuthInfo loginInfo) {
        Entity profile = new Entity("Profile");
        profile.setProperty("first_name", firstName);
        profile.setProperty("last_name", lastName);
        profile.setProperty("email", loginInfo.getEmail());
        profile.setProperty("school", school);
        profile.setProperty("timestamp", System.currentTimeMillis());

        return profile;    
    }

    private Student createProfileFromEntity(Entity profile) {
        final String first_name = (String)theStudentEntity.getProperty("first_name"); 
        final String last_name = (String)theStudentEntity.getProperty("last_name");
        final String email = (String)theStudentEntity.getProperty("email");
        final String school = (String)theStudentEntity.getProperty("school");
        final long timestamp = (long)theStudentEntity.getProperty("timestamp");

        return new Profile(first_name, last_name, school, subject, timestamp);
    }

    private String convertToJson(ArrayList list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    private String getParameter(HttpServletRequest request, String value_name, String defaultValue){
        String value = request.getParameter(value_name);
    
        if(value == ""){
            return defaultValue;
        }
        else 
            return value;
    }
}
