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

    GamepadButtonInputs buttons;

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

        buttons = new GamepadButtonInputs(gamepad1);

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
        if(buttons.wasPressed(Button.RIGHTTRIGGER)) {
            //Check if rotation should be affected by SLOW_MODE_FACTOR
            return new DriveMotion(-gamepad1.left_stick_y * Constants.SLOW_MODE_FACTOR,
                                   gamepad1.left_stick_x * Constants.SLOW_MODE_FACTOR,
                                   gamepad1.right_stick_x * Constants.SLOW_MODE_FACTOR);
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
            (Math.abs(gamepad1.left_stick_y) > Constants.JOYSTICK_THRESHOLD)
            || (Math.abs(gamepad1.left_stick_x) > Constants.JOYSTICK_THRESHOLD)
            || (Math.abs(gamepad1.right_stick_x) > Constants.JOYSTICK_THRESHOLD)
        ) {
            reset();
        }
    }

    /**
     * Handles all button inputs
     */
    public void buttonInputs(){
        buttons.update();

        if (buttons.wasPressed(Button.X)) {
            // todo: rename these variables pertaining to the motion/action, not the buttons
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

        } else if (buttons.wasPressed(Button.DPADDOWN)) {

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

        } else if (buttons.wasPressed(Button.DPADUP)) {

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

        } else if (buttons.wasPressed(Button.DPADRIGHT)) {

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

        } else if (buttons.wasPressed(Button.DPADLEFT)) {

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