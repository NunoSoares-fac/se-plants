var express = require('express');
var app = express();
var lc = require('./led_control')
var fs = require("fs")

app.use(express.json());

app.get('/info', function (req, res) {
   fs.readFile( __dirname + "/" + "info.json", 'utf8', function (err, data) {
      console.log( data );
      res.end( data );
   });
})

app.get('/infoL', function (req, res) {
   fs.readFile( __dirname + "/" + "led_state.json", 'utf8', function (err, data) {
      console.log( data );
      res.end( data );
   });
})

app.get('/infoT', function (req, res) {
   fs.readFile( __dirname + "/" + "threshold.json", 'utf8', function (err, data) {
      console.log( data );
      res.end( data );
   });
})

app.patch('/updateT', function (req, res) {
   console.log(req.body)
   let content = req.body;
   let data = fs.readFileSync('threshold.json');
   let threshold_data = JSON.parse(data);
   
   threshold_data.plant1.humidity_upper = content.plant1.humidity_upper;
   threshold_data.plant1.humidity_lower = content.plant1.humidity_lower;

   threshold_data.plant1.luminosity_upper = content.plant1.luminosity_upper;
   threshold_data.plant1.luminosity_lower = content.plant1.luminosity_lower;

   threshold_data.plant1.temperature_upper = content.plant1.temperature_upper;
   threshold_data.plant1.temperature_lower = content.plant1.temperature_lower;

   threshold_data.plant2.humidity_upper = content.plant2.humidity_upper;
   threshold_data.plant2.humidity_lower = content.plant2.humidity_lower;

   threshold_data.plant2.luminosity_upper = content.plant2.luminosity_upper;
   threshold_data.plant2.luminosity_lower = content.plant2.luminosity_lower;

   threshold_data.plant2.temperature_upper = content.plant2.temperature_upper;
   threshold_data.plant2.temperature_lower = content.plant2.temperature_lower;

   fs.writeFileSync('threshold.json', JSON.stringify(threshold_data));

   //Code for debugging purposes
   //data = fs.readFileSync('threshold.json');
   //console.log(JSON.parse(data));
   
   lc.update_led_state();

   res.send({ 200: 'Success' });
});

app.patch('/update', function (req, res) {
   console.log(req.body)
   let content = req.body;
   let data = fs.readFileSync('led_forced.json');
   let forced_leds = JSON.parse(data);
   
   forced_leds.plant1.led1 = content.plant1.led1;
   forced_leds.plant1.led2 = content.plant1.led2;
   forced_leds.plant1.led3 = content.plant1.led3;
   
   forced_leds.plant2.led1 = content.plant2.led1;
   forced_leds.plant2.led2 = content.plant2.led2;
   forced_leds.plant2.led3 = content.plant2.led3;
   
   fs.writeFileSync('led_forced.json', JSON.stringify(forced_leds));
   
   lc.update_led_state();

   res.send({ 200: 'Success' });
});

var server = app.listen(8081, function () {
   var host = server.address().address
   var port = server.address().port
   console.log("Listening at http://%s:%s", host, port)
})