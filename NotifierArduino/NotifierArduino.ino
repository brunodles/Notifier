#include <SoftwareSerial.h>
#include <stdlib.h>

#define bluetooth_rx 3
#define bluetooth_tx 2

#define onboardLed 13

#define redLed 11
#define greenLed 10
#define blueLed 9

#define mainLoopDelay 5

int redA;
int greenA;
int blueA;

int nextRed;
int nextGreen;
int nextBlue;

boolean shouldBlink;

SoftwareSerial btSerial(bluetooth_rx, bluetooth_tx);

void setup() {
    Serial.begin(9600); 
    btSerial.begin(9600);

    pinMode(redLed, OUTPUT);
    pinMode(greenLed, OUTPUT);
    pinMode(blueLed, OUTPUT);

    for (int i = 0; i < 10; i++){
      check(redLed);
      check(greenLed);
      check(blueLed);
      check(onboardLed);
    }
}

void check(int color) {
    digitalWrite(color, HIGH);
    delay(20);
    digitalWrite(color, LOW);
}

void loop() {
  if (Serial.available() > 0)
    checkSerial();
  if (btSerial.available() > 0)
    checkBluetooth();
  delay(mainLoopDelay);
  if (shouldBlink){
    analogWrite(redLed, random(0,255));
    analogWrite(greenLed, random(0,255));
    analogWrite(blueLed, random(0,255));
    delay(20);
  } else {
    changeColors();
  }
}

void checkSerial(){
  int incomingByte = Serial.read();
  String value = Serial.readString();
  checkData(incomingByte, value);
}

void checkBluetooth(){
  int incomingByte = btSerial.read();
  String value = btSerial.readString();
  checkData(incomingByte, value);
}

void checkData(int incomingByte, String value) {
    switch (incomingByte){
      case 'r':
        print("Red", value);
        nextRed = value.toInt();
      break;
      case 'g':
        print("Green", value);
        nextGreen = value.toInt();
      break;
      case 'b':
        print("Blue", value);
        nextBlue = value.toInt();
      break;
      case '.':
        colorCommand(value);
        break;
      case '!':
        Serial.print("Send AT command '");
        Serial.print(value);
        Serial.println("'");
        btSerial.print(value);
        Serial.print("->");
        Serial.println(btSerial.readString());
      break;
      case '#':
        Serial.println("Hex String ");
        Serial.print("r ");
        nextRed = strtoul(value.substring(0,2).c_str(), 0, 16);
        Serial.println(nextRed);
        Serial.print("g ");
        nextGreen = strtoul(value.substring(2,4).c_str(), 0, 16);
        Serial.println(nextGreen);
        Serial.print("b ");
        nextBlue = strtoul(value.substring(4,6).c_str(), 0, 16);
        Serial.println(nextBlue);
      break;
      default:
        Serial.print("I received: (");
        Serial.print(incomingByte);
        Serial.print(") ");
        Serial.print((char)incomingByte);
        Serial.print(" ");
        Serial.println(value);
  }
}

void colorCommand(String command){
  switch (command.charAt(0)){
    case 'b':
      redA = 0;
      blueA = 0;
      greenA = 0;
      digitalWrite(redLed, LOW);
      digitalWrite(greenLed, LOW);
      digitalWrite(blueLed, LOW);
      delay(100);
      shouldBlink = !shouldBlink;
    break;
  }
}

void print(String color, String value) {
    Serial.print("I received: ");
    Serial.print(color);
    Serial.print(' ');
    Serial.println(value);
}

void changeColors() {
    redA = updateColor(nextRed, redA, redLed);
    greenA = updateColor(nextGreen, greenA, greenLed);
    blueA = updateColor(nextBlue, blueA, blueLed);
}

int updateColor(int nextColor, int actualColor, int ledPin) {
    if (nextColor > actualColor)
        actualColor = actualColor + 1;
    else if (nextColor < actualColor)
        actualColor = actualColor - 1;
    analogWrite(ledPin, actualColor);
    return actualColor;
}
