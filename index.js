require("dotenv").config();
const twit = require('./twit');

//Function to post tweet
function tweeet(aux) {
  twit.post('statuses/update', { status: 'Test number '+ aux}, function (err, data, response) {
    console.log(data)
  });

}

//Test code to check if I can send tweets periodically AND IT WORKS!!!
var aux = Math.floor(Math.random() * 50);
tweeet(aux);
setInterval(function () {
  aux = Math.floor(Math.random() * 50);
  twit.post('statuses/update', { status: 'The random Number is '+ aux}, function (err, data, response) {
      console.log(data)
    });
}, 5000);