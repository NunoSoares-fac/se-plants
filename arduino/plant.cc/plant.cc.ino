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
#define DigitalLed1 8
#define DigitalLed2 3
#define DigitalLed3 5

DHT dht = DHT(DHTdataPin, DHTType);

// UDP https://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/udp-class.html
WiFiUDP UDP;
unsigned int localUdpPort = 4210;

#define packetBufferSize 35
char packetBuffer[packetBufferSize];

#define replyBufferSize 75
char replyBuffer[replyBufferSize];

IPAddress remoteIP(192,168,99,173);
#define remotePort 41234

void setup() {
  // Setup serial port
  Serial.begin(115200);
  Serial.println();

  pinMode(DigitalLed1, OUTPUT);
  digitalWrite(DigitalLed1,LOW);

  pinMode(DigitalLed2, OUTPUT);
  digitalWrite(DigitalLed2,LOW);

  pinMode(DigitalLed3, OUTPUT);
  digitalWrite(DigitalLed3,LOW);
  
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
  unsigned long time = millis();
  
  int packetSize = UDP.parsePacket();
  if(packetSize) {
    UDP.read(packetBuffer, packetBufferSize);

    packetBuffer[34] = '\0';
    Serial.println(packetBuffer);
    
    char led1 = packetBuffer[9];
    if(led1 == '0') {
      digitalWrite(DigitalLed1,LOW);
    } else if(led1 == '1') {
      digitalWrite(DigitalLed1,HIGH);
    }

    char led2 = packetBuffer[20];
    if(led2 == '0') {
      digitalWrite(DigitalLed2,LOW);
    } else if(led2 == '1') {
      digitalWrite(DigitalLed2,HIGH);
    }

    char led3 = packetBuffer[31];
    if(led3 == '0') {
      digitalWrite(DigitalLed3,LOW);
    } else if(led1 == '1') {
      digitalWrite(DigitalLed3,HIGH);
    }
    
  }
  
  lightness = analogRead(LDRAnalogicIn);
  
  dtostrf(dht.readHumidity(), 5, 2, humStr);
  dtostrf(dht.readTemperature(), 5, 2, temStr);
  
  sprintf(replyBuffer,
    "\{\"temperature\":\"%s\",\"humidity\":\"%s\",\"luminosity\":\"%d\"\}",  
    temStr, 
    humStr, 
    lightness);

  UDP.beginPacket(remoteIP, remotePort);
  UDP.write(replyBuffer, strlen(replyBuffer));
  UDP.endPacket();

  Serial.println(replyBuffer);

  while(millis() - time <= 500) {
    delay(50);
  }

}
