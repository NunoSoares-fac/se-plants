var express = require('express');
var app = express();
var fs = require("fs");

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

   fs.writeFileSync('threshold.json', JSON.stringify(threshold_data));

   //Code for debugging purposes
   //data = fs.readFileSync('threshold.json');
   //console.log(JSON.parse(data));

   res.send({ 200: 'Success' });
});

app.patch('/update', function (req, res) {
   console.log(req.body)
   let content = req.body;
   try {
      fs.readFile('info.json', 'utf8', function () {
         fs.writeFile('info.json', JSON.stringify(content), function(err, result) {
            if (err) console.log('error', err);
            else res.send({ 200: 'Success' });
         });
      });
   } catch (err) {
      console.log(err);
   }
});

var server = app.listen(8081, function () {
   var host = server.address().address
   var port = server.address().port
   console.log("Listening at http://%s:%s", host, port)
})