package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadButtonInputs {
    Gamepad masterGP;
    Gamepad currentGP;
    Gamepad previousGP;

    public GamepadButtonInputs(Gamepad gamepad) {
        masterGP = gamepad;
        currentGP = new Gamepad();
        previousGP = new Gamepad();
        update();
        update();
        update();
    }

    public void update() {
        previousGP.copy(currentGP);
        currentGP.copy(masterGP);
    }

    public boolean wasReleased(Button button){
        return button.something.test(previousGP) && !button.something.test(currentGP);
    }

    public boolean wasHeld(Button button){
        return button.something.test(previousGP) && button.something.test(currentGP);
    }

    public boolean wasPressed(Button button){
        return !button.something.test(previousGP) && button.something.test(currentGP);
    }
}