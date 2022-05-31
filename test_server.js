var dgram = require('dgram');
var fs = require("fs")

const server = dgram.createSocket('udp4');

/**
 * 
 Function responsible for updating led_state.json
 */
function update_led_state() {
  let data = fs.readFileSync('led_state.json');
  let info = fs.readFileSync('info.json');
  let thresholds = fs.readFileSync('threshold.json');
  let led = JSON.parse(data);
  let info_state = JSON.parse(info);
  let threshold_data = JSON.parse(thresholds);
  
  if (info_state.plant1.humidity >= threshold_data.plant1.humidity_upper || info_state.plant1.humidity < threshold_data.plant1.humidity_lower) led.led1 = "1";
  else led.led1 = "0";

  if (info_state.plant1.luminosity >= threshold_data.plant1.luminosity_upper || info_state.plant1.luminosity < threshold_data.plant1.luminosity_lower) led.led1 = "1";
  else led.led1 = "0";

  if (info_state.plant1.temperature >= threshold_data.plant1.temperature_upper || info_state.plant1.temperature < threshold_data.plant1.temperature_lower) led.led1 = "1";
  else led.led1 = "0";

  fs.writeFileSync('led_state.json', JSON.stringify(led));
}

server.on('error', (err) => {
  console.log(`server error:\n${err.stack}`);
  server.close();
});

server.on('message', (msg, rinfo) => {
  console.log(`server got: ${msg} from ${rinfo.address}:${rinfo.port}`);

  //Code for debugging purposes
  //var obj = JSON.parse(`${msg}`);
  //console.log(obj.plant1.temperature);

  //Rewrite info.json with current values
  fs.readFile('info.json', 'utf8', function () {
      fs.writeFile('info.json', `${msg}`, function(err, result) {
        console.log("Success! JSON file updated!");
      });
  });
  update_led_state()

  //Get led state from the json file
  let data = fs.readFileSync('led_state.json');
  let led = JSON.parse(data);

  //Send a response to the arduino with the current led state
  let toSend = ("{led1:" + (led.led1).toString() + ", led2:" + (led.led2).toString() + ", led3:" + (led.led3).toString() + "}");
  server.send(toSend, rinfo.port, rinfo.address,function(error){
    if(error){
      client.close();
    }
  });
});

server.on('listening', () => {
  const address = server.address();
  console.log(`server listening ${address.address}:${address.port}`);
});

server.bind(41234);