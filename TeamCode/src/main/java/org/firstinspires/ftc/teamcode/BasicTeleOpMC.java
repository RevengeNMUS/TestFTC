package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Basic Motion Controller
 * Takes Joystick and DPad input, and moves the robot accordingly
 *
 * To-Do:
 * Debuging (get android studio fixed) and General Polishing
 * More efficient DPad checking?
 *
 */
@TeleOp(name = "BasicMotionController", group = "idk")
public class BasicTeleOpMC extends LinearOpMode {

    /**
     * Vector-like class that houses not only x and y but also rotation
     */
    private class DriveMotion {
        public final double drive;
        public final double strafe;
        public final double rotate;
        public static final DriveMotion ZERO = new DriveMotion(0, 0, 0);

        /**
         * Use constructor to define vars of this DriveMotion
         * @param d amount of forward movement
         * @param s amount of strafing
         * @param r amount of rotation (correlate this to smth, angles, radians, whatever)
         */
        public DriveMotion (double d, double s, double r) {
            drive = d;
            strafe = s;
            rotate = r;
        }
    }

    //Used to check if 1 second of DPad movement is up
    ElapsedTime dPadTimer = new ElapsedTime();

    private int dPadMovementTime = 1000;

    //Used to check if the current movement is beceause or not because of DPad movement
    boolean wasDPadPressed = false;

    //current motion
    DriveMotion motion = DriveMotion.ZERO;

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        waitForStart();

        while(opModeIsActive()){
            move(inputCheck(motion));
        }
    }

    /**
     * Check Input
     *
     * Input Prefrence:
     * Button - Top Priority, if button is pressed then all other input is not considered
     * Joystick - Second Priority
     * Previous Button Press - Third Priority
     * Nothing - Causes Robot to stop motion
     */
    public DriveMotion inputCheck(DriveMotion current_motion){
        //assume that gamepad presses take priority if both the gamepad is pressed and the joystick is moved
        //assuming you cant turn during movement using dpad
        if(gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up) {
            dPadMovementTime += 1000;
            wasDPadPressed = true;

            //Following doesnt seem efficient, revise
            //add rotation via right joystick
            if (gamepad1.dpad_down) {
                current_motion = new DriveMotion(-0.5, 0, 0);
            }
            if (gamepad1.dpad_up) {
                current_motion = new DriveMotion(0.5, 0, 0);
            }
            if (gamepad1.dpad_right) {
                current_motion = new DriveMotion(0, 0.5, 0);
            }
            if (gamepad1.dpad_left) {
                current_motion = new DriveMotion(0, -0.5, 0);
            }

            return current_motion;
        }

        //joystick check
        if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0) {
            wasDPadPressed = false;
            dPadMovementTime = 1000;
            current_motion = new DriveMotion(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x)
            return current_motion;
        }

        //if the current motion is because of a previous dPad press and the one second is not up, continue current motion
        if (isDPadMotion()) {
            return current_motion;
        }

        current_motion = DriveMotion.ZERO;
        return DriveMotion.ZERO;
    }

    /**
     * Checks if current motion is because of previous DPad input
     * @return Whether current motion was caused by previous input
     */
    public boolean isDPadMotion(){
        wasDPadPressed = wasDPadPressed && DPadPressedTime.milliseconds() <= dPadMovementTime;
        if (!wasDPadPressed) {
            dPadMovementTime = 1000;
        }
        return wasDPadPressed;
    }

    /**
     * Used to move the robot based on strafe, forward motion, and turning
     *
     * @param strafeVal Amount of sideways motion
     * @param forwardMotion Amount of forward motion
     * @param turnVal Amount of turning (figure out how to add angle or radian or some actual unit to this)
     */
    public void move(double strafeVal, double forwardMotion, double turnVal){
        FrontLeft.setPower(forwardMotion - strafeVal + turnVal);
        FrontRight.setPower(forwardMotion + strafeVal - turnVal);
        BackLeft.setPower(forwardMotion + strafeVal + turnVal);
        BackRight.setPower(forwardMotion - strafeVal - turnVal);
    }

    /**
     * Used to move the robot based on given DSR
     *
     * @param dsr the drive strafe rotation for movement
     */
    public void move(DriveMotion dsr){
        FrontLeft.setPower(dsr.drive - dsr.strafe + dsr.rotate);
        FrontRight.setPower(dsr.drive + dsr.strafe - dsr.rotate);
        BackLeft.setPower(dsr.drive + dsr.strafe + dsr.rotate);
        BackRight.setPower(dsr.drive - dsr.strafe - dsr.rotate);
    }
}