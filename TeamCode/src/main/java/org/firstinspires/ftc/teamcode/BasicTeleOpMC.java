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
 * More descriptive description (¯\_(ツ)_/¯)
 * Make sure you didnt goof on any of the code you crouton
 * Add more comments cro 💔
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

    //DriveActionSequences to be activated by each button (See DriveActionSequence)
    DriveActionSequence backwardsMotion = new DriveActionSequence(new DriveMotion(-0.5, 0, 0), 1000);
    DriveActionSequence upMotion = new DriveActionSequence(new DriveMotion(0.5, 0, 0), 1000);
    DriveActionSequence rightMotion = new DriveActionSequence(new DriveMotion(0, 0.5, 0), 1000);
    DriveActionSequence leftMotion = new DriveActionSequence(new DriveMotion(0, -0.5, 0), 1000);
    DriveActionSequence forwardTurnMotion = new DriveActionSequence(new DriveMotion[]{new DriveMotion(0.5, 0, 0), new DriveMotion(0, 0, -0.5)}, 1000);

    //Keeps track of the last button pressed (for continuing motion, and helping with layering)
    DriveActionSequence pastButton = null;
    //Motors
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    //Booleans to keep track of the past buttons (Layer purposes)
    boolean xButtonLastPressed = false;
    boolean dPadDownLastPressed = false;
    boolean dPadUpLastPressed = false;
    boolean dPadRightLastPressed = false;
    boolean dPadLeftLastPressed = false;

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

    /**
     * Repeatedly called when the OpMode is started.
     */
    public void run(){
        updateDrivetrain(inputCheck(pastButton));
    }

    /**
     * Checks input and returns the DriveMotion that the robot should be
     * affected by.
     *
     * @param pastB The last button pressed
     * @return DM that should affect the robot
     */
    public DriveMotion inputCheck(DriveActionSequence pastB){
        //assume that gamepad presses take priority if both the gamepad is pressed and the joystick is moved
        //assuming you cant turn during movement using dpad

        handlePastButtonPress(pastB);
        buttonInputs(pastB);
        joystickInterupt(pastB);

        //not joystick interupt
        if(pastB != null){
            return pastB.motion();
        } else {
            //yes joystick interupt
            return joyStickInput();
        }
    }

    /**
     * Handles input using joysticks, and uses those inputs to make a DriveMotion
     * to move the robot by.
     * @return the DriveMotion
     */
    public DriveMotion joyStickInput(){
        boolean rightTrigger = gamepad1.right_trigger > TRIGGER_THRESHOLD;

        if(rightTrigger) {
            //Check if rotation should be affected by SLOW_MODE_FACTOR
            return new DriveMotion(gamepad1.left_stick_y * SLOW_MODE_FACTOR,
                                   gamepad1.left_stick_x * SLOW_MODE_FACTOR,
                                   gamepad1.right_stick_x * SLOW_MODE_FACTOR);
        }
        return new DriveMotion(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
    }

    /**
     * Handles a motion caused by a previously pressed button
     * that has motion that has not ended yet.
     * @param pastButton the last button pressed (overwritten)
     */
    public void handlePastButtonPress(DriveActionSequence pastButton){
        if(pastButton != null) {
            if(!pastButton.motionIsActive() && repeatedPresses > 0){
                repeatedPresses--;
                pastButton.init();
            }
        }
    }

    /**
     * Handles overwriting button input (past or present) in favor
     * of joystick input
     * @param pastButton last button pressed
     */
    public void joystickInterupt(DriveActionSequence pastButton){
        if ((gamepad1.left_stick_y > JOYSTICK_THRESHOLD || gamepad1.left_stick_y < -JOYSTICK_THRESHOLD) ||
            (gamepad1.left_stick_x > JOYSTICK_THRESHOLD || gamepad1.left_stick_x < -JOYSTICK_THRESHOLD)) {

            pastButton = null;

            dPadRightLastPressed = false;
            dPadLeftLastPressed = false;
            dPadDownLastPressed = false;
            dPadUpLastPressed = false;
            xButtonLastPressed = false;
        }
    }

    /**
     * Handles all button inputs
     * @param pastButton last button pressed
     */
    public void buttonInputs(DriveActionSequence pastButton){
        boolean dPadDown = gamepad1.dpadDownWasPressed();
        boolean dPadLeft = gamepad1.dpadLeftWasPressed();
        boolean dPadRight = gamepad1.dpadRightWasPressed();
        boolean dPadUp = gamepad1.dpadUpWasPressed();
        boolean xButton = gamepad1.xWasPressed();

        if (xButton) {

            if (xButtonLastPressed && pastButton.motionIsActive()) {
                pastButton = new DriveActionSequence(forwardTurnMotion);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(forwardTurnMotion);
                repeatedPresses = 0;
                pastButton.init();

            }
            xButtonLastPressed = true;
            dPadDownLastPressed = false;
            dPadUpLastPressed = false;
            dPadRightLastPressed = false;
            dPadLeftLastPressed = false;

        } else if (dPadDown) {

            if (dPadDownLastPressed && pastButton.motionIsActive()) {
                pastButton = new DriveActionSequence(backwardsMotion);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(backwardsMotion);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonLastPressed = false;
            dPadDownLastPressed = true;
            dPadUpLastPressed = false;
            dPadRightLastPressed = false;
            dPadLeftLastPressed = false;

        } else if (dPadUp) {

            if (dPadUpLastPressed && pastButton.motionIsActive()){
                pastButton = new DriveActionSequence(upMotion);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(upMotion);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonLastPressed = false;
            dPadDownLastPressed = false;
            dPadUpLastPressed = true;
            dPadRightLastPressed = false;
            dPadLeftLastPressed = false;

        } else if (dPadRight) {

            if (dPadRightLastPressed && pastButton.motionIsActive()){
                pastButton = new DriveActionSequence(rightMotion);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(rightMotion);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonLastPressed = false;
            dPadDownLastPressed = false;
            dPadUpLastPressed = false;
            dPadRightLastPressed = true;
            dPadLeftLastPressed = false;

        } else if (dPadLeft) {

            if (dPadLeftLastPressed && pastButton.motionIsActive()){
                pastButton = new DriveActionSequence(leftMotion);
                repeatedPresses++;
            } else {
                pastButton = new DriveActionSequence(leftMotion);
                repeatedPresses = 0;
                pastButton.init();
            }
            xButtonLastPressed = false;
            dPadDownLastPressed = false;
            dPadUpLastPressed = false;
            dPadRightLastPressed = false;
            dPadLeftLastPressed = true;
        }
    }

    /**
     * Used to move the robot's drivetrain based on given DriveMotion
     *
     * @param dsr the DriveMotion for movement
     */
    public void updateDrivetrain(DriveMotion dsr){
        frontLeft.setPower(dsr.drive - dsr.strafe + dsr.rotate);
        frontRight.setPower(dsr.drive + dsr.strafe - dsr.rotate);
        backLeft.setPower(dsr.drive + dsr.strafe + dsr.rotate);
        backRight.setPower(dsr.drive - dsr.strafe - dsr.rotate);
    }
}