#include <arduino.h>
#include <LiquidCrystal.h>

#define PIN_CONTRAST_ANALOG 8

int analogReading = 0;

LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);
int age = 22;
volatile int mainEventFlags = 0;

void setup() {
  // put your setup code here, to run once:
  //pinMode(PIN_CONTRAST_ANALOG, OUTPUT);

  //lcd stuff
  lcd.begin(16, 2);


}

void loop() {
  // put your main code here, to run repeatedly:
  analogReading = analogRead(PIN_CONTRAST_ANALOG);
  lcd.setCursor(0, 0);
  lcd.print("Reading = ");
  lcd.print(analogReading);
  lcd.print("  ");
  lcd.setCursor(0, 1);
  lcd.print("Voltage = ");
  lcd.print((float) analogReading/1023.0 * 5.0);
  lcd.print("  ");

}

