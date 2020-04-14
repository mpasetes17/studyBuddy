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
    const sub = document.getElementById("subject-select").value;
    const privacy = document.getElementById("privacy-select").value;
    const searchUrl = "subject=" + sub + "&privacy-select=" + privacy;
    fetch('/matches',
        {  
            method: "POST",
            body: searchUrl,
            headers: { 'Content-type': 'application/x-www-form-urlencoded' }
         })
         .then(response => response.json()).then((myData) => {
        const tableContainer = document.getElementById('search-results');
        console.log("myData in getTable()" + myData);
        tableContainer.innerText = ("");
        for(i=0; i<myData.length; i++){
          tableContainer.innerText += (myData[i].nickname + " " + myData[i].email + " " + myData[i].school + " " + myData[i].timestamp + "\n");
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

async function loginMenu() {
    // Fetches Authentication info
    const response = await fetch('/login');
    const userLoginInfo = await response.json();
    const email = userLoginInfo.email; 
    

    //Determines if the user is logged in or not
    if(userLoginInfo.isLoggedIn){
        hellomsg = document.getElementById("hello-msg")
        hellomsg.innerHTML =
            "<p>&emsp;Hi, " + email.substring(0, email.indexOf("@")) + "</p>";

        logout = document.getElementById("login-btn")
        logout.innerHTML= "<a href=\"" + userLoginInfo.url + "\">" +
            "Log Out</a>";
    }
    else{
        login = document.getElementById("login-btn")
        login.innerHTML= "<a href=\"" + userLoginInfo.url + "\">" +
            "Log In</a>";
    }
}

async function getProfile() {
    const response = await fetch('/login')
    const userLoginInfo = await response.json()
    loginMenu()
    if(userLoginInfo.isLoggedIn) {
        fetch('/profile',
            {  
                method: "POST",
                body: "first_name=?&last_name=?&school=?&action=findProfile",
                headers: { 'Content-type': 'application/x-www-form-urlencoded' }
            })
                .then(response => response.json()).then((myData) => {
            const profileTable = document.getElementById('profile-data');
            console.log(response);
            if(myData.result == "no profile found") {
                console.log("no profile");
                window.location.href = "createProfile.html";
            }
            else {
                const html =  "<center><h2>" + myData.firstName + " " + 
                                myData.lastName + "<h2></center>" +
                              "<center><h2>" + myData.school + "<h2></center>" +
                              "<a href=\"createProfile.html\">edit profile</a>";   
                profileTable.innerHTML = html;
            }
        });
    }
    else {
        window.location.href = userLoginInfo.url;
    }
}

function setupCreate(){
    loginMenu()
    document.getElementById("create-button").addEventListener("click", function(event){
        event.preventDefault();
        createProfile();
    });
}

async function createProfile() {
    const response = await fetch('/login')
    const userLoginInfo = await response.json()
    loginMenu()

    const firstName = document.getElementById("first-name").value;
    const lastName = document.getElementById("last-name").value;
    const school = document.getElementById("school").value;
    createURL="first-name=" + firstName + 
                "&last-name=" + lastName + 
                "&school=" + school +
                "&action=createProfile";
    if(userLoginInfo.isLoggedIn) {
        fetch('/profile',
            {  
                method: "POST",
                body: createURL,
                headers: { 'Content-type': 'application/x-www-form-urlencoded' }
            })
                .then(response => response.json()).then((myData) => {
            const profileTable = document.getElementById('profile=data');
            console.log(response);
            window.location.href = "profile.html";
        });
    }
    else {
        console.log("not logged in")
    }
}
