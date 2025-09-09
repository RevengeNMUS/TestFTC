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
 * todo: More descriptive description (¯\_(ツ)_/¯)
 * todo: Make sure you didnt goof on any of the code you crouton
 * todo: Add more comments cro 💔
 *
 */
@TeleOp(name = "BasicMotionController", group = "idk")
public class BasicTeleOpMC extends LinearOpMode {

    public static final double JOYSTICK_THRESHOLD = 0.1;
    //Slow mode factor
    public static final double SLOW_MODE_FACTOR = 0.3;

    //DriveActionSequences to be activated by each button (See DriveActionSequence)
    DriveActionSequence backwardsMotion = new DriveActionSequence(new DriveMotion(-0.5, 0, 0), 1000);
    DriveActionSequence upMotion = new DriveActionSequence(new DriveMotion(0.5, 0, 0), 1000);
    DriveActionSequence rightMotion = new DriveActionSequence(new DriveMotion(0, 0.5, 0), 1000);
    DriveActionSequence leftMotion = new DriveActionSequence(new DriveMotion(0, -0.5, 0), 1000);
    DriveActionSequence forwardTurnMotion = new DriveActionSequence(new DriveMotion[]{new DriveMotion(0.5, 0, 0), new DriveMotion(0, 0, -0.5)}, 1000);

    //Keeps track of the last button pressed (for continuing motion, and helping with layering)
    DriveActionSequence lastAction = null;


    //Motors
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    GamepadButtonInputs buttons;

    //Booleans to keep track of the past buttons (Layer purposes)
    boolean lastActionWasDance = false;
    boolean lastActionWasBackwards = false;
    boolean lastActionWasForwards = false;
    boolean lastActionWasRight = false;
    boolean lastActionWasLeft = false;

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
        if(lastAction != null){
            return lastAction.motion();
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
        if(buttons.wasPressed(Button.RIGHT_TRIGGER)) {
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
        if(lastAction == null)
            return;

        if(!lastAction.motionIsActive()){
            if(repeatedPresses > 0) {
                lastAction.init();
                repeatedPresses--;
            } else {
                reset();
            }
        }
    }


    public void reset(){
        lastAction = null;

        lastActionWasRight = false;
        lastActionWasLeft = false;
        lastActionWasBackwards = false;
        lastActionWasForwards = false;
        lastActionWasDance = false;

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

        if (buttons.wasPressed(Button.X)) {
            if (lastActionWasDance && lastAction != null && lastAction.motionIsActive()) {
                repeatedPresses++;
            } else {
                lastAction = new DriveActionSequence(forwardTurnMotion);
                repeatedPresses = 0;
                lastAction.init();

            }

            lastActionWasDance = true;
            lastActionWasBackwards = false;
            lastActionWasForwards = false;
            lastActionWasRight = false;
            lastActionWasLeft = false;

        } else if (buttons.wasPressed(Button.DPAD_DOWN)) {

            if (lastActionWasBackwards && lastAction != null && lastAction.motionIsActive()) {
                repeatedPresses++;
            } else {
                lastAction = new DriveActionSequence(backwardsMotion);
                repeatedPresses = 0;
                lastAction.init();
            }
            lastActionWasDance = false;
            lastActionWasBackwards = true;
            lastActionWasForwards = false;
            lastActionWasRight = false;
            lastActionWasLeft = false;

        } else if (buttons.wasPressed(Button.DPAD_UP)) {

            if (lastActionWasForwards && lastAction != null && lastAction.motionIsActive()){
                repeatedPresses++;
            } else {
                lastAction = new DriveActionSequence(upMotion);
                repeatedPresses = 0;
                lastAction.init();
            }
            lastActionWasDance = false;
            lastActionWasBackwards = false;
            lastActionWasForwards = true;
            lastActionWasRight = false;
            lastActionWasLeft = false;

        } else if (buttons.wasPressed(Button.DPAD_RIGHT)) {

            if (lastActionWasRight && lastAction != null && lastAction.motionIsActive()){
                repeatedPresses++;
            } else {
                lastAction = new DriveActionSequence(rightMotion);
                repeatedPresses = 0;
                lastAction.init();
            }
            lastActionWasDance = false;
            lastActionWasBackwards = false;
            lastActionWasForwards = false;
            lastActionWasRight = true;
            lastActionWasLeft = false;

        } else if (buttons.wasPressed(Button.DPAD_LEFT)) {

            if (lastActionWasLeft && lastAction != null && lastAction.motionIsActive()){
                repeatedPresses++;
            } else {
                lastAction = new DriveActionSequence(leftMotion);
                repeatedPresses = 0;
                lastAction.init();
            }
            lastActionWasDance = false;
            lastActionWasBackwards = false;
            lastActionWasForwards = false;
            lastActionWasRight = false;
            lastActionWasLeft = true;
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