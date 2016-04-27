package edu.rosehulman.onegoodgpsreading;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

import edu.rosehulman.onegoodgpsreading.me435.AccessoryActivity;
import edu.rosehulman.onegoodgpsreading.me435.FieldGps;
import edu.rosehulman.onegoodgpsreading.me435.FieldGpsListener;
import edu.rosehulman.onegoodgpsreading.me435.FieldOrientation;
import edu.rosehulman.onegoodgpsreading.me435.FieldOrientationListener;
import edu.rosehulman.onegoodgpsreading.me435.NavUtils;

enum State
{
    READY,
    REDSCRIPT,
    BLUESCRIPT,
    WAITINGGPS,
    DRIVINGHOME,
    SEEKINGHOME,
    WAITINGPICKUP
}

public class OneGoodGpsReadingActivity extends AccessoryActivity implements FieldGpsListener,
        FieldOrientationListener
{
    // Various constants and member variable names.
    public static final int LOWEST_DESIRABLE_DUTY_CYCLE = 150;
    public static final int LEFT_PWM_VALUE_FOR_STRAIGHT = 245;
    public static final int RIGHT_PWM_VALUE_FOR_STRAIGHT = 255;

    private static final String TAG = "OneGoodGps";
    private static final double NO_HEADING_KNOWN = 360.0;
    private TextView mCurrentStateTextView, mStateTimeTextView, mGpsInfoTextView, mSensorOrientationTextView;
    private int mGpsCounter = 0;
    private double mCurrentGpsX, mCurrentGpsY, mCurrentGpsHeading;
    private double mCurrentSensorHeading;
    private Handler mCommandHandler = new Handler();
    private Timer mTimer;
    public static final int LOOP_INTERVAL_MS = 100;

    private FieldGps mFieldGps;
    private FieldOrientation mFieldOrientation;

    private State currentState;
    private int currentStateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_good_gps_reading);

        mFieldGps = new FieldGps(this);
        mFieldOrientation = new FieldOrientation(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentStateTextView = (TextView) findViewById(R.id.current_state_textview);
        mStateTimeTextView = (TextView) findViewById(R.id.state_time_textview);
        mGpsInfoTextView = (TextView) findViewById(R.id.gps_info_textview);
        mSensorOrientationTextView = (TextView) findViewById(R.id.orientation_textview);
        currentState = State.READY;
        mCurrentStateTextView.setText("" + currentState);

        currentStateTime = 0;

        getStateTime();

    }


    public void handleRedTeamGo(View view)
    {
        if (currentState == State.READY)
        {
            currentState = State.REDSCRIPT;
            mCurrentStateTextView.setText("" + currentState);
            currentStateTime = 0;

            //Toast.makeText(this, "You clicked Red Team Go!", Toast.LENGTH_SHORT).show();
            redScript();
        }


    }

    public void handleBlueTeamGo(View view)
    {
        if (currentState == State.READY)
        {
            currentState = State.BLUESCRIPT;
            mCurrentStateTextView.setText("" + currentState);
            currentStateTime = 0;

            //Toast.makeText(this, "You clicked Blue Team Go!", Toast.LENGTH_SHORT).show();
            blueScript();
        }

    }

    public void handleFakeGps(View view)
    {
        
        onLocationChanged(40,10,135,null);
        //Toast.makeText(this, "You clicked Fake GPS Signal", Toast.LENGTH_SHORT).show();
    }

    public void handleMissionComplete(View view)
    {
        if (currentState == State.WAITINGPICKUP)
        {
            currentState = State.READY;
            mCurrentStateTextView.setText("" + currentState);
            currentStateTime = 0;
        }
        Toast.makeText(this, "You clicked Mission Complete!", Toast.LENGTH_SHORT).show();
        sendCommand("CUSTOM WubbaLubbaDubDub");
        mGpsInfoTextView.setText("---");
    }

    public void onCommandRecieved(String receivedCommand)
    {
        super.onCommandReceived(receivedCommand);
        Toast.makeText(this, "Received: " + receivedCommand, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //mFieldOrientation.registerListener(this);
        //mFieldGps.requestLocationUpdates(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //mFieldOrientation.unregisterListener();
        //mFieldGps.removeUpdates();
    }
/*
***************GPS Methods***********
 */


    @Override
    public void onLocationChanged(double x, double y, double heading, Location location)
    {
        mGpsCounter++;
        mCurrentGpsX = x;
        mCurrentGpsY = y;
        mCurrentGpsHeading = NO_HEADING_KNOWN;
        String gpsInfo = getString(R.string.xy_format, x, y);
        if (heading <= 180 && heading > -180)
        {
            if (currentState == State.WAITINGGPS)
            {
                currentState = State.DRIVINGHOME;
                mCurrentStateTextView.setText("" + currentState);
                currentStateTime = 0;

            }
            gpsInfo += " " + getString(R.string.degrees_format, heading);
            mCurrentGpsHeading = heading;
            mCurrentSensorHeading = heading;
            mSensorOrientationTextView.setText(getString(R.string.degrees_format, heading));
        }
        else
        {
            gpsInfo += " ?°";
        }
        gpsInfo += "   " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);
        if (currentState == State.DRIVINGHOME)
        {
            homeScript();// keep this from blocking in the middle of obtaining GPS reading.
        }
    }

    /*
    ******Field Orientation Sensor
     */

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues)
    {
        mCurrentSensorHeading = fieldHeading;
        mSensorOrientationTextView.setText(getString(R.string.degrees_format, fieldHeading));
    }

    /*
   ****RED SCRIPT***
    */
    public void redScript()
    {
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(OneGoodGpsReadingActivity.this, "RED", Toast.LENGTH_SHORT).show();
                sendCommand("WHEEL SPEED FORWARD 175 FORWARD 200");
            }
        }, 0);

        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(OneGoodGpsReadingActivity.this, "DRIVING", Toast.LENGTH_SHORT).show();
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 175");
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 150");
                currentStateTime = 0;
                currentState = State.WAITINGGPS;
                mCurrentStateTextView.setText("" + currentState);
            }
        }, 4000);


    }

    /*
    ****BLUE SCRIPT
     */
    public void blueScript()
    {
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(OneGoodGpsReadingActivity.this, "BLUE", Toast.LENGTH_SHORT).show();
                sendCommand("WHEEL SPEED FORWARD 200 FORWARD 175");
            }
        }, 0);

        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(OneGoodGpsReadingActivity.this, "DRIVING", Toast.LENGTH_SHORT).show();
                sendCommand("WHEEL SPEED FORWARD 175 FORWARD 150");
            }
        }, 2000);

        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 150");
                currentStateTime = 0;
                currentState = State.WAITINGGPS;
                mCurrentStateTextView.setText("" + currentState);
            }
        }, 4000);

    }

    public void homeScript()
    {
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(OneGoodGpsReadingActivity.this, "DRIVING", Toast.LENGTH_SHORT).show();
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 250");
            }
        }, 0);

        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(OneGoodGpsReadingActivity.this, "HOME", Toast.LENGTH_SHORT).show();
            }
        }, 3000);

        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                sendCommand("WHEEL SPEED BRAKE 0 BRAKE 0");
                currentState = State.WAITINGPICKUP;
                currentStateTime = 0;
                mCurrentStateTextView.setText("" + currentState);
            }
        }, 5000);

    }

    public void getStateTime()
    {


        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                currentStateTime++;
                mStateTimeTextView.setText("" + currentStateTime);
                if(currentState == State.SEEKINGHOME){
                    seekTargetAt(0,0);
                }
                stateTimeouts();
                getStateTime();//recursive with no termination. I know, I know.
            }
        }, 1000);


    }

    public void stateTimeouts()
    {
        if (currentState == State.WAITINGGPS && currentStateTime >= 5)
        {
            currentStateTime = 0;
            mStateTimeTextView.setText("0");
            currentState = State.SEEKINGHOME;
            mCurrentStateTextView.setText(""+currentState);

        }
        else if (currentState == State.SEEKINGHOME && currentStateTime >= 8)
        {
            currentStateTime = 0;
            mStateTimeTextView.setText("0");
            currentState = State.WAITINGPICKUP;
            mCurrentStateTextView.setText(""+currentState);
        }
        else if (currentState == State.WAITINGPICKUP && currentStateTime >= 8)
        {
            currentStateTime = 0;
            mStateTimeTextView.setText("0");
            currentState = State.SEEKINGHOME;
            mCurrentStateTextView.setText(""+currentState);
        }
        else
        {
            //do nothing
        }
    }
    private void seekTargetAt(double xTarget, double yTarget) {
        int leftDutyCycle = LEFT_PWM_VALUE_FOR_STRAIGHT;
        int rightDutyCycle = RIGHT_PWM_VALUE_FOR_STRAIGHT;
        double targetHeading = NavUtils.getTargetHeading(mCurrentGpsX, mCurrentGpsY, xTarget, yTarget);
        double leftTurnAmount = NavUtils.getLeftTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        double rightTurnAmount = NavUtils.getRightTurnHeadingDelta(mCurrentSensorHeading, targetHeading);

        if (leftTurnAmount < rightTurnAmount) {
            leftDutyCycle = LEFT_PWM_VALUE_FOR_STRAIGHT - (int)leftTurnAmount; // Using a VERY simple plan. :)
            leftDutyCycle = Math.max(leftDutyCycle, LOWEST_DESIRABLE_DUTY_CYCLE);
        } else {
            rightDutyCycle = RIGHT_PWM_VALUE_FOR_STRAIGHT - (int)rightTurnAmount; // Could also scale it.
            rightDutyCycle = Math.max(rightDutyCycle, LOWEST_DESIRABLE_DUTY_CYCLE);
        }
        sendCommand("WHEEL SPEED FORWARD " + leftDutyCycle + " FORWARD " + rightDutyCycle);
    }


}