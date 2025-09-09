package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Predicate;

public enum Button {

    X(gamepad -> gamepad.x),
    DPAD_DOWN(gamepad -> gamepad.dpad_down),
    DPAD_UP(gamepad -> gamepad.dpad_up),
    DPAD_RIGHT(gamepad -> gamepad.dpad_right),
    DPAD_LEFT(gamepad -> gamepad.dpad_left),
    RIGHT_TRIGGER(gamepad -> gamepad.right_trigger > 0.2);

    //todo: think of name later
    final Predicate<Gamepad> something;
    private Button(Predicate<Gamepad> smth){
        something = smth;
    }
}