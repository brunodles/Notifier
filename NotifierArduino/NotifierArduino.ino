#include <SoftwareSerial.h> 
#include <MeetAndroid.h>

MeetAndroid meetAndroid(3,2,9600); // rx tx

#define onboardLed 13

#define redLed 11
#define greenLed 10
#define blueLed 9

#define mainLoopDelay 5
#define bluetoothDelay 1000

#define maxColorMod 500

int redA;
int greenA;
int blueA;

int nextRed;
int nextGreen;
int nextBlue;

int count;

int colorMod;
boolean showColors;

void setup()  
{
//  Serial.begin(9600); 
  
  pinMode(redLed, OUTPUT);
  pinMode(greenLed, OUTPUT);
  pinMode(blueLed, OUTPUT);
  
  meetAndroid.registerFunction(red, 'R'); 
  meetAndroid.registerFunction(green, 'G'); 
  meetAndroid.registerFunction(blue, 'B'); 
  meetAndroid.registerFunction(timeTick, 'T'); 
  
  digitalWrite(onboardLed, LOW);
  
  check(redLed);
  check(greenLed);
  check(blueLed);
}

void check(int color){
  analogWrite(color, 150);
  delay(500);
  analogWrite(color, 0);
}

void loop()
{
  checkBluetooth();
  changeColors();
  delay(mainLoopDelay);
}

void checkBluetooth(){
  if (count == 0) 
    meetAndroid.receive();
  count = count + mainLoopDelay;
  if (count > bluetoothDelay)
    count = 0;
}

void changeColors(){
  colorMod = colorMod + 1;
  if (colorMod > maxColorMod){
    showColors = !showColors;
    colorMod = 0;
  }
  
  if (showColors) {
    redA    = updateColor(nextRed,   redA,   redLed);
    greenA  = updateColor(nextGreen, greenA, greenLed);
    blueA   = updateColor(nextBlue,  blueA,  blueLed);
  } else {
    redA    = updateColor(0,   redA,   redLed);
    greenA  = updateColor(0, greenA, greenLed);
    blueA   = updateColor(0,  blueA,  blueLed);
  }
}

int updateColor(int nextColor, int actualColor, int ledPin){
  if (nextColor > actualColor)
    actualColor = actualColor + 1;
  else if (nextColor < actualColor)
    actualColor = actualColor - 1;
  analogWrite(ledPin, actualColor);
  return actualColor;
}

void red(byte flag, byte numOfValues)
{
  int intensity = meetAndroid.getInt();
  nextRed = intensity;
//  analogWrite(redLed, intensity);
//  Serial.write("red = ");
//  Serial.write(intensity);
}

void green(byte flag, byte numOfValues)
{
  int intensity = meetAndroid.getInt();
  nextGreen = intensity;
//  analogWrite(greenLed, intensity);
//  Serial.write("green = ");
//  Serial.write(intensity);
}

void blue(byte flag, byte numOfValues)
{
  int intensity = meetAndroid.getInt();
  nextBlue = intensity;
//  analogWrite(blueLed, intensity);
//  Serial.write("blue = ");
//  Serial.write(intensity);
}

void timeTick(byte flag, byte numOfValues)
{
  int minutes = meetAndroid.getInt();
  
  if (minutes == 0) 
  {
    digitalWrite(onboardLed, LOW);
    delay(1000);
    digitalWrite(onboardLed, HIGH);
  }
  else 
  {
    for (int i=0; i<minutes; i++)
    {
      digitalWrite(onboardLed, LOW);
      delay(75);
      digitalWrite(onboardLed, HIGH);
      delay(75);
    }
  }
}

