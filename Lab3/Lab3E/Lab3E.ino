#include <arduino.h>
#include <LiquidCrystal.h>

#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3
#define FLAG_INTERRUPT_0 0x01
#define FLAG_INTERRUPT_1 0x02
#define FLAG_INTERRUPT_2 0x04

LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);
int age = 22;
volatile int mainEventFlags = 0;

void setup() {
  // put your setup code here, to run once:
  pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);

  attachInterrupt(0, int0_isr, FALLING);
  attachInterrupt(1, int1_isr, FALLING);

  //lcd stuff
  lcd.begin(16, 2);


}

void loop() {
  // put your main code here, to run repeatedly:
  if(mainEventFlags & FLAG_INTERRUPT_0){
    delay(20);
    mainEventFlags &= ~FLAG_INTERRUPT_0;
    if(!digitalRead(PIN_RIGHT_BUTTON)){
      age++;
    }
  }
  if(mainEventFlags & FLAG_INTERRUPT_1){
    delay(20);
    mainEventFlags &= ~FLAG_INTERRUPT_1;
    if(!digitalRead(PIN_LEFT_BUTTON)){
      age--;
    }
  }

  
  lcd.setCursor(0, 0);
  lcd.print("Zach is");
  lcd.setCursor(0, 1);
  lcd.print(age);
  lcd.setCursor(4, 1);
  lcd.print(" years old");

}

void int0_isr(){
  mainEventFlags |= FLAG_INTERRUPT_0;
}

void int1_isr(){
  mainEventFlags |= FLAG_INTERRUPT_1;
}

