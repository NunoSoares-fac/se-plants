#include <SPI.h>
#include <WiFi.h>
#include <WiFiUdp.h>
#include <DHT.h>

// wifi use 4,7,10,11,12,13,9 have a led on shild

#define WIFI_SSID "POCO X3 NFC"   //  your network SSID (name)
#define WIFI_PASS "aaaaaaaa"  // your network password

#define DHTType DHT22
#define DHTdataPin 2

#define LDRAnalogicIn A0
#define LedDigitalPWD 8

DHT dht = DHT(DHTdataPin, DHTType);

// UDP https://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/udp-class.html
WiFiUDP UDP;
unsigned int localUdpPort = 4210;

#define packetBufferSize 16
char packetBuffer[packetBufferSize];

#define replyBufferSize 255
char replyBuffer[replyBufferSize];

IPAddress remoteIP(192,168,138,118);
#define remotePort 41234

void setup() {
  // Setup serial port
  Serial.begin(115200);
  Serial.println();

  pinMode(LedDigitalPWD, OUTPUT);
  digitalWrite(LedDigitalPWD,LOW);
  dht.begin();

  // Begin WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  
  // Connecting to WiFi...
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  // Loop continuously while WiFi is not connected
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }
  
  // Connected to WiFi
  Serial.print("\nConnected! IP address: ");
  Serial.println(WiFi.localIP());

  UDP.begin(localUdpPort);
}

int lightness;
char humStr[8];
char temStr[8];

void loop() {
  int packetSize = UDP.parsePacket();
  if(packetSize) {
    UDP.read(packetBuffer, packetBufferSize);
    
    char ledAction = packetBuffer[8];
    if(ledAction == '0') {
      digitalWrite(LedDigitalPWD,LOW);
    } else if(ledAction == '1') {
      digitalWrite(LedDigitalPWD,HIGH);
    }

    Serial.print("ledAction: ");
    Serial.println(ledAction);
  }
  
  lightness = analogRead(LDRAnalogicIn);
  
  dtostrf(dht.readHumidity(), 5, 2, humStr);
  dtostrf(dht.readTemperature(), 5, 2, temStr);
  
  sprintf(replyBuffer,
    "{\"plant1\":\{\"temperature\":\"%s\",\"humidity\":\"%s\",\"luminosity\":\"%d\"}}", 
    temStr, 
    humStr, 
    lightness);

  UDP.beginPacket(remoteIP, remotePort);
  UDP.write(replyBuffer, strlen(replyBuffer));
  UDP.endPacket();

  Serial.println(replyBuffer);
  
  delay(500);

}
