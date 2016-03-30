#include <arduino.h>
#include <LiquidCrystal.h>
//input/output pins
#define PIN_LED_1 64
#define PIN_LED_2 65
#define PIN_LED_3 66
#define PIN_LED_4 67
#define PIN_LED_5 68
#define PIN_LED_6 69
#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3
#define PIN_SELECT_BUTTON 21

//analog pins
#define PIN_CONTRAST_ANALOG 8
#define PIN_HORZ_ANALOG 0
#define PIN_VERT_ANALOG 1

float horzRead = 0;
float vertRead = 0;

LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);


void setup() {
  // put your setup code here, to run once:
  pinMode(PIN_LED_1, OUTPUT);
  pinMode(PIN_LED_2, OUTPUT);
  pinMode(PIN_LED_3, OUTPUT);
  pinMode(PIN_LED_4, OUTPUT);
  pinMode(PIN_LED_5, OUTPUT);
  pinMode(PIN_LED_6, OUTPUT);

  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_SELECT_BUTTON, INPUT_PULLUP);

  lcd.begin(16, 2);

}

void loop() {
  // put your main code here, to run repeatedly:
  if(!digitalRead(PIN_RIGHT_BUTTON)){
    digitalWrite(PIN_LED_6, HIGH);
  }else{
    digitalWrite(PIN_LED_6, LOW);
  }
  if(!digitalRead(PIN_LEFT_BUTTON)){
    digitalWrite(PIN_LED_4, HIGH);
  }
  else{
    digitalWrite(PIN_LED_4, LOW);
  }
  if(!digitalRead(PIN_SELECT_BUTTON)){
    digitalWrite(PIN_LED_2, HIGH);
  }else{
    digitalWrite(PIN_LED_2, LOW);
  }

  horzRead = analogRead(PIN_HORZ_ANALOG);
  vertRead = analogRead(PIN_VERT_ANALOG);

  lcd.setCursor(0, 0);
  lcd.print("Horz = ");
  lcd.setCursor(8, 0);
  lcd.print(horzRead);

   lcd.setCursor(0, 1);
  lcd.print("Vert = ");
  lcd.setCursor(8, 1);
  lcd.print(vertRead);


}




