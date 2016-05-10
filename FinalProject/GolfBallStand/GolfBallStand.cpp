 #include "Arduino.h"
#include "GolfBallStand.h"

#define colorThresh 750

#define slot1ROffset -150
#define slot1GOffset -100
#define slot1BOffset 150
#define slot1WOffset 0
#define slot2ROffset -25
#define slot2GOffset -100
#define slot2BOffset 125
#define slot2WOffset 0
#define slot3ROffset -50
#define slot3GOffset 100
#define slot3BOffset -50
#define slot3WOffset 0


GolfBallStand::GolfBallStand() {
    _init();  
}

void GolfBallStand::_init() {
    pinMode(PIN_LED_1_UNDER, OUTPUT);
    pinMode(PIN_LED_1_FRONT, OUTPUT);
    pinMode(PIN_LED_2_UNDER, OUTPUT);
    pinMode(PIN_LED_2_FRONT, OUTPUT);
    pinMode(PIN_LED_3_UNDER, OUTPUT);
    pinMode(PIN_LED_3_FRONT, OUTPUT);
    pinMode(PIN_RED, OUTPUT);
    pinMode(PIN_GREEN, OUTPUT);
    pinMode(PIN_BLUE, OUTPUT);
    pinMode(PIN_GOLF_BALL_STAND_SWITCH, INPUT_PULLUP);
    digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_RED, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_OFF);

	rThreshAdjust = 0;
	gThreshAdjust = 0;
	bThreshAdjust = 0;
	wThreshAdjust = 0;

	//number of times had to try to get a color (more is worse)
	slot1Confidence = 0;
	slot2Confidence = 0;
	slot3Confidence = 0;

}

void GolfBallStand::setLedState(int ledColor, int location, int underOrFront) {
    // Start by clearing off all LEDs and colors.
    digitalWrite(PIN_RED, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_OFF);

    // Decide which of the six LEDs to turn on.
    if ((location & LOCATION_1) && (underOrFront & LED_UNDER)) {
        digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_1) && (underOrFront & LED_FRONT)) {
        digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_2) && (underOrFront & LED_UNDER)) {
        digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_2) && (underOrFront & LED_FRONT)) {
        digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_3) && (underOrFront & LED_UNDER)) {
        digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_3) && (underOrFront & LED_FRONT)) {
        digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_ON);
    }

    // Set the color.
    if (ledColor & LED_BLUE) {
        digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_ON);
    }
    if (ledColor & LED_GREEN) {
        digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_ON);
    }
    if (ledColor & LED_RED) {
        digitalWrite(PIN_RED, COLOR_TRANSISTOR_ON);
    }
}

int GolfBallStand::getAnalogReading(int location) {
    int photoReading = -1;
    switch (location) {
    case LOCATION_1:
        photoReading = analogRead(PIN_PHOTO_1);
        break;
    case LOCATION_2:
        photoReading = analogRead(PIN_PHOTO_2);
        break;
    case LOCATION_3:
        photoReading = analogRead(PIN_PHOTO_3);
        break;
    case LOCATION_EXTERNAL:
        photoReading = analogRead(PIN_PHOTO_EXTERNAL);
        break;
    }
    return photoReading;
}

int GolfBallStand::determineBallColor(int location) {
    int returnBallType = BALL_NONE;
    setLedState(LED_OFF, location, LED_UNDER_AND_FRONT);
    delay(GBS_TIME_DELAY);
    int offReading = getAnalogReading(location);

    setLedState(LED_RED, location, LED_UNDER_AND_FRONT);
    delay(GBS_TIME_DELAY);
    int redReading = getAnalogReading(location);
    
    setLedState(LED_GREEN, location, LED_UNDER_AND_FRONT);
    delay(GBS_TIME_DELAY);
    int greenReading = getAnalogReading(location);

    setLedState(LED_BLUE, location, LED_UNDER_AND_FRONT);
    delay(GBS_TIME_DELAY);
    int blueReading = getAnalogReading(location);

    setLedState(LED_WHITE, location, LED_UNDER_AND_FRONT);
    delay(GBS_TIME_DELAY);
    int whiteReading = getAnalogReading(location);
    
    setLedState(LED_OFF, location, LED_UNDER_AND_FRONT);

    // TODO: Figure out the ball color.
	bool R, G, B, W, ON, Red, Black, Blue, Yellow, Green, White;
	R = false;
	G = false;
	B = false;
	W = false;
	ON = false;

	int rOffset, gOffset, bOffset, wOffset;
	if (location == LOCATION_1) {
		rOffset = slot1ROffset;
		gOffset = slot1GOffset;
		bOffset = slot1BOffset;
		slot1Confidence++;
	}else if (location == LOCATION_2) {
		rOffset = slot2ROffset;
		gOffset = slot2GOffset;
		bOffset = slot2BOffset;
		slot2Confidence++;
	}
	else if (location == LOCATION_3) {
		rOffset = slot3ROffset;
		gOffset = slot3GOffset;
		bOffset = slot3BOffset;
		slot3Confidence++;
	}
	int external = getAnalogReading(LOCATION_EXTERNAL);
	Serial.print("External: ");
	Serial.println(external);
	Serial.print("Off reading: ");
	Serial.println(offReading);
	if(offReading > external){
		ON = true;
		if (whiteReading - wOffset < colorThresh - wThreshAdjust) {
			W = true;
		}
		if (redReading - rOffset < colorThresh - rThreshAdjust) {
			R = true;
		}
		if (blueReading - bOffset < colorThresh - bThreshAdjust) {
			B = true;
		}
		if (greenReading - gOffset < colorThresh - gThreshAdjust) {
			G = true;
		}
	}
	int numOptions = 0;
	int lastOption = -1;

	//check Red
	if (R && !G && !B && W) {
		numOptions++;
		lastOption = 0;
	}
	//check Black
	if (!R && !G && !B && !W) {
		numOptions++;
		lastOption = 1;
	}
	//check Blue
	if (!R && !G && B && W) {
		numOptions++;
		lastOption = 2;
	}
	//check Yellow
	if (R && G && !B && W) {
		numOptions++;
		lastOption = 3;
	}
	//check Green
	if (!R && G && !B && W) {
		numOptions++;
		lastOption = 4;
	}

	//check White
	if (R && G && B && W) {
		numOptions++;
		lastOption = 5;
	}

    Serial.println();
    Serial.print("Readings for location ");
	if (location == LOCATION_1) {
		Serial.println("location 1");
	}
	else if (location == LOCATION_2) {
		Serial.println("location 2");
	}else{
		Serial.println("location 3");
	}
    Serial.print("  LED off reading   = ");
    Serial.println(offReading);
    Serial.print("  LED red reading   = ");
    Serial.println(redReading - rOffset);
    Serial.print("  LED green reading = ");
    Serial.println(greenReading - gOffset);
    Serial.print("  LED blue reading  = ");
    Serial.println(blueReading - bOffset);
    Serial.print("  LED white reading = ");
    Serial.println(whiteReading - wOffset);

	if (!ON) {
		return BALL_NONE;
	}
	if (numOptions <= 0) {
		int rTemp = redReading + rOffset;
		int gTemp = greenReading + gOffset;
		int bTemp = blueReading + bOffset;
		if (rTemp < gTemp && rTemp < bTemp) {//likely red
			rThreshAdjust -= 10;
			gThreshAdjust += 10;
			bThreshAdjust += 10;
			wThreshAdjust -= 2;
			//Serial.println("red maybe?");
			return BALL_RED;
		}
		else if (gTemp < rTemp && gTemp < bTemp) {//likely green
			gThreshAdjust -= 10;
			rThreshAdjust += 10;
			bThreshAdjust += 10;
			wThreshAdjust -= 2;
			return BALL_GREEN;
			//Serial.println("green maybe?");
		}else if(bTemp < rTemp && bTemp < gTemp){//likely blue
			bThreshAdjust -= 10;
			rThreshAdjust += 10;
			gThreshAdjust += 10;
			wThreshAdjust -= 2;
			return BALL_BLUE;
			//Serial.println("blue maybe?");
		}else{//black maybe?
			bThreshAdjust = 0;
			rThreshAdjust = 0;
			gThreshAdjust = 0;
			wThreshAdjust -= 10;
			return BALL_BLACK;
			//Serial.println("black maybe?");
		}

	Serial.print("not enough options");
		return determineBallColor(location);
	}else if (numOptions == 1) {
		switch (lastOption) {
		case 0:
			returnBallType = BALL_RED;
			break;
		case 1:
			returnBallType = BALL_BLACK;
			break;
		case 2:
			returnBallType = BALL_BLUE;
			break;
		case 3:
			returnBallType = BALL_YELLOW;
			break;
		case 4:
			returnBallType = BALL_GREEN;
			break;
		case 5:
			returnBallType = BALL_WHITE;
			break;
		default:
			Serial.print("ERROR");
			break;
		}
	}else{//too many options
		//please try again
		Serial.print("too many options");
		return determineBallColor(location);
	}
	rThreshAdjust = 0;
	gThreshAdjust = 0;
	bThreshAdjust = 0;
	wThreshAdjust = 0;
    return returnBallType;
}

void GolfBallStand::resetConfidence() {
	slot1Confidence = 0;
	slot2Confidence = 0;
	slot3Confidence = 0;
}




