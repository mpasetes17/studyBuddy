async function getTable() {
    const profile = await fetch('/profile',
            {  
                method: "POST",
                body: "first_name=?&last_name=?&school=?&action=findProfile",
                headers: { 'Content-type': 'application/x-www-form-urlencoded' }
            })
                .then(response => response.json()).then((myData) => {
            return myData;
        });
  
    const sub = document.getElementById("subject-select").value;
    const privacy = document.getElementById("privacy-select").value;
    const searchUrl = "first-name=" + profile.firstName + "&last-name=" +
        profile.lastName + "&subject-select=" + sub + "&privacy-select=" + privacy;
    fetch('/matches',
        {  
            method: "POST",
            body: searchUrl,
            headers: { 'Content-type': 'application/x-www-form-urlencoded' }
         })
         .then(response => response.json()).then((matches) => {
        const tableContainer = document.getElementById('search-results');

        error = document.createElement("p");
        text = document.createTextNode("No matches at the moment, please try again later!");
        error.appendChild(text);
        error.setAttribute('dislpay','none');
        
        if (matches.length == 0){
            error.setAttribute('display','inline');
            tableContainer.appendChild(error);
        }else{
            error.setAttribute('display','none');
            for(i=0; i < matches.length; i++){
                const result_block = createResultBlock(matches[i], i);
                result_block.classList.add("Single_Result_Block_Style");
                tableContainer.appendChild(result_block);
            }
        }
    });
}

/**
This funtion clears the  
 */
function clearDiv(elementID){
    document.getElementById(elementID).innerHTML = ""; 
}

/**
This function creates and styles a relsult block
(Will break this into different cunctions later)

postion will be the id of every ul created by this function
 */
function createResultBlock(student_info, position){
    // Grabs school and time
    const school_domain = student_info.school.substring(student_info.school.indexOf('@'));
    const time = getTime(student_info.timestamp);

    // This is the data that will be shown for each student
    const name = (student_info.firstName == undefined || student_info.lastName == undefined) ?
        "Name: " + student_info.nickname :
        "Name: " + student_info.firstName + " " + student_info.lastName;
    const school = "School: " + school_domain.substring(0 , school_domain.indexOf('.'));
    const email = "Contact: " + student_info.email;
    const time_comment = "Last Request made at " + time.hour + ":" + time.minute;

    // This is the ul block that will be returned by the function
    var result_block = document.createElement("div");
    result_block.setAttribute('id', '' + position); 

    // This loop adds styling onto each data and appends it on the "ul" 
    data = [name, school, email, time_comment];
    for(var i = 0; i < data.length; i++){
        var container = document.createElement("p");
        const text = document.createTextNode(data[i]);
        container.appendChild(text);

        // Adds styling
        if(i == data.length - 1){                               // name and school
            container.classList.add("Time_Style");
        }else{                                                  // time
            container.classList.add("Student_Info_Style");
        }
        result_block.appendChild(container);
    }
    return result_block;
}

/** 
Returns a Json with info about time
*/
function getTime(UNIX_timestamp){
  var time = new Date(UNIX_timestamp * 1000);
  var monthStrings = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  return {
        year: time.getFullYear(),
        month: monthStrings[time.getMonth()],
        date: time.getFullYear(), 
        hour: time.getHours(), 
        minute: time.getMinutes(), 
        second: time.getSeconds()}
}

async function getUser() {
    // Fetches Authentication info
    const response = await fetch('/login');
    const userLoginInfo = await response.json();
    
    loginMenu();

    //Determines if the user is logged in or not
    if(userLoginInfo.isLoggedIn){
        const email = userLoginInfo.email;
        const username = email.substring(0, email.indexOf("@"));
        userInfo = document.getElementById("user-container")
        userInfo.innerText = username + "\n";
    }
    else{
        searchForm = document.getElementById("search-form")
        html = "<p>Click <a href=\"" + userLoginInfo.url + "\">" +
            "HERE</a> to log in before performing a search</p>";
        searchForm.innerHTML = html;
    }
}

/*
 * Prevents the page from redirecting upon submitting their subject of choice
 */
function pageSetup(){
    getUser();
    getProfile();
    document.getElementById("search-button").addEventListener("click", function(event){
        event.preventDefault();
        clearDiv('search-results');
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

/*
 * Searches for profile and prompts for new one if not found
 */
async function getProfile() {
    const response = await fetch('/login')
    const userLoginInfo = await response.json()
    if(userLoginInfo.isLoggedIn) {
        fetch('/profile',
            {  
                method: "POST",
                body: "first_name=?&last_name=?&school=?&action=findProfile",
                headers: { 'Content-type': 'application/x-www-form-urlencoded' }
            })
                .then(response => response.json()).then((myData) => {
            const profileTable = document.getElementById('profile-data');
            if(myData.result == "no profile found") {
                window.location.href = "createProfile.html";
            }
        });
    }
    else {
        window.location.href = userLoginInfo.url;
    }
}
