package edu.rosehulman.integratedimagerec;

import android.os.Handler;
import android.widget.Toast;

import edu.rosehulman.me435.NavUtils;
import edu.rosehulman.me435.RobotActivity;

public class Scripts
{

    /**
     * Reference to the primary activity.
     */
    private GolfBallDeliveryActivity mGolfBallDeliveryActivity;

    /**
     * Handler used to create scripts in this class.
     */
    protected Handler mCommandHandler = new Handler();

    /**
     * Time in milliseconds needed to perform a ball removal.
     */
    private int ARM_REMOVAL_TIME_MS = 4000;

    /**
     * Simple constructor.
     */
    public Scripts(GolfBallDeliveryActivity golfBallDeliveryActivity)
    {
        mGolfBallDeliveryActivity = golfBallDeliveryActivity;
    }

    /**
     * Used to test your values for straight driving.
     */
    public void testStraightDriveScript()
    {
        Toast.makeText(mGolfBallDeliveryActivity, "Begin Short straight drive test at " +
                        mGolfBallDeliveryActivity.mLeftStraightPwmValue + "  " + mGolfBallDeliveryActivity.mRightStraightPwmValue,
                Toast.LENGTH_SHORT).show();
        mGolfBallDeliveryActivity.sendWheelSpeed(mGolfBallDeliveryActivity.mLeftStraightPwmValue, mGolfBallDeliveryActivity.mRightStraightPwmValue);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(mGolfBallDeliveryActivity, "End Short straight drive test", Toast.LENGTH_SHORT).show();
                mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
            }
        }, 8000);

    }

    /**
     * Runs the script to drive to the near ball (perfectly straight) and drop it off.
     */
    public void nearBallScript()
    {
        if (GolfBallDeliveryActivity.mState != GolfBallDeliveryActivity.State.READY_FOR_MISSION)
        {
            double distanceToNearBall = NavUtils.getDistance(15, 0, 90, 50);
            long driveTimeToNearBallMs = (long) (distanceToNearBall / RobotActivity.DEFAULT_SPEED_FT_PER_SEC * 1000);
            mGolfBallDeliveryActivity.sendWheelSpeed(mGolfBallDeliveryActivity.mLeftStraightPwmValue,
                    mGolfBallDeliveryActivity.mRightStraightPwmValue);
            mCommandHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mGolfBallDeliveryActivity.sendWheelSpeed(0,0);
                    removeBallAtLocation(mGolfBallDeliveryActivity.mNearBallLocation);
                }
            }, driveTimeToNearBallMs);

            mCommandHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mGolfBallDeliveryActivity.mState == GolfBallDeliveryActivity.State.NEAR_BALL_SCRIPT)
                    {
                        mGolfBallDeliveryActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARD_FAR_BALL);
                    }
                }
            }, driveTimeToNearBallMs + ARM_REMOVAL_TIME_MS);
        }
    }


    /**
     * Script to drop off the far ball.
     */
    public void farBallScript()
    {
        if (mGolfBallDeliveryActivity.mState != GolfBallDeliveryActivity.State.READY_FOR_MISSION)
        {

            removeBallAtLocation(mGolfBallDeliveryActivity.mFarBallLocation);
            mCommandHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {

                    if (mGolfBallDeliveryActivity.mWhiteBallLocation != 0)
                    {
                        removeBallAtLocation(mGolfBallDeliveryActivity.mWhiteBallLocation);
                    }

                    if (mGolfBallDeliveryActivity.mState == GolfBallDeliveryActivity.State.FAR_BALL_SCRIPT)
                    {
                        mGolfBallDeliveryActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARDS_HOME);
                        mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
                    }

                }
            }, ARM_REMOVAL_TIME_MS);
        }

    }


    // -------------------------------- Arm script(s) ----------------------------------------

    /**
     * Removes a ball from the golf ball stand.
     */
    public void removeBallAtLocation(final int location)
    {
        mGolfBallDeliveryActivity.sendCommand("ATTACH 111111"); // Just in case
        switch (location)
        {
            case 1:
                removeBallAtOne();
                break;
            case 2:
                removeBallAtTwo();
                break;
            case 3:
                removeBallAtThree();
                break;
            default:
                break;
        }
    }

    private void removeBallAtOne()
    {
        mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90");
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 36 40 -90 -1000 90");
            }
        }, 300);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 38 80 -81 -155 90");
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.setLocationToColor(1, GolfBallDeliveryActivity.BallColor.NONE);
            }
        }, ARM_REMOVAL_TIME_MS);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90");
            }
        }, 2000);
    }


    private void removeBallAtTwo()
    {
        mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90");
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 9 64 -55 -166 90");
            }
        }, 300);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 8 88 -79 -158 90");
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.setLocationToColor(2, GolfBallDeliveryActivity.BallColor.NONE);
            }
        }, ARM_REMOVAL_TIME_MS);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90");
            }
        }, 2000);
    }


    private void removeBallAtThree()
    {
        mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90");
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION -13 70 -90 -140 90");
            }
        }, 300);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION -28 90 -81 -155 90");
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.setLocationToColor(3, GolfBallDeliveryActivity.BallColor.NONE);
            }
        }, ARM_REMOVAL_TIME_MS);
        mCommandHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mGolfBallDeliveryActivity.sendCommand("POSITION 0 90 0 -90 90");
            }
        }, 2000);
    }
}
