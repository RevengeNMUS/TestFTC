package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;

/**
 * Holder for any button or other basic drive inputs
 */
public class BasicDrivetrainInput {
    private final DriveMotion[] actions;
    private final int timePerAction;
    private final ElapsedTime timer = new ElapsedTime();
    public int presses = 0;

    /** Basic Contructor for a drivetrain input
     *
     * @param dms the DriveMotions to be executed by this button
     * @param timePerAction the amount of time for each action to complete in milliseconds
     */
    public BasicDrivetrainInput(DriveMotion[] dms, int timePerAction) {

        this.timePerAction = timePerAction;
        this.actions = dms;
        timer.reset();
    }

    /**
     *
     * @param dm the DriveMotion for this input to execute
     * @param time the amount of time for this DriveMotion to execute
     */
    public BasicDrivetrainInput(DriveMotion dm, int time) {
        this.timePerAction = time;
        this.actions = new DriveMotion[]{dm}; //CHECK ARRAY SPECIFICS
        timer.reset();
    }

    /**
     * Function that should be executed when this button is pressed.
     * Allows stacking button inputs.
     * (Possibly phase out return as you can just use motion())
     *
     * @return DriveMotion The drive motion that should be implemented on the robot
     */
    public DriveMotion buttonPressedAction(){
        if (motion() != DriveMotion.ZERO) {
            presses++;
        }
        else
            timer.reset();
        return motion();
    }

    public void reset(){
        presses = 0;
    }
    /**
     * Checks and gives the motion that this button should contribute to the bot
     * @return The vector of motion that the bot should implement
     */
    public DriveMotion motion() {
        if(timer.milliseconds() > timePerAction * 1000 * presses){
            //reset all
            presses = 0;
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
            if(Arrays.equals(((BasicDrivetrainInput) o).actions, (this.actions)) && ((BasicDrivetrainInput) o).timePerAction == this.timePerAction) {
                return true;
            }
        }
        return false;
    }
}