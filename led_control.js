var fs = require("fs")

/**
 * 
 Function responsible for updating led_state.json
 */
exports.update_led_state = function () {
  let data = fs.readFileSync('led_state.json');
  let info = fs.readFileSync('info.json');
  let thresholds = fs.readFileSync('threshold.json');
  let forced = fs.readFileSync('led_forced.json');
  
  let led = JSON.parse(data);
  let info_state = JSON.parse(info);
  let threshold_data = JSON.parse(thresholds);
  let forced_state = JSON.parse(forced);

  //Planta 1
  if (forced_state.plant1.led1 === "1" || parseInt(info_state.plant1.humidity) >= parseInt(threshold_data.plant1.humidity_upper) ||
    parseInt(info_state.plant1.humidity) < parseInt(threshold_data.plant1.humidity_lower)) led.plant1.led1 = "1";
  else led.plant1.led1 = "0";

  if (forced_state.plant1.led2 === "1" || parseInt(info_state.plant1.luminosity) >= parseInt(threshold_data.plant1.luminosity_upper) ||
    parseInt(info_state.plant1.luminosity) < parseInt(threshold_data.plant1.luminosity_lower)) led.plant1.led2 = "1";
  else led.plant1.led2 = "0";

  if (forced_state.plant1.led3 === "1" || parseInt(info_state.plant1.temperature) >= parseInt(threshold_data.plant1.temperature_upper) ||
    parseInt(info_state.plant1.temperature) < parseInt(threshold_data.plant1.temperature_lower)) led.plant1.led3 = "1";
  else led.plant1.led3 = "0";

  //Planta 2
  if (forced_state.plant2.led1 === "1" || parseInt(info_state.plant2.humidity) >= parseInt(threshold_data.plant2.humidity_upper) ||
    parseInt(info_state.plant2.humidity) < parseInt(threshold_data.plant2.humidity_lower)) led.plant2.led1 = "1";
  else led.plant2.led1 = "0";

  if (forced_state.plant2.led2 === "1" || parseInt(info_state.plant2.luminosity) >= parseInt(threshold_data.plant2.luminosity_upper) ||
    parseInt(info_state.plant2.luminosity) < parseInt(threshold_data.plant2.luminosity_lower)) led.plant2.led2 = "1";
  else led.plant2.led2 = "0";

  if (forced_state.plant2.led3 === "1" || parseInt(info_state.plant2.temperature) >= parseInt(threshold_data.plant2.temperature_upper) ||
    parseInt(info_state.plant2.temperature) < parseInt(threshold_data.plant2.temperature_lower)) led.plant2.led3 = "1";
  else led.plant2.led3 = "0";

  fs.writeFileSync('led_state.json', JSON.stringify(led));
}