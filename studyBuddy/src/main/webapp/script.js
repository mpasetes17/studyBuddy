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
 * Fetches the information to put in the html from our data
 */
function getTable() {
    fetch('/matches', {method: "POST"}).then(response => response.json()).then((myData) => {
        const tableContainer = document.getElementById('search-results');
        console.log("myData in getTable()" + myData);
        tableContainer.innerText = ("");
        for(i=0; i<myData.length; i++){
          tableContainer.innerText += (myData[i].firstName + " " + myData[i].lastName + " " + myData[i].school + " " + myData[i].subject + "\n");
        }
        });
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
/*
 * Prevents the page from redirecting upon submitting their subject of choice
 */
function pageSetup(){
    getUser();
    document.getElementById("search-button").addEventListener("click", function(event){
        event.preventDefault();
        getTable();
    });
}
