// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson; 

import com.google.sps.models.entitymodels.Student;
import com.google.sps.models.authmodels.AuthInfo;

@WebServlet("/matches")
public class DataServlet extends HttpServlet {

/******************************************************************
Queries DataStore and returns a Json formatted String of the result
********************************************************************/
private String QueryDataStore(String subject, DatastoreService datastore, AuthInfo loggedInUserInfo){
    
    // Prepare Filters
    Filter by_subject = new FilterPredicate("subject", FilterOperator.EQUAL, subject);
    Filter by_school = new FilterPredicate("school", FilterOperator.EQUAL, loggedInUserInfo.getSchool());
    Filter by_school_and_subject = CompositeFilterOperator.and(by_school, by_subject);

    // Create query and apply filter conditions
    Query query = new Query("Student");
    query.setFilter(by_school_and_subject);
    query.addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query); 

    ArrayList <Student> studentList = new ArrayList<>();

    // Scan results and add them in the studentList
    for (Entity entity: results.asIterable()){
        Student current_student = createStudentFromEntity(entity);
        studentList.add(current_student);
    }

return convertToJson(studentList);
}

/********************
Handles POST request
********************/
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    
    // Get Authentic User's Loggin Information
    UserService userService = UserServiceFactory.getUserService();

    if(userService.isUserLoggedIn()){

        // Prepares all needed information                       
        String subject = getParameter(request, "subject-select", "null");  // NOTE: (Query filters students by this subject)
        AuthInfo loginInfo = new AuthInfo();
        loginInfo.setEmail(userService.getCurrentUser().getEmail());

        // Access Datastore Services
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Find matches for the current user
        String matches = QueryDataStore(subject, datastore, loginInfo);

        // Create current student entity
        Entity student_entity = createStudentEntity(subject, loginInfo);
        
        // Append current student in datastore
        datastore.put(student_entity);

        // Return a JSON response of the results
        response.setContentType("application/json;");
        response.getWriter().println(matches);
    }else{
        String loginLink = userService.createLoginURL("/");
        response.sendRedirect(loginLink);
    }    
  }

/**************************************************
Takes Entity properties and returns a Student entity 
based on the properties.
***************************************************/
private Entity createStudentEntity(String subject, AuthInfo loginInfo){
    // TODO: replace hardcoded school with real ones  

    Entity stu_entity = new Entity("Student");
    stu_entity.setProperty("first_name", "NULL");
    stu_entity.setProperty("last_name", "NULL");
    stu_entity.setProperty("email", loginInfo.getEmail());
    stu_entity.setProperty("school", loginInfo.getSchool());
    stu_entity.setProperty("subject", subject);
    stu_entity.setProperty("timestamp", System.currentTimeMillis());

    return stu_entity;    
}

/****************************************************
Takes a Student entity and returns a Student Object
based on the entity.
*****************************************************/
  private Student createStudentFromEntity(Entity theStudentEntity){
      final String first_name = (String)theStudentEntity.getProperty("first_name"); 
      final String last_name = (String)theStudentEntity.getProperty("last_name");
      final String email = (String)theStudentEntity.getProperty("email");
      final String school = (String)theStudentEntity.getProperty("school");
      final String subject = (String)theStudentEntity.getProperty("subject");
      final long timestamp = (long)theStudentEntity.getProperty("timestamp");

      return new Student(first_name, last_name, school, subject, timestamp);
  }

/*************************************************
Takes an ArrayList and returns a Json String
based on the Arraylist.
************************************************/
  private String convertToJson(ArrayList list){
      Gson gson = new Gson();
      return gson.toJson(list);
  }

/***************************************************
Parameters: 
1. An HttpServletRequest
2. A value_name
3. A default value

Returns the value i.e(Subject) from the POST request
if defined, otherwise returns default value 
****************************************************/
 private String getParameter(HttpServletRequest request, String value_name, String defaultValue){
      String value = request.getParameter(value_name);
    
      if(value == ""){
          return defaultValue;
      }else return value;
  }
}
