package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Buttons {
    Gamepad gp_master;
    Gamepad gp_current;
    Gamepad gp_copy;

    public boolean xButtonLastPressed = false;
    public boolean dPadDownLastPressed = false;
    public boolean dPadUpLastPressed = false;
    public boolean dPadRightLastPressed = false;
    public boolean dPadLeftLastPressed = false;

    public void buttonsReset(){
        dPadLeftLastPressed = false;
        xButtonLastPressed = false;
        dPadDownLastPressed = false;
        dPadUpLastPressed = false;
        dPadRightLastPressed = false;
    }

    public Buttons (Gamepad gamepad){
        gp_master = gamepad;
        gp_current = gamepad;
        gp_copy = gamepad;
        update();
    }

    public void update(){
        gp_copy.copy(gp_current);
        gp_current.copy(gp_master);
    }


    public boolean xWasPressed(){

        xButtonLastPressed = true;
        dPadDownLastPressed = false;
        dPadUpLastPressed = false;
        dPadRightLastPressed = false;
        dPadLeftLastPressed = false;

        return !gp_copy.x && gp_current.x;
    }

    public boolean dpaddownWasPressed(){

        xButtonLastPressed = false;
        dPadDownLastPressed = true;
        dPadUpLastPressed = false;
        dPadRightLastPressed = false;
        dPadLeftLastPressed = false;

        return !gp_copy.dpad_down && gp_current.dpad_down;
    }
    public boolean dpadupWasPressed(){

        xButtonLastPressed = false;
        dPadDownLastPressed = false;
        dPadUpLastPressed = true;
        dPadRightLastPressed = false;
        dPadLeftLastPressed = false;

        return !gp_copy.dpad_up && gp_current.dpad_up;
    }
    public boolean dpadrightWasPressed(){

        xButtonLastPressed = false;
        dPadDownLastPressed = false;
        dPadUpLastPressed = false;
        dPadRightLastPressed = true;
        dPadLeftLastPressed = false;

        return !gp_copy.dpad_right && gp_current.dpad_right;
    }
    public boolean dpadleftWasPressed(){

        xButtonLastPressed = false;
        dPadDownLastPressed = false;
        dPadUpLastPressed = false;
        dPadRightLastPressed = false;
        dPadLeftLastPressed = true;

        return !gp_copy.dpad_left && gp_current.dpad_left;
    }

}
