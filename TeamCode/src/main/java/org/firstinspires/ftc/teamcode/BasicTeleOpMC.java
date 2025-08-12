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
            current_motion = new DriveMotion(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            return current_motion;
        }

        //if the current motion is because of a previous dPad press and the one second is not up, continue current motion
        if (isDPadMotion()) {
            return current_motion;
        }

        current_motion = DriveMotion.ZERO;
        return current_motion;
    }

    /**
     * Checks if current motion is because of previous DPad input
     * @return Whether current motion was caused by previous input
     */
    public boolean isDPadMotion(){
        wasDPadPressed = wasDPadPressed && dPadTimer.milliseconds() <= dPadMovementTime;
        if (!wasDPadPressed) {
            dPadMovementTime = 1000;
        }
        return wasDPadPressed;
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