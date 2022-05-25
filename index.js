require("dotenv").config();
const twit = require('./twit');
var fs = require("fs");

const threshold = {
  humidity:"50",
  luminosity:"0",
  temperature:"25"
};

//Test code to check if I can send tweets periodically AND IT WORKS!!!
var aux = Math.floor(Math.random() * 50);
setInterval(function () {
  aux = Math.floor(Math.random() * 50);
  /*twit.post('statuses/update', { status: 'The random Number is '+ aux}, function (err, data, response) {
      console.log(data)
    });*/
  let data = fs.readFileSync('led_state.json');
  let info = fs.readFileSync('info.json');
  let led = JSON.parse(data);
  let info_state= JSON.parse(info);
  if (info_state.plant1.humidity >= threshold.humidity) led.led1 = "1";
  else led.led1 = "0";
  fs.writeFileSync('led_state.json', JSON.stringify(led));

}, 2000);