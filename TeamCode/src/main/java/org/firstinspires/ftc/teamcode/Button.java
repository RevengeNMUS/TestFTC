package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Predicate;

public enum Button {
    X(gamepad -> gamepad.x),
    DPADDOWN(gamepad -> gamepad.dpad_down),
    DPADUP(gamepad -> gamepad.dpad_up),
    DPADRIGHT(gamepad -> gamepad.dpad_right),
    DPADLEFT(gamepad -> gamepad.dpad_left),
    RIGHTTRIGGER(gamepad -> gamepad.right_trigger > Constants.TRIGGER_THRESHOLD);


    //think of name later
    final Predicate<Gamepad> something;
    private Button(Predicate<Gamepad> smth){
        something = smth;
    }
}