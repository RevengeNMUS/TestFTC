package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Basic Motion Controller
 * Takes Joystick and DPad input, and moves the robot accordingly
 * Allows stacking of DPad input
 *
 * To-Do:
 * Debuging (get android studio fixed) and General Polishing
 * More descriptive description (¯\_(ツ)_/¯)
 * Make sure you didnt goof on any of the code you bagel
 * Add more comments cro
 *
 */
@TeleOp(name = "BasicMotionController", group = "idk")
public class BasicTeleOpMC extends LinearOpMode {
    //Joystick Threshhold
    public static final double JOYSTICK_THRESHOLD = 0.1;
    //Trigger Threshold
    public static final double TRIGGER_THRESHOLD = 0.2;
    //Slow mode factor
    public static final double SLOW_MODE_FACTOR = 0.3;

    //current motion of the robot
    DriveMotion motion = DriveMotion.ZERO;

    //DriveActionSequences to be activated by each button (See DriveActionSequence)
    DriveActionSequence downMovement = new DriveActionSequence(new DriveMotion(-0.5, 0, 0), 1000);
    DriveActionSequence upMovement = new DriveActionSequence(new DriveMotion(0.5, 0, 0), 1000);
    DriveActionSequence rightMovement = new DriveActionSequence(new DriveMotion(0, 0.5, 0), 1000);
    DriveActionSequence leftMovement = new DriveActionSequence(new DriveMotion(0, -0.5, 0), 1000);
    DriveActionSequence xButtonMovement = new DriveActionSequence(new DriveMotion[]{new DriveMotion(0.5, 0, 0), new DriveMotion(0, 0, -0.5)}, 1000);

    //Keeps track of the last button pressed (for continuing motion, and helping with layering)
    DriveActionSequence pastButton = null;

    //Motors
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    //Booleans to keep track of the past buttons (Layer purposes)
    boolean xButtonPressed = false;
    boolean dPadDownPressed = false;
    boolean dPadUpPressed = false;
    boolean dPadRightPressed = false;
    boolean dPadLeftPressed = false;

    //Amount of times that button presses have been layered
    private int repeatedPresses = 0;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        waitForStart();

        while(opModeIsActive()){
            move(inputCheck(motion, pastButton));
        }

        move(new DriveMotion(0,0,0));
    }

    /**
     * Check Input
     * (vars currentMotion and motion ARE unneeded, but might be useful in the future, so I havent phased it out.)
     *
     * @param pastB The last button pressed
     * @param current_motion The current motion of the bot
     *
     * Input Prefrence:
     * DriveActionSequence - Top Priority, if DriveActionSequence is pressed then all other input is not considered
     * Joystick - Second Priority
     * Previous DriveActionSequence Press - Third Priority
     * Nothing - Causes Robot to stop motion
     *
     *
     */
    public DriveMotion inputCheck(DriveMotion current_motion, DriveActionSequence pastB){
        //assume that gamepad presses take priority if both the gamepad is pressed and the joystick is moved
        //assuming you cant turn during movement using dpad


        //Screenshot of inputs
        boolean dPadDown = gamepad1.dpadDownWasPressed();
        boolean dPadLeft = gamepad1.dpadLeftWasPressed();
        boolean dPadRight = gamepad1.dpadRightWasPressed();
        boolean dPadUp = gamepad1.dpadUpWasPressed();
        boolean xButton = gamepad1.xWasPressed();
        boolean rightTrigger = gamepad1.right_trigger > TRIGGER_THRESHOLD;

        if (dPadDown || dPadLeft || dPadRight || dPadUp || xButton) {

            if (xButton) {

                if (xButtonPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(xButtonMovement);
                xButtonPressed = true;
                dPadDownPressed = false;
                dPadUpPressed = false;
                dPadRightPressed = false;
                dPadLeftPressed = false;

            } else if (dPadDown) {

                if (dPadDownPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(downMovement);
                xButtonPressed = false;
                dPadDownPressed = true;
                dPadUpPressed = false;
                dPadRightPressed = false;
                dPadLeftPressed = false;

            } else if (dPadUp) {

                if (dPadUpPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(upMovement);
                xButtonPressed = false;
                dPadDownPressed = false;
                dPadUpPressed = true;
                dPadRightPressed = false;
                dPadLeftPressed = false;

            } else if (dPadRight) {

                if (dPadRightPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(rightMovement);
                xButtonPressed = false;
                dPadDownPressed = false;
                dPadUpPressed = false;
                dPadRightPressed = true;
                dPadLeftPressed = false;

            } else if (dPadLeft) {

                if (dPadLeftPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(leftMovement);
                xButtonPressed = false;
                dPadDownPressed = false;
                dPadUpPressed = false;
                dPadRightPressed = false;
                dPadLeftPressed = true;
            }

            current_motion = pastB.motion();
            return current_motion;
        }

        //joystick check
        if ((gamepad1.left_stick_y > JOYSTICK_THRESHOLD || gamepad1.left_stick_y < -JOYSTICK_THRESHOLD) || (gamepad1.left_stick_x > JOYSTICK_THRESHOLD || gamepad1.left_stick_x < -JOYSTICK_THRESHOLD)) {
            pastB = null;

            dPadRightPressed = false;
            dPadLeftPressed = false;
            dPadDownPressed = false;
            dPadUpPressed = false;
            xButtonPressed = false;

            if(rightTrigger) {
                //Check if rotation should be affected by SLOW_MODE_FACTOR
                current_motion = new DriveMotion(gamepad1.left_stick_y * SLOW_MODE_FACTOR, gamepad1.left_stick_x * SLOW_MODE_FACTOR, gamepad1.right_stick_x * SLOW_MODE_FACTOR);
                return current_motion;
            }
            current_motion = new DriveMotion(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            return current_motion;


        }

        if(pastB != null) {
            if(pastB.motion() == DriveMotion.ZERO && !(repeatedPresses <= 0)){
                repeatedPresses--;
                pastB.init();
            }

            current_motion = pastB.motion();
            return current_motion;
        }

        return DriveMotion.ZERO;
    }

    /**
     * Used to move the robot based on given DSR
     *
     * @param dsr the drive strafe rotation for movement
     */
    public void move(DriveMotion dsr){
        frontLeft.setPower(dsr.drive - dsr.strafe + dsr.rotate);
        frontRight.setPower(dsr.drive + dsr.strafe - dsr.rotate);
        backLeft.setPower(dsr.drive + dsr.strafe + dsr.rotate);
        backRight.setPower(dsr.drive - dsr.strafe - dsr.rotate);
    }
}