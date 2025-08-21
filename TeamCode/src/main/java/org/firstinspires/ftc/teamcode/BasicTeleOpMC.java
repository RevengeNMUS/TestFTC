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
 *
 */
@TeleOp(name = "BasicMotionController", group = "idk")
public class BasicTeleOpMC extends LinearOpMode {
    //Joystick Threshhold
    public static final double THRESHOLD = 0.1;
    //current motion
    DriveMotion motion = DriveMotion.ZERO;
    DriveActionSequence dPadDown = new DriveActionSequence(new DriveMotion(-0.5, 0, 0), 1000);
    DriveActionSequence dPadUp = new DriveActionSequence(new DriveMotion(0.5, 0, 0), 1000);
    DriveActionSequence dPadRight = new DriveActionSequence(new DriveMotion(0, 0.5, 0), 1000);
    DriveActionSequence dPadLeft = new DriveActionSequence(new DriveMotion(0, -0.5, 0), 1000);
    DriveActionSequence xButton = new DriveActionSequence(new DriveMotion[]{new DriveMotion(0.5, 0, 0), new DriveMotion(0, 0, -0.5)}, 1000);
    DriveActionSequence pastButton = null;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    boolean xButtonPressed = false;
    boolean dPadDownPressed = false;
    boolean dPadUpPressed = false;
    boolean dPadRightPressed = false;
    boolean dPadLeftPressed = false;


    private int repeatedPresses = 0;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        waitForStart();

        while(opModeIsActive()){
            move(inputCheck(motion, pastButton));
        }
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
        if (gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up || gamepad1.x) {

            if (gamepad1.xWasPressed()) {

                if (xButtonPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(xButton);
                xButtonPressed = true;
                dPadDownPressed = false;
                dPadUpPressed = false;
                dPadRightPressed = false;
                dPadLeftPressed = false;

            } else if (gamepad1.dpadDownWasPressed()) {

                if (dPadDownPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(dPadDown);
                xButtonPressed = false;
                dPadDownPressed = true;
                dPadUpPressed = false;
                dPadRightPressed = false;
                dPadLeftPressed = false;

            } else if (gamepad1.dpadUpWasPressed()) {

                if (dPadUpPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(dPadUp);
                xButtonPressed = false;
                dPadDownPressed = false;
                dPadUpPressed = true;
                dPadRightPressed = false;
                dPadLeftPressed = false;

            } else if (gamepad1.dpadRightWasPressed()) {

                if (dPadRightPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(dPadRight);
                xButtonPressed = false;
                dPadDownPressed = false;
                dPadUpPressed = false;
                dPadRightPressed = true;
                dPadLeftPressed = false;

            } else if (gamepad1.dpadLeftWasPressed()) {

                if (dPadLeftPressed && pastB.motion() != DriveMotion.ZERO)
                    repeatedPresses++;
                else
                    repeatedPresses = 0;
                pastB = new DriveActionSequence(dPadLeft);
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
        if ((gamepad1.left_stick_y > THRESHOLD || gamepad1.left_stick_y < -THRESHOLD) || (gamepad1.left_stick_x > THRESHOLD || gamepad1.left_stick_x < -THRESHOLD)) {
            pastB = null;
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