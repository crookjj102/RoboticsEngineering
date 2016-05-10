#include <GolfBallStand.h>
#include <Usb.h>
#include <Max3421e.h>
#include <AndroidAccessory.h>

char manufacturer[] = "Rose-Hulman";
char model[] = "GOLFBALLSTAND";
char versionStr[] = "1.0";


AndroidAccessory acc(manufacturer, model, model, versionStr,
                     "https://youtu.be/QrGrOK8oZG8",
                     "12345");

int ballColor_1, ballColor_2, ballColor_3;

char rxBuf[255];

int options[3][8] = {BALL_WHITE, BALL_YELLOW, BALL_RED,
                     BALL_WHITE, BALL_YELLOW, BALL_GREEN,
                     BALL_WHITE, BALL_BLUE, BALL_RED,
                     BALL_WHITE, BALL_BLUE, BALL_GREEN,
                     BALL_BLACK, BALL_YELLOW, BALL_RED,
                     BALL_BLACK, BALL_YELLOW, BALL_GREEN,
                     BALL_BLACK, BALL_BLUE, BALL_RED,
                     BALL_BLACK, BALL_BLUE, BALL_GREEN
                    };

bool availableOp[3][8] = {1, 1, 1,
                          1, 1, 1,
                          1, 1, 1,
                          1, 1, 1,
                          1, 1, 1,
                          1, 1, 1,
                          1, 1, 1,
                          1, 1, 1,
                         };

int totTries = 0;
bool done = false;
char message[3];
char message2[1];


GolfBallStand stand;
void setup() {
  // put your setup code here, to run once:
  delay(1500);
  acc.powerOn();
  Serial.begin(9600);

}

void loop() {
  //  while (digitalRead(PIN_GOLF_BALL_STAND_SWITCH)) {
  //    // Do nothing until the switch is pressed.
  //  }
  if (acc.isConnected()) {
    int len = acc.read(rxBuf, sizeof(rxBuf), 1);
    if (len > 0) {
      rxBuf[len - 1] = '\0';
      String inputString = String(rxBuf);
      if (inputString.equalsIgnoreCase("TEST")) {
        runColors();
      }
    }
    if(done){
      done = false;
      message2[0] = 'L';
      acc.write(message, sizeof(message));
    }
  }
  if (!digitalRead(PIN_GOLF_BALL_STAND_SWITCH)) {
    runColors();
  }

}

void printBallColor(int ballColor) {
  switch (ballColor) {
    case BALL_NONE:
      Serial.println("No ball");
      break;
    case BALL_BLACK:
      Serial.println("Black ball");
      break;
    case BALL_BLUE:
      Serial.println("Blue ball");
      break;
    case BALL_GREEN:
      Serial.println("Green ball");
      break;
    case BALL_RED:
      Serial.println("Red ball");
      break;
    case BALL_YELLOW:
      Serial.println("Yellow ball");
      break;
    case BALL_WHITE:
      Serial.println("White ball");
      break;
  }
}

void runColors() {
  Serial.print("---------------------------------");
  ballColor_1 = stand.determineBallColor(LOCATION_1);
  Serial.print("  Location 1 ball   = ");
  printBallColor(ballColor_1);
  delay(100);
  ballColor_2 = stand.determineBallColor(LOCATION_2);
  Serial.print("  Location 2 ball   = ");
  printBallColor(ballColor_2);
  delay(100);
  ballColor_3 = stand.determineBallColor(LOCATION_3);
  Serial.print("  Location 3 ball   = ");
  printBallColor(ballColor_3);
  delay(100);
  stand.setLedState(LED_GREEN, LOCATION_3, LED_FRONT);
  Serial.print("---------------------------------\n\n");

  int externalPhotoCellReading = stand.getAnalogReading(LOCATION_EXTERNAL);
  Serial.print("External photo cell reading = ");
  Serial.println(externalPhotoCellReading);

  adjustColorsBasedOnOptions();
}


void adjustColorsBasedOnOptions() {
  //we should have the c++ return the top # most likely options
  //if we don't have a valid combo with the top options,
  //try again with each lower permutation of likely options


  bool answerValid = false;
  bool b1There = ballColor_1 != BALL_NONE;
  bool b2There = ballColor_2 != BALL_NONE;
  bool b3There = ballColor_3 != BALL_NONE;

   if (!b1There || !b2There || !b3There) {
    if(b1There || b2There || b3There){
    }
    answerValid = true;
  }

  for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 3; j++) {
      for (int k = 0; k < 3; k++) {
        int expectedColor;
        if (k == 0) {
          expectedColor = ballColor_1;
        } else if (k == 1) {
          expectedColor = ballColor_2;
        } else {
          expectedColor = ballColor_3;
        }

        if (options[i][j] == expectedColor && availableOp[i][j]) {
          availableOp[i][j] = 0;
        }
        if (!availableOp[i][0] && !availableOp[i][1] && !availableOp[i][2]) {
          answerValid = true;
        }

      }
    }
  }

  if (!answerValid) {
    Serial.println("not really a good answer!");
    totTries++;
    if (totTries <= 0) {
      runColors();
    } else {
      answerValid = true;
      totTries = 0;
    }
  }
  if (answerValid) {
    Serial.println("Final Colors:");
    Serial.print("  Location 1 ball   = ");
    printBallColor(ballColor_1);
    if (ballColor_1 != BALL_NONE) {
      message[0] = ballColor_1 + '0';
    } else {
      message[0] = 'A';
    }
    Serial.print("  Location 2 ball   = ");
    if (ballColor_2 != BALL_NONE) {
      message[1] = ballColor_2 + '0';
    } else {
      message[1] = 'A';
    }
    printBallColor(ballColor_2);
    Serial.print("  Location 3 ball   = ");
    if (ballColor_3 != BALL_NONE) {
      message[2] = ballColor_3 + '0';
    } else {
      message[2] = 'A';
    }
    printBallColor(ballColor_3);
    done = true;
    

  }

}

