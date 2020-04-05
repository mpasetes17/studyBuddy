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

function pageSetup(){
    document.getElementById("search-button").addEventListener("click", function(event){
        event.preventDefault();
        getTable();
    });
}

/*
*   many thoughts many prayers

function getTable2(){
    fetch('/matches', {method: "POST"}).then(response => response.json())
        .then(function (data){
            const tCont = document.getElementById('search-results');
             tCont.innterText=data;
        })
}
*/