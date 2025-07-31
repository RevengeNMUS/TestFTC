package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
//e
@TeleOp(name = "gaming", group = "gamings")
public class MainTeleOp extends OpMode {
    float yPower = 0;
    float xPower = 0;

    @Override
    public void init(){
        xPower = gamepad1.left_stick_x;
        yPower = gamepad1.left_stick_y;

    }

    public void move(float xP, float yP){
        hardwareMap.get(DcMotor.class, "front_left_drive");
        hardwareMap.get(DcMotor.class, "front_right_drive");
        hardwareMap.get(DcMotor.class, "back_right_drive");
        hardwareMap.get(DcMotor.class, "back_left_drive");
    }

    @Override
    public void loop(){

    }
}
