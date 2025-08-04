package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Basic Motion Controller
 * Takes Joystick and Buttonpad (idk the technical name of it) input, and moves the robot accordingly
 */
@TeleOp(name = "BasicMotionController", group = "idk")
public class BasicTeleOpMC extends LinearOpMode {

    ElapsedTime DPadPressedTime = new ElapsedTime();
    boolean wasDPadPressed = false;
    float forward = 0;
    float strafe = 0;

    float turn = 0;

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

        }
    }

    /**
     * Check Input
     */
    public void inputCheck(){
        if(gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up)
            dPadPressed();
    }

    /**
     * Activates when DPad is pressed (add the ability to layer DPad entries)
     */
    public void dPadPressed(){
        DPadPressedTime.reset();
        wasDPadPressed = true;
    }

    public void nonDPad(){
        wasDPadPressed = false;
    }

    public boolean isDPadMotion(){
        return (DPadPressedTime.milliseconds() <= 1000 && wasDPadPressed);
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
}
