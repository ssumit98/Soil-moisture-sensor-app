#include <ESP8266HTTPClient.h>
#include <FirebaseArduino.h>
#include  <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include<SoftwareSerial.h>
#define FIREBASE_HOST "farmapp-49982.firebaseio.com"
#define WIFI_SSID "sumit" // Change the name of your WIFI
#define WIFI_PASSWORD "cyberknights" // Change the password of your WIFI

char HH = 5;
char MM = 30;
unsigned int localPort = 2390;
IPAddress timeServerIP; // time.nist.gov NTP server address
const char* ntpServerName = "time.nist.gov";
const int NTP_PACKET_SIZE = 48; // NTP time stamp is in the first 48 bytes of the message
byte packetBuffer[ NTP_PACKET_SIZE]; //buffer to hold incoming and outgoing packets
WiFiUDP udp;
void setup() {
 pinMode(A0,INPUT);
  Serial.begin(115200);
  //Serial.println();
  //Serial.println();
  WiFi.mode(WIFI_STA);

   WiFi.begin (WIFI_SSID, WIFI_PASSWORD);
   while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    //Serial.print(".");
  }
  //Serial.println ("");
  //Serial.println ("WiFi Connected!");
  Firebase.begin(FIREBASE_HOST);
  //Serial.println("");
  
  //Serial.println("WiFi connected");
  //Serial.println("IP address: ");
  //Serial.println(WiFi.localIP());

  //Serial.println("Starting UDP");
  udp.begin(localPort);
  //Serial.print("Local port: ");
  //Serial.println(udp.localPort());

}
unsigned long sendNTPpacket(IPAddress& address)
{
 // Serial.println("sending NTP packet...");
  memset(packetBuffer, 0, NTP_PACKET_SIZE);
  packetBuffer[0] = 0b11100011;   // LI, Version, Mode
  packetBuffer[1] = 0;     // Stratum, or type of clock
  packetBuffer[2] = 6;     // Polling Interval
  packetBuffer[3] = 0xEC;  // Peer Clock Precision
  // 8 bytes of zero for Root Delay & Root Dispersion
  packetBuffer[12]  = 49;
  packetBuffer[13]  = 0x4E;
  packetBuffer[14]  = 49;
  packetBuffer[15]  = 52;
  udp.beginPacket(address, 123); //NTP requests are to port 123
  udp.write(packetBuffer, NTP_PACKET_SIZE);
  udp.endPacket();
}

void loop() {
  String date1,date2,date3;
  float h = analogRead(A0);
  h=map(h,0,1024,0,100);
  h=100-h;
  StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.createObject();
        //JsonObject& time1 = jsonBuffer.createObject();
        //root["moisture"] = h;
        
          char hours, minutes, seconds;
  WiFi.hostByName(ntpServerName, timeServerIP); 
  sendNTPpacket(timeServerIP); // send an NTP packet to a time server
delay(1000);
  
  int cb = udp.parsePacket();
  if (!cb) {
    //Serial.println("no packet yet");
  }
  else {
    //Serial.print("packet received, length=");
    //Serial.println(cb);
    udp.read(packetBuffer, NTP_PACKET_SIZE); // read the packet into the buffer
unsigned long highWord = word(packetBuffer[40], packetBuffer[41]);
    unsigned long lowWord = word(packetBuffer[42], packetBuffer[43]);
    unsigned long secsSince1900 = highWord << 16 | lowWord;
    const unsigned long seventyYears = 2208988800UL;
    unsigned long epoch = secsSince1900 - seventyYears;
  //  Serial.println(epoch);
  minutes = ((epoch % 3600) / 60);
    minutes = minutes + MM; //Add UTC Time Zone
    hours = (epoch  % 86400L) / 3600;    
    if(minutes > 59)
    {      
      hours = hours + HH + 1; //Add UTC Time Zone  
      minutes = minutes - 60;
    }
    else
    {
      hours = hours + HH;
    }
    date1=String(hours,DEC);
    date2=String(minutes,DEC);
    seconds = (epoch % 60);
    date3=String(seconds,DEC);
  String time11=date1+":"+date2+":"+date3+" ,"+h;
  root["upload_time"] = time11;
  String name1 = Firebase.push("/Sensor_db/Sensor3_data/", root);
//  String n = Firebase.push("/Sensor_db/Sensor3_data",time1);
  delay(2000);
  }
}
 
