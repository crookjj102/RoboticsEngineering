package edu.rosehulman.onegoodgpsreading;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

public class OneGoodGpsReadingActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_good_gps_reading);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentStateTextView = (TextView) findViewById(R.id.current_state_textview);
        mStateTimeTextView = (TextView) findViewById(R.id.state_time_textview);
        mGpsInfoTextView = (TextView) findViewById(R.id.gps_info_textview);
        mSensorOrientationTextView = (TextView) findViewById(R.id.orientation_textview);
    }


    public void handleRedTeamGo(View view) {
        Toast.makeText(this, "You clicked Red Team Go!", Toast.LENGTH_SHORT).show();
    }

    public void handleBlueTeamGo(View view) {
        Toast.makeText(this, "You clicked Blue Team Go!", Toast.LENGTH_SHORT).show();
    }

    public void handleFakeGps(View view) {
        Toast.makeText(this, "You clicked Fake GPS Signal", Toast.LENGTH_SHORT).show();
    }

    public void handleMissionComplete(View view) {
        Toast.makeText(this, "You clicked Mission Complete!", Toast.LENGTH_SHORT).show();
    }
}
