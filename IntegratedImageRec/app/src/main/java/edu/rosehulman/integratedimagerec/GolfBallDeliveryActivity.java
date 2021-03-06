package edu.rosehulman.integratedimagerec;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import edu.rosehulman.me435.NavUtils;

public class GolfBallDeliveryActivity extends ImageRecActivity
{

    /**
     * Constant used with logging that you'll see later.
     */
    public static final String TAG = "GolfBallDelivery";

    /**
     * An enum used for variables when a ball color needs to be referenced.
     */
    public enum BallColor
    {
        NONE, BLUE, RED, YELLOW, GREEN, BLACK, WHITE
    }

    public enum State
    {
        WARMUP,
        READY_FOR_MISSION,
        DRIVE_TOWARD_NEAR_BALL,
        NEAR_BALL_SCRIPT,
        DRIVE_TOWARD_FAR_BALL,
        FAR_BALL_SCRIPT,
        DRIVE_TOWARDS_HOME,
        WAITING_FOR_PICKUP,
        SEEKING_HOME
    }

    public static State mState;
    private Scripts mScripts;

    /**
     * An array (of size 3) that stores what color is present in each golf ball stand location.
     */
    public BallColor[] mLocationColors = new BallColor[]{BallColor.NONE, BallColor.NONE, BallColor.NONE};

    /**
     * Simple boolean that is updated when the Team button is pressed to switch teams.
     */
    public boolean mOnRedTeam = false;


    // ---------------------- UI References ----------------------
    /**
     * An array (of size 3) that keeps a reference to the 3 balls displayed on the UI.
     */
    private ImageButton[] mBallImageButtons;

    /**
     * References to the buttons on the UI that can change color.
     */
    private Button mTeamChangeButton, mGoOrMissionCompleteButton;

    private Button mStartStop;

    private CircularMovingAverageArray orientationFilter;

    /**
     * An array constants (of size 7) that keeps a reference to the different ball color images resources.
     */
    // Note, the order is important and must be the same throughout the app.
    private static final int[] BALL_DRAWABLE_RESOURCES = new int[]{R.drawable.none_ball, R.drawable.blue_ball,
            R.drawable.red_ball, R.drawable.yellow_ball, R.drawable.green_ball, R.drawable.black_ball, R.drawable.white_ball};

    /**
     * TextViews that can change values.
     */
    private TextView mCurrentStateTextView, mStateTimeTextView, mGpsInfoTextView, mSensorOrientationTextView,
            mGuessXYTextView, mLeftDutyCycleTextView, mRightDutyCycleTextView, mMatchTimeTextView;

    private TextView mJumboXTextView, mJumboYTextView;
    // ---------------------- End of UI References ----------------------


    // ---------------------- Mission strategy values ----------------------
    /**
     * Constants for the known locations.
     */
    public static final long NEAR_BALL_GPS_X = 90;
    public static final long FAR_BALL_GPS_X = 240;


    /**
     * Variables that will be either 50 or -50 depending on the balls we get.
     */
    private double mNearBallGpsY, mFarBallGpsY;

    /**
     * If that ball is present the values will be 1, 2, or 3.
     * If not present the value will be 0.
     * For example if we have the black ball, then mWhiteBallLocation will equal 0.
     */
    public int mNearBallLocation, mFarBallLocation, mWhiteBallLocation;
    // ----------------- End of mission strategy values ----------------------


    // ---------------------------- Timing area ------------------------------
    /**
     * Time when the state began (saved as the number of millisecond since epoch).
     */
    private long mStateStartTime;

    /**
     * Time when the match began, ie when Go! was pressed (saved as the number of millisecond since epoch).
     */
    private long mMatchStartTime;

    /**
     * Constant that holds the maximum length of the match (saved in milliseconds).
     */
    private long MATCH_LENGTH_MS = 300000; // 5 minutes in milliseconds (5 * 60 * 1000)
    // ----------------------- End of timing area --------------------------------


    // ---------------------------- Driving area ---------------------------------
    /**
     * When driving towards a target, using a seek strategy, consider that state a success when the
     * GPS distance to the target is less than (or equal to) this value.
     */
    public static final double ACCEPTED_DISTANCE_AWAY_FT = 10.0; // Within 10 feet is close enough.

    /**
     * Multiplier used during seeking to calculate a PWM value based on the turn amount needed.
     */
    private static final double SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER = 3.0;  // units are (PWM value)/degrees

    /**
     * Variable used to cap the slowest PWM duty cycle used while seeking. Pick a value from -255 to 255.
     */
    private static final int LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE = 150;

    /**
     * PWM duty cycle values used with the drive straight dialog that make your robot drive straightest.
     */
    public int mLeftStraightPwmValue = 255, mRightStraightPwmValue = 255;
    // ------------------------ End of Driving area ------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /////////////////////////////////////////////////////////////
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBallImageButtons = new ImageButton[]{(ImageButton) findViewById(R.id.location_1_image_button),
                (ImageButton) findViewById(R.id.location_2_image_button),
                (ImageButton) findViewById(R.id.location_3_image_button)};
        mTeamChangeButton = (Button) findViewById(R.id.team_change_button);
        mCurrentStateTextView = (TextView) findViewById(R.id.current_state_textview);
        mStateTimeTextView = (TextView) findViewById(R.id.state_time_textview);
        mGpsInfoTextView = (TextView) findViewById(R.id.gps_info_textview);
        mSensorOrientationTextView = (TextView) findViewById(R.id.orientation_textview);
        mGuessXYTextView = (TextView) findViewById(R.id.guess_location_textview);
        mLeftDutyCycleTextView = (TextView) findViewById(R.id.left_duty_cycle_textview);
        mRightDutyCycleTextView = (TextView) findViewById(R.id.right_duty_cycle_textview);
        mMatchTimeTextView = (TextView) findViewById(R.id.match_time_textview);
        mGoOrMissionCompleteButton = (Button) findViewById(R.id.go_or_mission_complete_button);

        orientationFilter = new CircularMovingAverageArray(10);
        mScripts = new Scripts(this);

        mJumboXTextView = (TextView) findViewById(R.id.jumbo_x);
        mJumboYTextView = (TextView) findViewById(R.id.jumbo_y);
        mStartStop = (Button) findViewById(R.id.Stop_Go);

        //mStartStop.

        // When you start using the real hardware you don't need test buttons.
        boolean hideFakeGpsButtons = true;
        if (hideFakeGpsButtons)
        {
            Toast.makeText(this, "time to hide these buttons", Toast.LENGTH_SHORT).show();
            TableLayout fakeGpsButtonTable = (TableLayout) findViewById(R.id.fake_gps_button_table);
            fakeGpsButtonTable.setVisibility(View.GONE);
        }
        setState(State.WARMUP);

    }

    /**
     * Use this helper method to set the color of a ball.
     * The location value here is 1 based.  Send 1, 2, or 3
     * Side effect: Updates the UI with the appropriate ball color resource image.
     */
    public void setLocationToColor(int location, BallColor ballColor)
    {
        mBallImageButtons[location - 1].setImageResource(BALL_DRAWABLE_RESOURCES[ballColor.ordinal()]);
        mLocationColors[location - 1] = ballColor;
    }

    /**
     * Used to get the state time in milliseconds.
     */
    private long getStateTimeMs()
    {
        return System.currentTimeMillis() - mStateStartTime;
    }

    /**
     * Used to get the match time in milliseconds.
     */
    private long getMatchTimeMs()
    {
        return System.currentTimeMillis() - mMatchStartTime;
    }


    // --------------------------- Methods added ---------------------------


    // --------------------------- Drive command ---------------------------


    // --------------------------- Sensor listeners ---------------------------


    // --------------------------- Button Handlers ----------------------------

    /**
     * Helper method that is called by all three golf ball clicks.
     */
    private void handleBallClickForLocation(final int location)
    {
        new DialogFragment()
        {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("What was the real color?").setItems(R.array.ball_colors,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                GolfBallDeliveryActivity.this.setLocationToColor(location, BallColor.values()[which]);
                            }
                        });
                return builder.create();
            }
        }.show(getFragmentManager(), "unused tag");

    }

    /**
     * Click for jumbotron thing
     */

    public void goAndOrStop(View view)
    {
        Toast.makeText(this, "should probably set the state or something", Toast.LENGTH_SHORT).show();
    }

    /**
     * Click to the far left image button (Location 1).
     */
    public void handleBallAtLocation1Click(View view)
    {
        handleBallClickForLocation(1);
    }

    /**
     * Click to the center image button (Location 2).
     */
    public void handleBallAtLocation2Click(View view)
    {
        handleBallClickForLocation(2);
    }

    /**
     * Click to the far right image button (Location 3).
     */
    public void handleBallAtLocation3Click(View view)
    {
        handleBallClickForLocation(3);
    }

    /**
     * Sets the mOnRedTeam boolean value as appropriate
     * Side effects: Clears the balls
     *
     * @param view
     */
    public void handleTeamChange(View view)
    {
        setLocationToColor(1, BallColor.NONE);
        setLocationToColor(2, BallColor.NONE);
        setLocationToColor(3, BallColor.NONE);
        if (mOnRedTeam)
        {
            mOnRedTeam = false;
            mTeamChangeButton.setBackgroundResource(R.drawable.blue_button);
            mTeamChangeButton.setText("Team Blue");
        }
        else
        {
            mOnRedTeam = true;
            mTeamChangeButton.setBackgroundResource(R.drawable.red_button);
            mTeamChangeButton.setText("Team Red");
        }
        // setTeamToRed(mOnRedTeam); // This call is optional. It will reset your GPS and sensor heading values.
    }

    /**
     * Sends a message to Arduino to perform a ball color test.
     */
    public void handlePerformBallTest(View view)
    {
        sendCommand("TEST");
    }

    /**
     * Clicks to the red arrow image button that should show a dialog window.
     */
    public void handleDrivingStraight(View view)
    {
        Toast.makeText(this, "handleDrivingStraight", Toast.LENGTH_SHORT).show();
        new DialogFragment()
        {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Driving Straight Calibration");
                View dialoglayout = getLayoutInflater().inflate(R.layout.driving_straight_dialog, (ViewGroup) getCurrentFocus());
                builder.setView(dialoglayout);
                final NumberPicker rightDutyCyclePicker = (NumberPicker) dialoglayout.findViewById(R.id.right_pwm_number_picker);
                rightDutyCyclePicker.setMaxValue(255);
                rightDutyCyclePicker.setMinValue(0);
                rightDutyCyclePicker.setValue(mRightStraightPwmValue);
                rightDutyCyclePicker.setWrapSelectorWheel(false);
                final NumberPicker leftDutyCyclePicker = (NumberPicker) dialoglayout.findViewById(R.id.left_pwm_number_picker);
                leftDutyCyclePicker.setMaxValue(255);
                leftDutyCyclePicker.setMinValue(0);
                leftDutyCyclePicker.setValue(mLeftStraightPwmValue);
                leftDutyCyclePicker.setWrapSelectorWheel(false);
                Button doneButton = (Button) dialoglayout.findViewById(R.id.done_button);
                doneButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mLeftStraightPwmValue = leftDutyCyclePicker.getValue();
                        mRightStraightPwmValue = rightDutyCyclePicker.getValue();
                        dismiss();
                    }
                });
                final Button testStraightButton = (Button) dialoglayout.findViewById(R.id.test_straight_button);
                testStraightButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mLeftStraightPwmValue = leftDutyCyclePicker.getValue();
                        mRightStraightPwmValue = rightDutyCyclePicker.getValue();
                        Toast.makeText(GolfBallDeliveryActivity.this, "TODO: Implement the drive straight test", Toast.LENGTH_SHORT).show();
                        mScripts.testStraightDriveScript();
                    }
                });
                return builder.create();
            }
        }.show(getFragmentManager(), "unused tag");
    }

    /**
     * Test GPS point when going to the Far ball (assumes Blue Team heading to red ball).
     */
    public void handleFakeGpsF0(View view)
    {
        onLocationChanged(165, 50, NO_HEADING, null);
    }

    public void handleFakeGpsF1(View view)
    {
        onLocationChanged(209, 50, 0, null);
    }

    public void handleFakeGpsF2(View view)
    {
        onLocationChanged(231, 50, 135, null);
    }

    public void handleFakeGpsF3(View view)
    {
        onLocationChanged(240, 41, 35, null);
    }

    public void handleFakeGpsH0(View view)
    {
        onLocationChanged(165, 0, -180, null);
    }

    public void handleFakeGpsH1(View view)
    {
        onLocationChanged(11, 0, -180, null);
    }

    public void handleFakeGpsH2(View view)
    {
        onLocationChanged(9, 0, -170, null);
    }

    public void handleFakeGpsH3(View view)
    {
        onLocationChanged(0, -9, -170, null);
    }

    public void handleSetOrigin(View view)
    {
        mFieldGps.setCurrentLocationAsOrigin();
    }

    public void handleSetXAxis(View view)
    {
        mFieldGps.setCurrentLocationAsLocationOnXAxis();
    }

    public void handleZeroHeading(View view)
    {
        mFieldOrientation.setCurrentFieldHeading(0.0);
        orientationFilter.clearArray();
    }

    public void handleGoOrMissionComplete(View view)
    {
        if (mState == State.WARMUP)
        {
            //do nothing.
        }
        else if (mState == State.READY_FOR_MISSION)
        {
            mMatchStartTime = System.currentTimeMillis();
            updateMissionStrategyVariables();
            mGoOrMissionCompleteButton.setBackgroundResource(R.drawable.red_button);
            mGoOrMissionCompleteButton.setText("EMERGENCY STOP");
            setState(State.DRIVE_TOWARD_NEAR_BALL);
        }
        else if (mState == State.WAITING_FOR_PICKUP)
        {
            setState(State.READY_FOR_MISSION);
            mGoOrMissionCompleteButton.setText("GO!");
        }
        else
        {
            mMatchStartTime = System.currentTimeMillis();
            updateMissionStrategyVariables();
            mGoOrMissionCompleteButton.setBackgroundResource(R.drawable.green_button);
            mGoOrMissionCompleteButton.setText("GO!");
            setState(State.READY_FOR_MISSION);
        }
    }

    @Override
    protected void onCommandReceived(String receivedCommand)
    {
        super.onCommandReceived(receivedCommand);

        setColorFromSerialCommand(receivedCommand.charAt(0), 1);
        setColorFromSerialCommand(receivedCommand.charAt(1), 2);
        setColorFromSerialCommand(receivedCommand.charAt(2), 3);

    }

    private void setColorFromSerialCommand(char letter, int loc)
    {
        switch (letter)
        {
            case 'A':
                setLocationToColor(loc, BallColor.NONE);
                break;
            case '0':
                setLocationToColor(loc, BallColor.BLACK);
                break;
            case '1':
                setLocationToColor(loc, BallColor.BLUE);
                break;
            case '2':
                setLocationToColor(loc, BallColor.GREEN);
                break;
            case '3':
                setLocationToColor(loc, BallColor.RED);
                break;
            case '4':
                setLocationToColor(loc, BallColor.YELLOW);
                break;
            case '5':
                setLocationToColor(loc, BallColor.WHITE);
                break;
            default:
                //do nothing.
        }
    }

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location)
    {
        super.onLocationChanged(x, y, heading, location);
        String gpsInfo = getString(R.string.xy_format, x, y);
        if (heading <= 180.0 && heading > -180.0)
        {
            gpsInfo += " " + getString(R.string.degrees_format, heading);
        }
        else
        {
            gpsInfo += " ?°";
        }
        gpsInfo += "    " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);


        if (mState == State.DRIVE_TOWARD_FAR_BALL)
        {
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY,
                    FAR_BALL_GPS_X, mFarBallGpsY);
            if (distanceFromTarget < ACCEPTED_DISTANCE_AWAY_FT)
            {
                setState(State.FAR_BALL_SCRIPT);
            }
        }
        else if (mState == State.DRIVE_TOWARDS_HOME)
        {
            // Shorter to write since the RobotActivity already calculates the distance to 0, 0.
            if (mCurrentGpsDistance < ACCEPTED_DISTANCE_AWAY_FT + 10)//allow it some extra reach room since we can keep trying
            {
                setState(State.WAITING_FOR_PICKUP);
            }
        }else if (mState == State.DRIVE_TOWARD_NEAR_BALL){
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY, NEAR_BALL_GPS_X, mNearBallGpsY);
            if(distanceFromTarget < ACCEPTED_DISTANCE_AWAY_FT){
                setState(State.NEAR_BALL_SCRIPT);
            }
        }

    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues)
    {
        orientationFilter.addNewData(fieldHeading);
        super.onSensorChanged(fieldHeading, orientationValues);
        mSensorOrientationTextView.setText(getString(R.string.degrees_format, fieldHeading));
    }

    public void setState(State newState)
    {
        mStateStartTime = System.currentTimeMillis();
        if (mState == State.WARMUP && newState != State.READY_FOR_MISSION)
        {
            Toast.makeText(this, "ILLEGAL TRANSITION FROM WARMUP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mState == State.READY_FOR_MISSION && newState != State.DRIVE_TOWARD_NEAR_BALL)
        {
            Toast.makeText(this, "ILLEGAL TRANSITION FROM READY STATE", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentStateTextView.setText(newState.name());
        mState = newState;
        switch (newState)
        {
            case WARMUP:
                sendWheelSpeed(0, 0);
                break;
            case READY_FOR_MISSION:
                mGoOrMissionCompleteButton.setBackgroundResource(R.drawable.green_button);
                mGoOrMissionCompleteButton.setText("GO!");
                sendWheelSpeed(0, 0);
                break;
            case DRIVE_TOWARD_NEAR_BALL:
                mGpsInfoTextView.setText("---");
                mGuessXYTextView.setText("---");
//                mScripts.nearBallScript();

                ViewFlipper localFlipper = (ViewFlipper) findViewById(R.id.my_view_flipper);
                localFlipper.setDisplayedChild(2);
                break;
            case NEAR_BALL_SCRIPT:
                sendWheelSpeed(0,0);
                mScripts.removeBallAtLocation(mNearBallLocation);
                break;
            case DRIVE_TOWARD_FAR_BALL:
                //mScripts.removeBallAtLocation(mNearBallLocation);
                break;
            case FAR_BALL_SCRIPT:
                sendWheelSpeed(0,0);
                mScripts.removeBallAtLocation(mFarBallLocation);
                if (mWhiteBallLocation != 0)
                {
                    //drop off white ball while we still can
                    mCommandHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mScripts.removeBallAtLocation(mWhiteBallLocation);
                        }
                    }, 3000);

                }
//                mScripts.farBallScript();
                break;
            case DRIVE_TOWARDS_HOME:
                break;
            case SEEKING_HOME:
                //actions handled in loop
                break;
            default:
                //do nothing
                break;
        }

    }

    /**
     * Updates the mission strategy variables.
     */
    private void updateMissionStrategyVariables()
    {
        // mNearBallGpsY = -50.0; // Note, X value is a constant.
        //mFarBallGpsY = 50.0; // Note, X value is a constant.

        for(int i = 0; i < 3; i++) {
            BallColor ballColor = mLocationColors[i];
            int location = i+1;
            if (mOnRedTeam) {
                if (ballColor == BallColor.BLUE) {
                    mFarBallLocation = location;
                    mFarBallGpsY = -50;
                } else if (ballColor == BallColor.YELLOW) {
                    mFarBallLocation = location;
                    mFarBallGpsY = 50;
                } else if (ballColor == BallColor.GREEN) {
                    mNearBallGpsY = -50;
                    mNearBallLocation = location;
                } else if (ballColor == BallColor.RED) {
                    mNearBallGpsY = 50;
                    mNearBallLocation = location;
                } else if (ballColor == BallColor.WHITE) {
                    mWhiteBallLocation = location;
                } else if (ballColor == BallColor.BLACK) {
                    mWhiteBallLocation = 0;
                }
            } else//Blue team.
            {
                if (ballColor == BallColor.BLUE) {
                    mNearBallLocation = location;
                    mNearBallGpsY = 50;
                } else if (ballColor == BallColor.YELLOW) {
                    mNearBallLocation = location;
                    mNearBallGpsY = -50;
                } else if (ballColor == BallColor.GREEN) {
                    mFarBallGpsY = 50;
                    mFarBallLocation = location;
                } else if (ballColor == BallColor.RED) {
                    mFarBallGpsY = -50;
                    mFarBallLocation = location;
                } else if (ballColor == BallColor.WHITE) {
                    mWhiteBallLocation = location;
                } else if (ballColor == BallColor.BLACK) {
                    mWhiteBallLocation = 0;
                }
            }
        }

        Log.d(TAG, "Near ball is position " + mNearBallLocation + " so drive to " + mNearBallGpsY);
        Log.d(TAG, "Far ball is position " + mFarBallLocation + " so drive to " + mFarBallGpsY);
        Log.d(TAG, "White ball is position " + mWhiteBallLocation);
    }

    @Override
    public void sendWheelSpeed(int leftDutyCycle, int rightDutyCycle)
    {
        super.sendWheelSpeed(leftDutyCycle, rightDutyCycle);
        mLeftDutyCycleTextView.setText("LEFT\n" + leftDutyCycle);
        mRightDutyCycleTextView.setText("RIGHT\n" + rightDutyCycle);
    }

    /**
     * Method that is called 10 times per second for updates. Note, the setup was done within RobotActivity.
     */
    public void loop()
    {
        super.loop(); // Important to call super first so that the RobotActivity loop function is run first.
        // RobotActivity updated the mGuessX and mGuessY already. Here we need to display it.
        mStateTimeTextView.setText("" + getStateTimeMs() / 1000);
        mGuessXYTextView.setText("(" + (int) mGuessX + ", " + (int) mGuessY + ")");

        mJumboXTextView.setText("" + (int) mGuessX);
        mJumboYTextView.setText("" + (int) mGuessY);

        // Match timer.
        long matchTimeMs;
        long timeRemainingSeconds = MATCH_LENGTH_MS / 1000;
        if (mState != State.READY_FOR_MISSION && mState != State.WARMUP)
        {
            matchTimeMs = getMatchTimeMs();
            timeRemainingSeconds = (MATCH_LENGTH_MS - matchTimeMs) / 1000;
            if (getMatchTimeMs() > MATCH_LENGTH_MS)
            {
                setState(State.READY_FOR_MISSION);
            }
        }
        mMatchTimeTextView.setText(getString(R.string.time_format, timeRemainingSeconds / 60, timeRemainingSeconds % 60));

        switch (mState)
        {
            case WARMUP:
                if (getStateTimeMs() > 3000)
                {
                    setState(State.READY_FOR_MISSION);
                }
                break;
            case DRIVE_TOWARD_NEAR_BALL:
                if(getStateTimeMs() > 6000){
                    setState(State.NEAR_BALL_SCRIPT);
                }else{
                    seekTargetAt(NEAR_BALL_GPS_X, mNearBallGpsY);
                }
                break;
            case NEAR_BALL_SCRIPT:
                if(getStateTimeMs() > 6000) {
                    setState(State.DRIVE_TOWARD_FAR_BALL);
                }
                break;

            case DRIVE_TOWARD_FAR_BALL:

                if (getStateTimeMs() > 7000)
                {

                    setState(State.FAR_BALL_SCRIPT);
                }else {
                    seekTargetAt(FAR_BALL_GPS_X, mFarBallGpsY);
                }
                break;
            case FAR_BALL_SCRIPT:
                if(getStateTimeMs() > 6000) {
                    setState(State.DRIVE_TOWARDS_HOME);
                }
                break;
            case DRIVE_TOWARDS_HOME:
                seekTargetAt(0, 0);
                if(getStateTimeMs() > 7000){
                    setState(State.WAITING_FOR_PICKUP);
                }
                break;
            case WAITING_FOR_PICKUP:
                sendWheelSpeed(0,0);
                mGoOrMissionCompleteButton.setText("Complete Mission");
                mGoOrMissionCompleteButton.setBackgroundResource(R.drawable.green_button);
                if (getStateTimeMs() > 3000)//wait for 3 seconds to be shut off
                {
                    setState(State.SEEKING_HOME);
                }
                break;
            case SEEKING_HOME:
                if(getMatchTimeMs() >= 300000){
                    setState(State.WAITING_FOR_PICKUP);
                }
                seekTargetAt(0, 0);
                if (getStateTimeMs() > 8000)//try for 8 seconds to finish making it home
                {
                    setState(State.WAITING_FOR_PICKUP);
                }


                break;
            default:
                // Other states don't need to do anything, but could.
                break;
        }

    }

    /**
     * Adjust the PWM duty cycles based on the turn amount needed to point at the target heading.
     *
     * @param x GPS X value of the target.
     * @param y GPS Y value of the target.
     */
    private void seekTargetAt(double x, double y)
    {
        int leftDutyCycle = mLeftStraightPwmValue;
        int rightDutyCycle = mRightStraightPwmValue;
        double targetHeading = NavUtils.getTargetHeading(mGuessX, mGuessY, x, y);
        double leftTurnAmount = NavUtils.getLeftTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        double rightTurnAmount = NavUtils.getRightTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        if (leftTurnAmount < rightTurnAmount)
        {
            leftDutyCycle = mLeftStraightPwmValue - (int) (leftTurnAmount * SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER);
            leftDutyCycle = Math.max(leftDutyCycle, LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE);
        }
        else
        {
            rightDutyCycle = mRightStraightPwmValue - (int) (rightTurnAmount * SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER);
            rightDutyCycle = Math.max(rightDutyCycle, LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE);
        }
        sendWheelSpeed(leftDutyCycle, rightDutyCycle);
    }
}








