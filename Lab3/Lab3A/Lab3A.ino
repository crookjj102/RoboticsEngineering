#include "Arduino.h"

#define PIN_LED_1 64
#define PIN_LED_2 65
#define PIN_LED_3 66
#define PIN_LED_4 67
#define PIN_LED_5 68
#define PIN_LED_6 69


void setup() {                
	pinMode(PIN_LED_1, OUTPUT); 
	pinMode(PIN_LED_2, OUTPUT);
	pinMode(PIN_LED_3, OUTPUT);
	pinMode(PIN_LED_4, OUTPUT);
	pinMode(PIN_LED_5, OUTPUT);
	pinMode(PIN_LED_6, OUTPUT);

}
void loop() {
	lightLED(PIN_LED_1);
	lightLED(PIN_LED_2);
	lightLED(PIN_LED_3);
	lightLED(PIN_LED_4);
	lightLED(PIN_LED_5);
	lightLED(PIN_LED_6);
	lightLED(PIN_LED_5);
	lightLED(PIN_LED_4);
	lightLED(PIN_LED_3);
	lightLED(PIN_LED_2);
	}
/*
Lights an LED for 60 ms.
*/
void lightLED(int pinLED){
	digitalWrite(pinLED, HIGH);
	delay(90);
	digitalWrite(pinLED, LOW);
	
}
