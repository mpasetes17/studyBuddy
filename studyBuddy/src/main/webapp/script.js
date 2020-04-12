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

/*
 * Prevents the page from redirecting upon submitting their subject of choice
 */
function pageSetup(){
    document.getElementById("search-button").addEventListener("click", function(event){
        event.preventDefault();
        getTable();
    });
}