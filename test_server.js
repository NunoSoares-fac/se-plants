var dgram = require('dgram');
var fs = require("fs");

const server = dgram.createSocket('udp4');
var clientP = 0;
server.on('error', (err) => {
  console.log(`server error:\n${err.stack}`);
  server.close();
});

server.on('message', (msg, rinfo) => {
    console.log(`server got: ${msg} from ${rinfo.address}:${rinfo.port}`);
    var obj = JSON.parse(`${msg}`);
    //console.log(obj.plant1.temperature);
    fs.readFile('info.json', 'utf8', function () {
        fs.writeFile('info.json', `${msg}`, function(err, result) {
            console.log("Success! JSON file updated!")
        });
    });
  var x= Math.round(Math.random())
  server.send(msg, rinfo.port, 'localhost',function(error){
  if(error){
    client.close();
  }else{
    console.log(x);
  }

});
});



server.on('listening', () => {
  const address = server.address();
  console.log(`server listening ${address.address}:${address.port}`);
});

server.bind(41234);