#include <Arduino.h>
#include <LiquidCrystal.h>

// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);
//0 -180. only need unsigned chars. initialize to 90.
unsigned char servoValues[2][3] = {{90,90,90},{90,90,90}};

void setup() 
{
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);

}

void loop()
{


}

/*
writes the servoValues to the LCD.

*/
void setLCDValues()
{
	for( int i = 0; i<2; i++){
		for (int j = 0; j<3; j++){
			lcd.setCursor(5*j, i);
			lcd.print(servoValues[i][j]);
		}
	}

}