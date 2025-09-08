package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

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

    Buttons buttons = new Buttons(gamepad1);

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

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

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
    public void run() {
        updateDrivetrain(inputCheck());
    }

    /**
     * Checks input and returns the DriveMotion that the robot should be
     * affected by.
     *
     * @return DM that should affect the robot
     */
    public DriveMotion inputCheck(){
        //assume that gamepad presses take priority if both the gamepad is pressed and the joystick is moved
        //assuming you cant turn during movement using dpad
        handlePastButtonPress();
        buttonInputs();
        joystickInterrupt();

        //not joystick interrupt
        if(pastButton != null){
            return pastButton.motion();
        } else {
            //yes joystick interrupt
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
            return new DriveMotion(-gamepad1.left_stick_y * SLOW_MODE_FACTOR,
                                   gamepad1.left_stick_x * SLOW_MODE_FACTOR,
                                   gamepad1.right_stick_x * SLOW_MODE_FACTOR);
        }
        return new DriveMotion(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
    }

    /**
     * Handles a motion caused by a previously pressed button
     * that has motion that has not ended yet.
     */
    public void handlePastButtonPress(){
        if(pastButton == null)
            return;

        if(!pastButton.motionIsActive()){
            if(repeatedPresses > 0) {
                pastButton.init();
                repeatedPresses--;
            } else {
                reset();
            }
        }
    }


    public void reset(){
        pastButton = null;

        dPadRightLastPressed = false;
        dPadLeftLastPressed = false;
        dPadDownLastPressed = false;
        dPadUpLastPressed = false;
        xButtonLastPressed = false;

        repeatedPresses = 0;
    }

    /**
     * Handles overwriting button input (past or present) in favor
     * of joystick input
     */
    public void joystickInterrupt(){
        if (
            (Math.abs(gamepad1.left_stick_y) > JOYSTICK_THRESHOLD)
            || (Math.abs(gamepad1.left_stick_x) > JOYSTICK_THRESHOLD)
            || (Math.abs(gamepad1.right_stick_x) > JOYSTICK_THRESHOLD)
        ) {
            reset();
        }
    }

    /**
     * Handles all button inputs
     */
    public void buttonInputs(){
        buttons.update();

        if (buttons.xWasPressed()) {

            if (xButtonLastPressed && pastButton != null && pastButton.motionIsActive()) {
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

        } else if (buttons.dpaddownWasPressed()) {

            if (dPadDownLastPressed && pastButton != null && pastButton.motionIsActive()) {
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

        } else if (buttons.dpadupWasPressed()) {

            if (dPadUpLastPressed && pastButton != null && pastButton.motionIsActive()){
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

        } else if (buttons.dpadrightWasPressed()) {

            if (dPadRightLastPressed && pastButton != null && pastButton.motionIsActive()){
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

        } else if (buttons.dpadleftWasPressed()) {

            if (dPadLeftLastPressed && pastButton != null && pastButton.motionIsActive()){
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
        frontLeft.setPower(dsr.drive + dsr.strafe + dsr.rotate);
        frontRight.setPower(dsr.drive - dsr.strafe - dsr.rotate);
        backLeft.setPower(dsr.drive - dsr.strafe + dsr.rotate);
        backRight.setPower(dsr.drive + dsr.strafe - dsr.rotate);
    }
}