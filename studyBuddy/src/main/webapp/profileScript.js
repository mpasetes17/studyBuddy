
/*
 * Searches for profile and prompts for new one if not found
 */
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

/*
 * Setup login/logout button in navigation menu
 */
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
 * Setup the create profile page
 */
function setupCreate(){
    loginMenu()
    document.getElementById("create-button").addEventListener("click", function(event){
        event.preventDefault();
        createProfile();
    });
}

/*
 * Creates POST request for creating a profile
 */
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