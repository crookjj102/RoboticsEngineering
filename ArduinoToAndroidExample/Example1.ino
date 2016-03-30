#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define PIN_LED 13

char manufacturer[] = "Rose-Hulman";
char model[] = "My LED Toggle Prep";
char versionStr[] = "1.0";
char onMsg[] = "I'm sorry, Dave. I can't let you do that.";
char offMsg[] = "Dave, this conversation can serve no purpose anymore. Goodbye.";


AndroidAccessory acc = AndroidAccessory(
	manufacturer,
	model, 
	"bullshit",
	versionStr,
	"bullshit",
	"bullshit");
char rxBuffer[255];

void setup()
{
	pinMode(PIN_LED, OUTPUT);
	delay(1500);
	acc.powerOn();

}

void loop()
{
	if(acc.isConnected()){
		int len = acc.read(rxBuffer, sizeof(rxBuffer), 1);//buffer, lengh, num retries. 
		if(len>0){//we got a new message.
			rxBuffer[len - 1] = '\0';
			String inputString = String(rxBuffer);

			if(inputString.equalsIgnoreCase("ON")){
				digitalWrite(PIN_LED, HIGH);
				acc.write(onMsg, sizeof(onMsg));
			}
			else if (inputString.equalsIgnoreCase("OFF")){
				digitalWrite(PIN_LED,LOW);
				acc.write(offMsg, sizeof(offMsg));
			}
		}
	}
	else{
		//do nothing
	}
}