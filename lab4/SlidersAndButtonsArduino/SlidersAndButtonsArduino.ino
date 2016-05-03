#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <LiquidCrystal.h>
#include <Servo.h>
#include <ArmServos.h>
#include <ArmServosSpeedControlled.h>
#include <RobotAsciiCom.h>
#include <WildThumperCom.h>

char manufacturer[] = "Rose-Hulman";
char model[] = "My Sliders and Buttons";
char versionStr[] = "1.0";
        
// Only Manufacturer, Model, and Version matter to Android
AndroidAccessory acc(manufacturer,
                     model,
                     "Basic command set to carry out Arm Scripts",
                     versionStr,
                     "https://sites.google.com/site/me435spring2013/",
                     "12345");
byte rxBuf[255];
char txBuf[64];
// Note, when sending commands to Android I don't add the '\n'.
// Turned out to be easier to just assume the whole message arrives together.
// Seems to work fine.  Just separate into different write() commands.
char rightButtonScript[] = "ball_present";
char leftButtonScript[] = "center_ball";
char selectButtonScript[] = "starboard_ball";

/***  Pin I/O   ***/ 
#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3
#define PIN_SELECT_BUTTON 21

/*** Interrupt flags ***/
volatile int mainEventFlags = 0;
#define FLAG_INTERRUPT_0 0x0001
#define FLAG_INTERRUPT_1 0x0002
#define FLAG_INTERRUPT_2 0x0004
#define FLAG_NEED_TO_SEND_BATTERY_VOLTAGE  0x0008
#define FLAG_NEED_TO_SEND_WHEEL_CURRENT    0x0010

LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);
#define LINE_1 0
#define LINE_2 1

RobotAsciiCom robotCom;
WildThumperCom wildThumperCom(10);
int batteryVoltageReplyLength = 0;
int wheelCurrentReplyLength = 0;

void setup() {
  Serial.begin(9600);
  pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_SELECT_BUTTON, INPUT_PULLUP);
  attachInterrupt(0, int0_isr, FALLING);
  attachInterrupt(1, int1_isr, FALLING);
  attachInterrupt(2, int2_isr, FALLING);
  lcd.begin(16, 2);
  
  robotCom.registerPositionCallback(positionCallback);
  robotCom.registerJointAngleCallback(jointAngleCallback);
  robotCom.registerGripperCallback(gripperCallback);
  robotCom.registerAttachSelectedServosCallback(attachSelectedServosCallback);
  robotCom.registerWheelSpeedCallback(wheelSpeedMessageFromAndroid);
  robotCom.registerBatteryVoltageRequestCallback(batteryVoltageRequestFromAndroid);
  robotCom.registerWheelCurrentRequestCallback(wheelCurrentRequestFromAndroid);

    // Register callbacks for commands you might receive from the Wild Thumper.
  wildThumperCom.registerBatteryVoltageReplyCallback(batteryVoltageReplyFromThumper);
  wildThumperCom.registerWheelCurrentReplyCallback(wheelCurrentReplyFromThumper);

  //attach servos
  wildThumperCom.sendAttachSelectedServos(0x3F);
  
  lcd.clear();
  lcd.print("Ready");
  delay(1500);
  acc.powerOn();
}


void batteryVoltageRequestFromAndroid(void) {
  wildThumperCom.sendBatteryVoltageRequest();
}

void wheelCurrentRequestFromAndroid(void) {
  wildThumperCom.sendWheelCurrentRequest();
}

void batteryVoltageReplyFromThumper(int batteryMillivolts) {
  // Send to Android from within the main event loop.
  mainEventFlags |= FLAG_NEED_TO_SEND_BATTERY_VOLTAGE;
  batteryVoltageReplyLength = robotCom.prepareBatteryVoltageReply(
      batteryMillivolts, txBuf, sizeof(txBuf));
  // Display battery voltage on LCD.
  lcd.clear();
  lcd.print("Battery voltage:");
  lcd.setCursor(0, LINE_2);
  lcd.print(batteryMillivolts / 1000);
  lcd.print(".");
  if (batteryMillivolts % 1000  < 100) {
    lcd.print("0");
  }
  if (batteryMillivolts % 1000 < 10) {
    lcd.print("0");
  }
  lcd.print(batteryMillivolts % 1000);
}

void wheelCurrentReplyFromThumper(int leftWheelMotorsMilliamps, int rightWheelMotorsMilliamps) {
  // Send to Android from within the main event loop.
  mainEventFlags |= FLAG_NEED_TO_SEND_WHEEL_CURRENT;
  wheelCurrentReplyLength = robotCom.prepareWheelCurrentReply(
      leftWheelMotorsMilliamps, rightWheelMotorsMilliamps, txBuf, sizeof(txBuf));

  // Display wheel currents on LCD.
  lcd.clear();
  lcd.print("Wheel current:");
  lcd.setCursor(0, LINE_2);
  lcd.print(leftWheelMotorsMilliamps / 1000);
  lcd.print(".");
  if (leftWheelMotorsMilliamps % 1000  < 100) {
    lcd.print("0");
  }
  if (leftWheelMotorsMilliamps % 1000 < 10) {
    lcd.print("0");
  }
  lcd.print(leftWheelMotorsMilliamps % 1000);
  lcd.print("  ");
  lcd.print(rightWheelMotorsMilliamps / 1000);
  lcd.print(".");
  if (rightWheelMotorsMilliamps % 1000  < 100) {
    lcd.print("0");
  }
  if (rightWheelMotorsMilliamps % 1000 < 10) {
    lcd.print("0");
  }
  lcd.print(rightWheelMotorsMilliamps % 1000);
}

void wheelSpeedMessageFromAndroid(byte leftMode, byte rightMode, byte leftDutyCycle, byte rightDutyCycle) {
  wildThumperCom.sendWheelSpeed(leftMode, rightMode, leftDutyCycle, rightDutyCycle);  
  lcd.clear();
  lcd.print("Wheel speed:");
  lcd.setCursor(0, LINE_2);
  lcd.print("L");
  lcd.print(leftMode);
  lcd.print(" R");
  lcd.print(rightMode);
  lcd.print(" L");
  lcd.print(leftDutyCycle);
  lcd.print(" R");
  lcd.print(rightDutyCycle);
}

void positionCallback(int joint1Angle, int joint2Angle, int joint3Angle, int joint4Angle, int joint5Angle) {
  wildThumperCom.sendPosition(joint1Angle, joint2Angle, joint3Angle, joint4Angle, joint5Angle);
  lcd.clear();
  lcd.print("Position:");
  lcd.setCursor(0, LINE_2);
  lcd.print(joint1Angle);
  lcd.print(" ");
  lcd.print(joint2Angle);
  lcd.print(" ");
  lcd.print(joint3Angle);
  lcd.print(" ");
  lcd.print(joint4Angle);
  lcd.print(" ");
  lcd.print(joint5Angle);
}

void jointAngleCallback(byte jointNumber, int jointAngle) {
  wildThumperCom.sendJointAngle(jointNumber, jointAngle);
  lcd.clear();
  lcd.print("Joint angle:");
  lcd.setCursor(0, LINE_2);
  lcd.print("J");
  lcd.print(jointNumber);
  lcd.print(" move to ");
  lcd.print(jointAngle);
}

void gripperCallback(int gripperDistance) {
  if (gripperDistance < 10) {
    gripperDistance = 10;
  }
  wildThumperCom.sendGripperDistance(gripperDistance);
  lcd.clear();
  lcd.print("Gripper:");
  lcd.setCursor(0, LINE_2);
  lcd.print("Gripper to ");
  lcd.print(gripperDistance);
}   

void attachSelectedServosCallback(byte servosToEnable) {
  wildThumperCom.sendAttachSelectedServos(servosToEnable);
  lcd.clear();
  lcd.print("Attach:");
  lcd.setCursor(0, LINE_2);
  lcd.print("54321G = ");
  lcd.print(servosToEnable, BIN);
}

void loop() {
  // See if there is a new message from Android.
  if (acc.isConnected()) {
    int len = acc.read(rxBuf, sizeof(rxBuf), 1);
    if (len > 0) {
      robotCom.handleRxBytes(rxBuf, len);
    }
    if (mainEventFlags & FLAG_INTERRUPT_0) {
      delay(20);
      mainEventFlags &= ~FLAG_INTERRUPT_0;
      if (!digitalRead(PIN_RIGHT_BUTTON)) {
        acc.write(rightButtonScript, sizeof(rightButtonScript));
      }
    }
    if (mainEventFlags & FLAG_INTERRUPT_1) {
      delay(20);
      mainEventFlags &= ~FLAG_INTERRUPT_1;
      if (!digitalRead(PIN_LEFT_BUTTON)) {
        acc.write(leftButtonScript, sizeof(leftButtonScript));
      }
    }
    if (mainEventFlags & FLAG_INTERRUPT_2) {
      delay(20);
      mainEventFlags &= ~FLAG_INTERRUPT_2;
      if (!digitalRead(PIN_SELECT_BUTTON)) {
        acc.write(selectButtonScript, sizeof(selectButtonScript));
      }
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
  wildThumperCom.updateServos();
  
  //wildThumperCom.sendUpdateServos();
   if (Serial.available() > 0) {
    wildThumperCom.handleRxByte(Serial.read());
  }
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
