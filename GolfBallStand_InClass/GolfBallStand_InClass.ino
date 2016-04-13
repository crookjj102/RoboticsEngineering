#include <GolfBallStand.h>

GolfBallStand stand;
void setup()
{
	Serial.begin(9600);
	stand.setLedState(LED_RED, LOCATION_3, LED_FRONT);
}

void loop()
{

}