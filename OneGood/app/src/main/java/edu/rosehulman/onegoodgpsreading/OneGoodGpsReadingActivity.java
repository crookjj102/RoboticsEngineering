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
    }


    public void handleRedTeamGo(View view)
    {
        if (currentState == State.READY)
        {
            currentState = State.REDSCRIPT;
            mCurrentStateTextView.setText(""+currentState);
        }

        Toast.makeText(this, "You clicked Red Team Go!", Toast.LENGTH_SHORT).show();
    }

    public void handleBlueTeamGo(View view)
    {
        if(currentState == State.READY)
        {
            currentState = State.BLUESCRIPT;
            mCurrentStateTextView.setText(""+currentState);
        }
        Toast.makeText(this, "You clicked Blue Team Go!", Toast.LENGTH_SHORT).show();
    }

    public void handleFakeGps(View view)
    {
        Toast.makeText(this, "You clicked Fake GPS Signal", Toast.LENGTH_SHORT).show();
    }

    public void handleMissionComplete(View view)
    {
        if(currentState==State.WAITINGPICKUP)
        {
            currentState = State.READY;
            mCurrentStateTextView.setText(""+currentState);
        }
        Toast.makeText(this, "You clicked Mission Complete!", Toast.LENGTH_SHORT).show();
        sendCommand("CUSTOM WubbaLubbaDubDub");
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
            gpsInfo += " " + getString(R.string.degrees_format, heading);
            mCurrentGpsHeading = heading;
            mCurrentSensorHeading = heading;
            mSensorOrientationTextView.setText(getString(R.string.degrees_format, heading));
        } else
        {
            gpsInfo += " ?°";
        }
        gpsInfo += "   " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);
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
}
