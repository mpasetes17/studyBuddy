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
import com.google.gson.Gson;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Hello world!</h1>");
  }
  
  //returns proper info from dataStore
  @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //creates entity
        //gets stuff from data store iteratively
        System.out.println("is this being called?");
        String returnJSON = convertToJsonUsingGson("Stephanie Hernandez Math sgh005@ucsd.edu");
        // Send the JSON as the response
        response.setContentType("application/json;");
        response.getWriter().println(returnJSON);
    }
  /**
   * Converts an String (for now) into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String convertToJsonUsingGson(String myString) {
    Gson gson = new Gson();
    String json = gson.toJson(myString);
    return json;
  }
}

