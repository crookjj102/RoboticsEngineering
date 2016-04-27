#include <LiquidCrystal.h>

/***  Pin I/O   ***/
#define PIN_LED_1 64
#define PIN_LED_2 65
#define PIN_LED_3 66
#define PIN_LED_4 67
#define PIN_LED_5 68
#define PIN_LED_6 69
#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3
#define PIN_SELECT_BUTTON 21
#define PIN_CONTRAST_ANALOG 8
#define PIN_HORZ_ANALOG 0
#define PIN_VERT_ANALOG 1

/*** Interrupt flags ***/
volatile int mainEventFlags = 0;
#define FLAG_INTERRUPT_0 0x01
#define FLAG_INTERRUPT_1 0x02
#define FLAG_INTERRUPT_2 0x04

/***  LCD ***/
LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);
#define LINE_1 0
#define LINE_2 1

int counter = 0;

void setup()
{
  pinMode(PIN_LED_1, OUTPUT);
  pinMode(PIN_LED_2, OUTPUT);
  pinMode(PIN_LED_3, OUTPUT);
  pinMode(PIN_LED_4, OUTPUT);
  pinMode(PIN_LED_5, OUTPUT);
  pinMode(PIN_LED_6, OUTPUT);
  digitalWrite(PIN_LED_1, LOW);
  digitalWrite(PIN_LED_2, LOW);
  digitalWrite(PIN_LED_3, LOW);
  digitalWrite(PIN_LED_4, LOW);
  digitalWrite(PIN_LED_5, LOW);
  digitalWrite(PIN_LED_6, LOW);
  pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_SELECT_BUTTON, INPUT_PULLUP);
  attachInterrupt(0, int0_isr, FALLING);
  attachInterrupt(1, int1_isr, FALLING);
  attachInterrupt(2, int2_isr, FALLING);

  lcd.begin(16, 2);
  lcd.clear();
  lcd.setCursor(0, LINE_1);
  lcd.print("0123456789012345");

  Serial.begin(57600);

}

void loop()
{
  int horzReading = analogRead(PIN_HORZ_ANALOG);
  int vertReading = analogRead(PIN_VERT_ANALOG);
  int vert = vertReading / 64;
  if (mainEventFlags & FLAG_INTERRUPT_0)
  {
    delay(20);
    mainEventFlags &= ~FLAG_INTERRUPT_0;
    if (!digitalRead(PIN_RIGHT_BUTTON))
    {
      counter++;
      Serial.print("Counter = ");
      Serial.println(counter);
      lcd.setCursor(vert, LINE_1);
      lcd.print(" ");
    }
  }
  if (mainEventFlags & FLAG_INTERRUPT_1)
  {
    delay(20);
    mainEventFlags &= ~FLAG_INTERRUPT_1;
    if (!digitalRead(PIN_LEFT_BUTTON))
    {
      // do stuff
    }
  }
  if (mainEventFlags & FLAG_INTERRUPT_2)
  {
    delay(20);
    mainEventFlags &= ~FLAG_INTERRUPT_2;
    if (!digitalRead(PIN_SELECT_BUTTON))
    {
    }
  }
  // do a bunch of stuff
  lcd.setCursor(0, LINE_2);
  if (vert >= 10) lcd.print(vert);
  else
  {
    lcd.print(" ");
    lcd.print(vert);
  }
}


// Simple ISRs that set flags only
void int0_isr()
{
  mainEventFlags |= FLAG_INTERRUPT_0;
}

void int1_isr()
{
  mainEventFlags |= FLAG_INTERRUPT_1;
}

void int2_isr()
{
  mainEventFlags |= FLAG_INTERRUPT_2;
}

