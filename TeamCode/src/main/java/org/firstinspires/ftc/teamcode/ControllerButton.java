package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;

/**
 * Holder for any button inputs
 */
public class ControllerButton {
    public final String name;
    public final DriveMotion[] actions;
    public final int timeForAction;
    public ElapsedTime timer = new ElapsedTime();
    public int compounded = 1;

    public ControllerButton(DriveMotion[] dms, int time, String name) {
        this.name = name;
        this.timeForAction = time;
        this.actions = dms;
        timer.reset();
    }
    public ControllerButton(DriveMotion dm, int time, String name) {
        this.name = name;
        this.timeForAction = time;
        this.actions = new DriveMotion[]{dm}; //CHECK ARRAY SPECIFICS
        timer.reset();
    }

    /**
     * Function that should be executed when this button is pressed.
     * Allows stacking button inputs.
     * (Possibly phase out return as you can just use motion())
     *
     * @param pastButton the previous button pressed (in order to stack inputs)
     * @return DriveMotion The drive motion that should be implemented on the robot
     */
    public DriveMotion buttonPressedAction(ControllerButton pastButton){
        if (pastButton != null && pastButton.equals(this) && pastButton.motion() != DriveMotion.ZERO) {
            timer = pastButton.timer;
            compounded += 1;
        }
        else
            timer.reset();
        return motion();
    }

    /**
     * Checks and gives the motion that this button should contribute to the bot
     * @return The vector of motion that the bot should implement
     */
    public DriveMotion motion() {
        if(timer.milliseconds() > 1000 * compounded){
            compounded = 1;
            return DriveMotion.ZERO;
        }
        return actions[((int) (timer.milliseconds()/1000)) % actions.length];
    }

    /**
     * Equals method
     * @param o object to be compared
     * @return is equal?
     */
    @Override
    public boolean equals(Object o){
        if(o != null && o.getClass() == this.getClass()){
            if(((ControllerButton) o).name.equals(this.name) && Arrays.equals(((ControllerButton) o).actions, (this.actions)) && ((ControllerButton) o).timeForAction == this.timeForAction) {
                return true;
            }
        }
        return false;
    }
}