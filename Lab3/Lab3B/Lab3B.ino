#include <Arduino.h>

#define RIGHT_BUTTON 2
#define LEFT_BUTTON 3

#define PIN_LED_1 64
#define PIN_LED_2 65
#define PIN_LED_3 66
#define PIN_LED_4 67
#define PIN_LED_5 68
#define PIN_LED_6 69


void setup()
{
	pinMode(LEFT_BUTTON, INPUT_PULLUP);
	pinMode(RIGHT_BUTTON, INPUT_PULLUP);

	pinMode(PIN_LED_1, OUTPUT); 
	pinMode(PIN_LED_2, OUTPUT);
	pinMode(PIN_LED_3, OUTPUT);
	pinMode(PIN_LED_4, OUTPUT);
	pinMode(PIN_LED_5, OUTPUT);
	pinMode(PIN_LED_6, OUTPUT);
}
void loop()
{
	if(!digitalRead(LEFT_BUTTON))
	{
		digitalWrite(PIN_LED_1, HIGH);
		digitalWrite(PIN_LED_2, HIGH);
		digitalWrite(PIN_LED_3, HIGH);
	}
	else
	{
		digitalWrite(PIN_LED_1, LOW);
		digitalWrite(PIN_LED_2, LOW);
		digitalWrite(PIN_LED_3, LOW);
	}

	if(!digitalRead(RIGHT_BUTTON))
	{
		digitalWrite(PIN_LED_4, HIGH);
		digitalWrite(PIN_LED_5, HIGH);
		digitalWrite(PIN_LED_6, HIGH);
	}
	else
	{
		digitalWrite(PIN_LED_4, LOW);
		digitalWrite(PIN_LED_5, LOW);
		digitalWrite(PIN_LED_6, LOW);
	}
}