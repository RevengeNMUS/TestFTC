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
            run();
        }
    }

    public void run(){
        move(inputCheck(pastButton));
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
        boolean rightTrigger = gamepad1.right_trigger > TRIGGER_THRESHOLD;

        buttonInputs(pastB);

        joystickInterupt(pastB);
        
        //joystick check
            if(rightTrigger) {
                //Check if rotation should be affected by SLOW_MODE_FACTOR
                current_motion = new DriveMotion(gamepad1.left_stick_y * SLOW_MODE_FACTOR, gamepad1.left_stick_x * SLOW_MODE_FACTOR, gamepad1.right_stick_x * SLOW_MODE_FACTOR);
                return current_motion;
            }
            current_motion = new DriveMotion(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            return current_motion;

        if(pastB != null) {
            if(!pastB.motionIsActive() && repeatedPresses > 0){
                repeatedPresses--;
                pastB.init();
            }
            
            return pastB.motion();
        }

        return DriveMotion.ZERO;
    }

    public void joystickInterupt(DriveActionSequence pastButton){
        if ((gamepad1.left_stick_y > JOYSTICK_THRESHOLD || gamepad1.left_stick_y < -JOYSTICK_THRESHOLD) ||
            (gamepad1.left_stick_x > JOYSTICK_THRESHOLD || gamepad1.left_stick_x < -JOYSTICK_THRESHOLD)) {

            pastButton = null;

            dPadRightPressed = false;
            dPadLeftPressed = false;
            dPadDownPressed = false;
            dPadUpPressed = false;
            xButtonPressed = false;
        }
    }

    public void buttonInputs(DriveActionSequence pastButton){
        boolean dPadDown = gamepad1.dpadDownWasPressed();
        boolean dPadLeft = gamepad1.dpadLeftWasPressed();
        boolean dPadRight = gamepad1.dpadRightWasPressed();
        boolean dPadUp = gamepad1.dpadUpWasPressed();
        boolean xButton = gamepad1.xWasPressed();

        if (xButton) {

            if (xButtonPressed && pastButton.motionIsActive()) {
                pastButton = new DriveActionSequence(xButtonMovement);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(xButtonMovement);
                repeatedPresses = 0;
                pastButton.init();

            }
            xButtonPressed = true;
            dPadDownPressed = false;
            dPadUpPressed = false;
            dPadRightPressed = false;
            dPadLeftPressed = false;

        } else if (dPadDown) {

            if (dPadDownPressed && pastButton.motionIsActive()) {
                pastButton = new DriveActionSequence(downMovement);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(downMovement);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonPressed = false;
            dPadDownPressed = true;
            dPadUpPressed = false;
            dPadRightPressed = false;
            dPadLeftPressed = false;

        } else if (dPadUp) {

            if (dPadUpPressed && pastButton.motionIsActive()){
                pastButton = new DriveActionSequence(upMovement);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(upMovement);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonPressed = false;
            dPadDownPressed = false;
            dPadUpPressed = true;
            dPadRightPressed = false;
            dPadLeftPressed = false;

        } else if (dPadRight) {

            if (dPadRightPressed && pastButton.motionIsActive()){
                pastButton = new DriveActionSequence(rightMovement);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(rightMovement);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonPressed = false;
            dPadDownPressed = false;
            dPadUpPressed = false;
            dPadRightPressed = true;
            dPadLeftPressed = false;

        } else if (dPadLeft) {

            if (dPadLeftPressed && pastButton.motionIsActive()){
                pastButton = new DriveActionSequence(leftMovement);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(leftMovement);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonPressed = false;
            dPadDownPressed = false;
            dPadUpPressed = false;
            dPadRightPressed = false;
            dPadLeftPressed = true;
        }
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