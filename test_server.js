var dgram = require('dgram');
var fs = require("fs")

const server = dgram.createSocket('udp4');
var ips = [];
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
  console.log(led);
  //Planta 1
  if (info_state.plant1.humidity >= threshold_data.plant1.humidity_upper ||
    info_state.plant1.humidity < threshold_data.plant1.humidity_lower) plant1.led.led1 = "1";
  else plant1.led.led1 = "0";

  if (info_state.plant1.luminosity >= threshold_data.plant1.luminosity_upper ||
    info_state.plant1.luminosity < threshold_data.plant1.luminosity_lower) plant1.led.led2 = "1";
  else plant1.led.led2 = "0";

  if (info_state.plant1.temperature >= threshold_data.plant1.temperature_upper ||
    info_state.plant1.temperature < threshold_data.plant1.temperature_lower) plant1.led.led3 = "1";
  else plant1.led.led3 = "0";

  //Planta 2
  if (info_state.plant2.humidity >= threshold_data.plant1.humidity_upper ||
    info_state.plant2.humidity < threshold_data.plant1.humidity_lower) plant2.led.led1 = "1";
  else plant2.led.led1 = "0";

  if (info_state.plant2.luminosity >= threshold_data.plant1.luminosity_upper ||
    info_state.plant2.luminosity < threshold_data.plant1.luminosity_lower) plant2.led.led2 = "1";
  else plant2.led.led2 = "0";

  if (info_state.plant2.temperature >= threshold_data.plant1.temperature_upper ||
    info_state.plant2.temperature < threshold_data.plant1.temperature_lower) plant2.led.led3 = "1";
  else plant2.led.led3 = "0";

  fs.writeFileSync('led_state.json', JSON.stringify(led));
}

server.on('error', (err) => {
  console.log(`server error:\n${err.stack}`);
  server.close();
});

server.on('message', (msg, rinfo) => {
  //console.log(`server got: ${msg} from ${rinfo.address}:${rinfo.port}`);

  //Code for debugging purposes
  //var obj = JSON.parse(`${msg}`);
  //console.log(obj.plant1.temperature);

  if (ips.length == 0) ips.push(rinfo.address)
  else if (!ips.includes(rinfo.address)) {
    ips.push(rinfo.address);
  }

  //Rewrite info.json with current values

  let req_info=JSON.parse(msg)

  let data_info = fs.readFileSync('info.json');
  let info_json = JSON.parse(data_info);

  if (rinfo.address == ips[0]) {
    info_json.plant1.humidity = req_info.humidity;

    info_json.plant1.luminosity = req_info.luminosity;

    info_json.plant1.temperature = req_info.temperature;
  }
  else {
    info_json.plant2.humidity = req_info.humidity;

    info_json.plant2.luminosity = req_info.luminosity;

    info_json.plant2.temperature = req_info.temperature;
  }
  
  fs.writeFileSync('info.json', JSON.stringify(info_json));

  update_led_state()

  //Get led state from the json file
  let data = fs.readFileSync('led_state.json');
  let led = JSON.parse(data);
  let toSend;

  //Send a response to the arduino with the current led state
  if (rinfo.address == ips[0]) {
    toSend = "{\"led1\":\"" + (led.plant1.led1).toString() + "\",\"led2\":\"" + (led.plant1.led2).toString() + "\",\"led3\":\"" + (led.plant1.led3).toString() + "\"}";
  }
  else {
    toSend = "{\"led1\":\"" + (led.plant2.led1).toString() + "\",\"led2\":\"" + (led.plant2.led2).toString() + "\",\"led3\":\"" + (led.plant2.led3).toString() + "\"}";
  }
  
  server.send(toSend, rinfo.port, rinfo.address, function (error) {
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