require("dotenv").config();
const twit = require('./twit');
var fs = require("fs");


//Test code to check if I can send tweets periodically AND IT WORKS!!!
setInterval(function () {
  let current_state;
  let data = fs.readFileSync('led_state.json');
  let info = fs.readFileSync('info.json');
  
  let led = JSON.parse(data);
  let info_state = JSON.parse(info);

  current_state = "The status of plant 1:" +
      "\nHumidity: " + info_state.plant1.humidity + 
      "\nLuminosity: " + info_state.plant1.luminosity +
    "\nTemperature: " + info_state.plant1.temperature +
    "\n \n The status of plant 2:" +
      "\nHumidity: " + info_state.plant2.humidity + 
      "\nLuminosity: " + info_state.plant2.luminosity +
      "\nTemperature: " + info_state.plant2.temperature +
  
  twit.post('statuses/update', { status: current_state}, function (err, data, response) {
      console.log(data)
  });
  
}, 10000);