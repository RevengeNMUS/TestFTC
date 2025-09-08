package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Buttons {
    Gamepad gp_master;
    Gamepad gp_current;
    Gamepad gp_copy;

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
        return !gp_copy.x && gp_current.x;
    }

    public boolean dpaddownWasPressed(){
        return !gp_copy.dpad_down && gp_current.dpad_down;
    }
    public boolean dpadupWasPressed(){
        return !gp_copy.dpad_up && gp_current.dpad_up;
    }
    public boolean dpadrightWasPressed(){
        return !gp_copy.dpad_right && gp_current.dpad_right;
    }
    public boolean dpadleftWasPressed(){
        return !gp_copy.dpad_left && gp_current.dpad_left;
    }

}
