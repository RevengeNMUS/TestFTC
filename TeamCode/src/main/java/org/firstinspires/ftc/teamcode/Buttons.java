package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Buttons {
    Gamepad masterGP;
    Gamepad currentGP;
    Gamepad previousGP;

    public Buttons(Gamepad gamepad) {
        masterGP = gamepad;
        currentGP = new Gamepad();
        previousGP = new Gamepad();
        update();
    }

    public void update() {
        previousGP.copy(currentGP);
        currentGP.copy(masterGP);
    }

    public boolean xWasPressed() {
        return currentGP.x && !previousGP.x;
    }

    public boolean dpaddownWasPressed() {
        return currentGP.dpad_down && !previousGP.dpad_down;
    }

    public boolean dpadupWasPressed() {
        return currentGP.dpad_up && !previousGP.dpad_up;
    }

    public boolean dpadrightWasPressed() {
        return currentGP.dpad_right && !previousGP.dpad_right;
    }

    public boolean dpadleftWasPressed() {
        return currentGP.dpad_left && !previousGP.dpad_left;
    }
}