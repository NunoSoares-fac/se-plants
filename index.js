require("dotenv").config();
const twit = require('./twit');
var fs = require("fs");

//Test code to check if I can send tweets periodically AND IT WORKS!!!
setInterval(function () {
  twit.post('statuses/update', { status: 'The random Number is '+ aux}, function (err, data, response) {
      console.log(data)
  });
  
}, 10000);