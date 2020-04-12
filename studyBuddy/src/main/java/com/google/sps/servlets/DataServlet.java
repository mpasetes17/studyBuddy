
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
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Integer;
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
private String QueryDataStore(Student student, DatastoreService datastore){
    // Prepare Filters
    Filter by_subject = new FilterPredicate("subject", FilterOperator.EQUAL, student.getSubject());
    Filter by_school = new FilterPredicate("school", FilterOperator.EQUAL, student.getSchool());
    Filter by_school_and_subject = CompositeFilterOperator.and(by_school, by_subject);

    // Create query and apply filter conditions
    Query query = new Query("Candidates");
    query.setFilter(by_school_and_subject);
    query.addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query); 

    ArrayList<Student> studentList = new ArrayList<>();

    // Scan results and add them in the studentList
    for (Entity entity: results.asIterable()){
        Student current_student = createStudentFromEntity(entity);
        studentList.add(current_student);
    }

    ArrayList<Student> matches = privacy(studentList, student);

/*
    switch(student.getPrivacyLevel){
        case 1:
        studentList = leveOne(studentList);
        break;
        case 2:
        studentList = leveTwo(studentList);
        break;
        case 3:
        studentList = leveThree(studentList);
        break;
    }
*/

return convertToJson(matches);
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
        final String subject = getSubjectParameter(request, "subject-select", "null");  
        final int privacy_level = getPrivacyParameter(request, "privacy-select", 0);
        Student loggedInStudent = new Student(
            userService.getCurrentUser().getNickname(),
            userService.getCurrentUser().getEmail(),
            AuthInfo.getSchoolFrom(userService.getCurrentUser().getEmail()),  // AuthInfo static method that takes in and returns a school 
            subject, 
            privacy_level, 
            System.currentTimeMillis()
        );

        // Access Datastore Services
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Find matches for the current user
        String matches = QueryDataStore(loggedInStudent, datastore);

        // Create current student entity
        Entity student_entity = createStudentEntity(loggedInStudent);
        
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
private Entity createStudentEntity(Student student){  

    Entity stu_entity = new Entity("Candidates");
    stu_entity.setProperty("nickname", student.getNickname());
    stu_entity.setProperty("email", student.getEmail());
    stu_entity.setProperty("school", student.getSchool());
    stu_entity.setProperty("subject", student.getSubject());
    stu_entity.setProperty("privacy_level", student.getPrivacyLevel());
    stu_entity.setProperty("timestamp", student.getTimestamp());

    return stu_entity;    
}

/****************************************************
Takes a Student entity and returns a Student Object
based on the entity.
*****************************************************/
  private Student createStudentFromEntity(Entity theStudentEntity){
      final String nickname = (String)theStudentEntity.getProperty("nickname");
      final String email = (String)theStudentEntity.getProperty("email");
      final String school = (String)theStudentEntity.getProperty("school");
      final String subject = (String)theStudentEntity.getProperty("subject");
      final long privacy_level = (long)theStudentEntity.getProperty("privacy_level");
      final long timestamp = (long)theStudentEntity.getProperty("timestamp");

      return new Student(nickname, email, school, subject, privacy_level, timestamp);
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
 private String getSubjectParameter(HttpServletRequest request, String value_name, String defaultValue){
      String value = request.getParameter(value_name);
      if(value == ""){
          return defaultValue;
      }
      return value;
  }

/***************************************************
Parameters: 
1. An HttpServletRequest
2. A value_name
3. A default value

Returns the value i.e(privacy level) from the POST request
if defined, otherwise returns default value 
****************************************************/
private int getPrivacyParameter(HttpServletRequest request, String value_name, int defaultValue){
    int value = Integer.parseInt(request.getParameter(value_name));
    if (value < 1 || value > 3){
        return defaultValue;
    }
    return value;
}


private ArrayList<Student> privacy(ArrayList<Student> candidates, Student student){

    if (candidates.isEmpty())
        return candidates;

    ArrayList<Student> selected = new ArrayList<>();
    for (int i= 0; i < candidates.size() && selected.size() <= 7; i++){
        if(candidates.get(i).getPrivacyLevel() == student.getPrivacyLevel()){
        selected.add(candidates.get(i));    
        }   
    }
    return selected;   
}
}

/*
/*************************
Level one pivacy
**************************
List <Student> levelOne(List <Student> candidates){
        List selected = new ArrayList();
        for (int i= 0; i <= 7 || selected.size() <= 7; i++){
            if(candidates[i].getPrivacyLevel() == 1){
                selected.add(candidates[i]);    
            }   
        }   
    }
    return selected;
}

/*************************
Level two pivacy
**************************
List <Student> levelTwo(List <Student> candidates){
    if(candidates.size() <= 7){
        return candidates;
    }else{
        List selected;
        for (int i= 0; i <7; i++){
            if(candidates[i].get)
            selected.add(candidates[i]);
        }
    }
    return selected;
}

*/