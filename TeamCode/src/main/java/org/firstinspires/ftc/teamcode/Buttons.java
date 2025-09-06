package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Buttons {
    Gamepad gp;
    boolean x;
    boolean y;
    boolean b;
    boolean a;
    boolean dpaddown;
    boolean dpadup;
    boolean dpadright;
    boolean dpadleft;

    public Buttons (Gamepad gamepad){
        gp = gamepad;
        update();
    }

    public void update(){
        y = gp.y;
        x = gp.x;
        a = gp.a;
        b = gp.b;
        dpaddown = gp.dpad_down;
        dpadup = gp.dpad_up;
        dpadleft = gp.dpad_left;
        dpadright = gp.dpad_right;
    }

    public boolean xWasPressed(){
        if(x != gp.x){
            update();
            return true;
        } else {
            return false;
        }
    }

    public boolean yWasPressed(){
        if(y != gp.y){
            update();
            return true;
        } else {
            return false;
        }
    }
    public boolean aWasPressed(){
        if(a != gp.a){
            update();
            return true;
        } else {
            return false;
        }
    }
    public boolean bWasPressed(){
        if(b != gp.b){
            update();
            return true;
        } else {
            return false;
        }
    }
    public boolean dpaddownWasPressed(){
        if(dpaddown != gp.dpad_down){
            update();
            return true;
        } else {
            return false;
        }
    }
    public boolean dpadupWasPressed(){
        if(dpadup != gp.dpad_up){
            update();
            return true;
        } else {
            return false;
        }
    }
    public boolean dpadrightWasPressed(){
        if(dpadright != gp.dpad_right){
            update();
            return true;
        } else {
            return false;
        }
    }
    public boolean dpadleftWasPressed(){
        if(dpadleft != gp.dpad_left){
            update();
            return true;
        } else {
            return false;
        }
    }

}
