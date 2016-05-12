#include <GolfBallStand.h>
#include <Usb.h>
#include <Max3421e.h>
#include <AndroidAccessory.h>
#include <RobotAsciiCom.h>
#include <WildThumperCom.h>

#define TEAM_NUMBER 10

char manufacturer[] = "Rose-Hulman";
char model[] = "GOLFBALLSTAND";
char versionStr[] = "1.0";


AndroidAccessory acc(manufacturer, model, model, versionStr,
                     "https://youtu.be/QrGrOK8oZG8",
                     "12345");

int ballColor_1, ballColor_2, ballColor_3;

byte rxBuf[255];
char txBuf[64];
int wheelCurrentReplyLength = 0;
int batteryVoltageReplyLength = 0;

/***  Pin I/O   ***/ 
#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3
#define PIN_SELECT_BUTTON 21

/*** Interrupt flags ***/
volatile int mainEventFlags = 0;
#define FLAG_INTERRUPT_0                   0x0001
#define FLAG_INTERRUPT_1                   0x0002
#define FLAG_INTERRUPT_2                   0x0004
#define FLAG_NEED_TO_SEND_BATTERY_VOLTAGE  0x0008
#define FLAG_NEED_TO_SEND_WHEEL_CURRENT    0x0010

bool done = false;
char message[3];
char message2[1];

RobotAsciiCom robotAsciiCom;
GolfBallStand stand;
WildThumperCom wildThumperCom(TEAM_NUMBER);
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
    pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_SELECT_BUTTON, INPUT_PULLUP);
  attachInterrupt(0, int0_isr, FALLING);
  attachInterrupt(1, int1_isr, FALLING);
  attachInterrupt(2, int2_isr, FALLING);

   // Register callbacks for commands you might receive from Android.
  robotAsciiCom.registerWheelSpeedCallback(wheelSpeedMessageFromAndroid);
  robotAsciiCom.registerPositionCallback(positionMessageFromAndroid);
  robotAsciiCom.registerJointAngleCallback(jointAngleMessageFromAndroid);
  robotAsciiCom.registerGripperCallback(gripperMessageFromAndroid);
  robotAsciiCom.registerAttachSelectedServosCallback(attachSelectedServosCallback);
  robotAsciiCom.registerBatteryVoltageRequestCallback(batteryVoltageRequestFromAndroid);
  robotAsciiCom.registerWheelCurrentRequestCallback(wheelCurrentRequestFromAndroid);
  robotAsciiCom.registerCustomStringCallback(customStringCallbackFromAndroid);
  
  // Register callbacks for commands you might receive from the Wild Thumper.
  wildThumperCom.registerBatteryVoltageReplyCallback(batteryVoltageReplyFromThumper);
  wildThumperCom.registerWheelCurrentReplyCallback(wheelCurrentReplyFromThumper);

    //attach servos
  wildThumperCom.sendAttachSelectedServos(0x3F);
  
  delay(1500);
  acc.powerOn();
}

void loop() {
  //  while (digitalRead(PIN_GOLF_BALL_STAND_SWITCH)) {
  //    // Do nothing until the switch is pressed.
  //  }
  if (acc.isConnected()) {
    int len = acc.read(rxBuf, sizeof(rxBuf), 1);
    if (len > 0) {
        robotAsciiCom.handleRxBytes(rxBuf, len);
    }
    if(done){
      done = false;
      message2[0] = 'L';
      acc.write(message, sizeof(message));
    }
    // Passing commands from the Wild Thumper on up to Android.
    if (mainEventFlags & FLAG_NEED_TO_SEND_BATTERY_VOLTAGE) {
      mainEventFlags &= ~FLAG_NEED_TO_SEND_BATTERY_VOLTAGE;
      acc.write(txBuf, batteryVoltageReplyLength);
    }
    if (mainEventFlags & FLAG_NEED_TO_SEND_WHEEL_CURRENT) {
      mainEventFlags &= ~FLAG_NEED_TO_SEND_WHEEL_CURRENT;
      acc.write(txBuf, wheelCurrentReplyLength);
    }
  }
  if(Serial.available() > 0){
    wildThumperCom.handleRxByte(Serial.read());
  }
  if (!digitalRead(PIN_GOLF_BALL_STAND_SWITCH)) {
    runColors();
  }

}

//Prints to serial the ball color
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

//runs through the colors trying to find the right ones.
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

  sendColors();
}


void sendColors() {
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

void wheelSpeedMessageFromAndroid(byte leftMode, byte rightMode, byte leftDutyCycle, byte rightDutyCycle) {
  wildThumperCom.sendWheelSpeed(leftMode, rightMode, leftDutyCycle, rightDutyCycle);  
}

void positionMessageFromAndroid(int joint1Angle, int joint2Angle, int joint3Angle, int joint4Angle, int joint5Angle) {
  wildThumperCom.sendPosition(joint1Angle, joint2Angle, joint3Angle, joint4Angle, joint5Angle);  
}

void jointAngleMessageFromAndroid(byte jointNumber, int jointAngle) {
  wildThumperCom.sendJointAngle(jointNumber, jointAngle);
}

void gripperMessageFromAndroid(int gripperDistance) {
  gripperDistance = constrain(gripperDistance, 10, 65);
  wildThumperCom.sendGripperDistance(gripperDistance);
}
   
void attachSelectedServosCallback(byte servosToEnable) {
  wildThumperCom.sendAttachSelectedServos(servosToEnable);
}

void batteryVoltageRequestFromAndroid(void) {
  wildThumperCom.sendBatteryVoltageRequest();
}

void wheelCurrentRequestFromAndroid(void) {
  wildThumperCom.sendWheelCurrentRequest();
}

void customStringCallbackFromAndroid(String customString) {
  if (customString.equalsIgnoreCase("TEST")) {
    // perform that command
    runColors();
  } else {
    Serial.println("Unknown Custom Command");
  }
}

void batteryVoltageReplyFromThumper(int batteryMillivolts) {
  // Send to Android from within the main event loop.
  mainEventFlags |= FLAG_NEED_TO_SEND_BATTERY_VOLTAGE;
  batteryVoltageReplyLength = robotAsciiCom.prepareBatteryVoltageReply(
      batteryMillivolts, txBuf, sizeof(txBuf));
}

void wheelCurrentReplyFromThumper(int leftWheelMotorsMilliamps, int rightWheelMotorsMilliamps) {
  // Send to Android from within the main event loop.
  mainEventFlags |= FLAG_NEED_TO_SEND_WHEEL_CURRENT;
  wheelCurrentReplyLength = robotAsciiCom.prepareWheelCurrentReply(
      leftWheelMotorsMilliamps, rightWheelMotorsMilliamps, txBuf, sizeof(txBuf));
}

void int0_isr() {
  mainEventFlags |= FLAG_INTERRUPT_0;
}

void int1_isr() {
  mainEventFlags |= FLAG_INTERRUPT_1;
}

void int2_isr() {
  mainEventFlags |= FLAG_INTERRUPT_2;
}

