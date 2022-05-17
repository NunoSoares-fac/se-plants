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
   console.log("Example app listening at http://%s:%s", host, port)
})