
#include <SPI.h>
#include <WiFi.h>
#include <WiFiUdp.h>

#define WIFI_SSID "OMEN-HP"   //  your network SSID (name)
#define WIFI_PASS "uVe2f6Gr"  // your network password

// UDP
WiFiUDP UDP;
char packet[255];
char reply[] = "Packet received!";

IPAddress remoteIP(10,42,0,1);
#define remotePort 22222

void setup() {
  // Setup serial port
  Serial.begin(115200);
  Serial.println();
  
  // Begin WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  
  // Connecting to WiFi...
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  // Loop continuously while WiFi is not connected
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(100);
    Serial.print(".");
  }
  
  // Connected to WiFi
  Serial.println();
  Serial.print("Connected! IP address: ");
  Serial.println(WiFi.localIP());
}
 
void loop() {
  strcpy(packet, "asdasdas\n");
  UDP.beginPacket(remoteIP, remotePort);
  UDP.write(packet, strlen(packet));
  UDP.endPacket();

  delay(1000);

  /*int packetSize = UDP.parsePacket();
  if (packetSize) {
    Serial.print("Received packet! Size: ");
    Serial.println(packetSize); 
    int len = UDP.read(packet, 255);
    if (len > 0)
    {
      packet[len] = '\0';
    }
    Serial.print("Packet received: ");
    Serial.println(packet);
  }*/
 
}
