package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson; 

import com.google.sps.models.entitymodels.Profile;
import com.google.sps.models.authmodels.AuthInfo;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private String QueryDataStore(String email, DatastoreService datastore) {
        Filter email_filter = new FilterPredicate("email", FilterOperator.EQUAL, email);
        Query query = new Query("Profile")
                            .setFilter(email_filter)
                            .addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery profilesFound = datastore.prepare(query);
        List<Entity> resultList = profilesFound.asList(FetchOptions.Builder.withLimit(1));
        if(resultList.size() != 1)
            return null;

        Profile profile = createProfileFromEntity(resultList.get(0));
        Gson gson = new Gson();
        return gson.toJson(profile);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String firstName = getParameter(request, "first_name", "null"); 
        String lastName = getParameter(request, "last_name", "null");                   
        String school = getParameter(request, "school", "null");
        String action = getParameter(request, "action", "findProfile");

        if(action.equalsIgnoreCase("createProfile")) {
            createProfile(request, response);
        }
        else {
            findProfile(request, response);
        }
    }

    private void findProfile(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
            response.getWriter().println("{\"result\":" + "\"no profile found\"}");
        }
        else
            response.getWriter().println("{\"result\":" + "\"no profile found\"}");    
    }

    private void createProfile(HttpServletRequest request, HttpServletResponse response) throws IOException{
        UserService userService = UserServiceFactory.getUserService();
        if(userService.isUserLoggedIn()){
            String email = userService.getCurrentUser().getEmail();
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
            AuthInfo loginInfo = new AuthInfo();
            loginInfo.setEmail(email);
            final String firstName = getParameter(request, "first-name", "Study");
            final String lastName = getParameter(request, "last-name", "Buddy");
            final String school = getParameter(request, "school", "???");
            Entity profile = createProfileEntity(firstName, lastName, school, loginInfo);
            datastore.put(profile);
            response.setContentType("application/json;");
            response.getWriter().println(convertToJson(profile));
        }
        else
            response.getWriter().println("{\"result\":" + "\"no profile found\"}");  
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

    private Profile createProfileFromEntity(Entity profile) {
        final String first_name = (String)profile.getProperty("first_name"); 
        final String last_name = (String)profile.getProperty("last_name");
        final String email = (String)profile.getProperty("email");
        final String school = (String)profile.getProperty("school");
        final long timestamp = (long)profile.getProperty("timestamp");

        return new Profile(first_name, last_name, school, email, timestamp);
    }

    private String convertToJson(Entity profile){
        Gson gson = new Gson();
        return gson.toJson(profile);
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
