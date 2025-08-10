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
 * Add DPad layering
 * General Polishing
 * More efficient DPad checking?
 * More methods to make more readable and nicer
 *
 */
@TeleOp(name = "BasicMotionController", group = "idk")
public class BasicTeleOpMC extends LinearOpMode {

    /**
     * Vector-like class that houses not only x and y but also rotation
     */
    private class DSR{
        public double drive;
        public double strafe;
        public double rotate;

        public static final DSR EMPTY_DSR = new DSR(0, 0, 0)
        public DSR(double d, double s, double r) {
            drive = d;
            strafe = s;
            rotate = r;
        }

        public setDSR(double d, double s, double r){
            drive = d;
            strafe = s;
            rotate = r;
        }
    }

    //Used to check if 1 second of DPad movement is up
    ElapsedTime DPadPressedTime = new ElapsedTime();

    //Used to check if the current movement is beceause or not because of DPad movement
    boolean wasDPadPressed = false;
    //

    //current DSR
    DSR drive_strafe_rotate = new DSR(0, 0, 0);

    DcMotor FrontLeft;
    DcMotor FrontRight;
    DcMotor BackLeft;
    DcMotor BackRight;

    @Override
    public void runOpMode(){
        FrontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        FrontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        BackLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        BackRight = hardwareMap.get(DcMotor.class, "back_right_drive");

        waitForStart();

        while(opModeIsActive()){
            move(inputCheck(drive_strafe_rotate));
        }
    }

    /**
     * Check Input
     * (ADD ROTATION YOU DOUGHNUT)
     *
     * Input Prefrence:
     * Button - Top Priority, if button is pressed then all other input is not considered
     * Joystick - Second Priority
     * Previous Button Press - Third Priority
     * Nothing - Causes Robot to stop motion
     */
    public DSR inputCheck(DSR current_dsr){
        //assume that gamepad presses take priority if both the gamepad is pressed and the joystick is moved
        if(gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up) {
            dPadPressed();
            //Doesnt seem efficient
            //add rotation via right joystick
            if(gamepad1.dpad_down) {
                current_dsr.setDSR(-0.5, 0, 0)
                return new DSR(-0.5, 0, 0);
            }
            if(gamepad1.dpad_up) {
                current_dsr.setDSR(0.5, 0, 0)
                return new DSR(0.5, 0, 0);
            }
            if(gamepad1.dpad_right) {
                current_dsr.setDSR(0, 0.5, 0)
                return new DSR(0, 0.5, 0);
            }
            if(gamepad1.dpad_left) {
                current_dsr.setDSR(0, -0.5, 0)
                return new DSR(0, -0.5, 0);
            }

        }

        //joystick check
        if(gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0){
            nonDPad();
            return new DSR(gamepad1.right_stick_y, gamepad1.left_stick_y, 0)
        }

        //if the current motion is because of a previous dPad press and the one second is not up, continue current motion
        if(isDPadMotion()) {
            return current_dsr;
        }

        current_dsr.setDSR(0, 0, 0);
        return DSR.EMPTY_DSR;
    }

    /**
     * DPad Input (Resets a one second timer, sets wasDPadPressed to true)
     * Activates when DPad is pressed (add the ability to layer DPad entries)
     */
    public void dPadPressed(){
        DPadPressedTime.reset();
        wasDPadPressed = true;
    }

    /**
     * Non-DPad input
     */
    public void nonDPad(){
        wasDPadPressed = false;
    }

    /**
     * Checks if current motion is because of previous DPad input
     * @return whether current motion was caused by previous input
     */
    public boolean isDPadMotion(){
        wasDPadPressed = wasDPadPressed && DPadPressedTime.milliseconds() <= 1000;
        return wasDPadPressed;
    }

    /**
     * Used to move the robot based on strafe, forward motion, and turning
     *
     * @param strafeVal Amount of sideways motion
     * @param forwardMotion Amount of forward motion
     * @param turnVal Amount of turning (figure out how to add angle or radian or some actual unit to this)
     */
    public void move(float strafeVal, float forwardMotion, float turnVal){
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
    public void move(DSR dsr){
        FrontLeft.setPower(dsr.drive - dsr.strafe + dsr.rotate);
        FrontRight.setPower(dsr.drive + dsr.strafe - dsr.rotate);
        BackLeft.setPower(dsr.drive + dsr.strafe + dsr.rotate);
        BackRight.setPower(dsr.drive - dsr.strafe - dsr.rotate);
    }
}