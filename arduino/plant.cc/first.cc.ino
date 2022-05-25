
/* 
 *  Liga o led se a luminosidade for menos que 900 
*/

#include <DHT.h>
#define dataPin 2
#define DHTType DHT22

DHT dht = DHT(dataPin, DHTType);

int light;
int led=13;


void setup() {
  // put your setup code here, to run once:

  Serial.begin(9600);
  pinMode(led,OUTPUT);
  dht.begin();
  
}

void loop() {
  // put your main code here, to run repeatedly:
  delay(3000);
  light=analogRead(A0);
  

  if(light<500){
    digitalWrite(led,HIGH);
    }
  else{
    digitalWrite(led,LOW);
    }

    float h= dht.readHumidity();
    float t= dht.readTemperature();
    
    Serial.print("light ");
    Serial.println(light);
    Serial.print("humidity ");
    Serial.println(h);
    Serial.print("temperature ");
    Serial.println(t);
}
