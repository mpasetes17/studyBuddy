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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

async function getUser() {
    // Fetches Authentication info
    const response = await fetch('/login');
    const userLoginInfo = await response.json();
    const email = userLoginInfo.email; 
    

    //Determines if the user is logged in or not
    if(userLoginInfo.isLoggedIn){
        userInfo = document.getElementById("user-container")
        userInfo.innerText = email + "\n";

        hellomsg = document.getElementById("hello-msg")
        hellomsg.innerHTML =
            "<p>&emsp;Hi, " + email.substring(0, email.indexOf("@")) + "</p>";

        logout = document.getElementById("login-btn")
        logout.innerHTML= "<a href=\"" + userLoginInfo.url + "\">" +
            "Log Out</a>";
    }
    else{
        searchForm = document.getElementById("search-form")
        html = "<p>Click <a href=\"" + userLoginInfo.url + "\">" +
            "HERE</a> to log in before performing a search</p>";
        searchForm.innerHTML = html;

        login = document.getElementById("login-btn")
        login.innerHTML= "<a href=\"" + userLoginInfo.url + "\">" +
            "Log In</a>";
    }
}

async function setupLogin() {
    const response = await fetch('/login')
    const userLoginInfo = await response.json()
    const login_btn = document.getElementById('login-btn')

    html = "<a href=\"" + userLoginInfo.url + "\">";
    if(userLoginInfo.isLoggedIn) {
        html += "Log Out";
        const email = userLoginInfo.email;
        hellomsg = document.getElementById("hello-msg")
        hellomsg.innerHTML =
            "<p>&emsp;Hi, " + email.substring(0, email.indexOf("@")) + "</p>";
    }
    else {
        html += "Log In";
    }
    login_btn.innerHTML = html + "</a>";
}
