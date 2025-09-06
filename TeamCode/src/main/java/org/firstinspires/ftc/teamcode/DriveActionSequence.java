package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;

/**
 * Holder for any button or other basic drive inputs
 */
public class DriveActionSequence {
    private final DriveMotion[] actions;
    private final int timePerAction;
    private final ElapsedTime timer = new ElapsedTime();

    /** Basic Contructor for a drivetrain input
     *
     * @param dms the DriveMotions to be executed by this button
     * @param timePerAction the amount of time for each action to complete in milliseconds
     */
    public DriveActionSequence(DriveMotion[] dms, int timePerAction) {

        this.timePerAction = timePerAction;
        this.actions = dms;
        timer.reset();
    }

    public DriveActionSequence(DriveActionSequence das){
        this.timePerAction = das.timePerAction;
        this.actions = das.actions;
        timer.reset();
    }

    /**
     *
     * @param dm the DriveMotion for this input to execute
     * @param time the amount of time for this DriveMotion to execute
     */
    public DriveActionSequence(DriveMotion dm, int time) {
        this.timePerAction = time;
        this.actions = new DriveMotion[]{dm}; //CHECK ARRAY SPECIFICS
        timer.reset();
    }

    /**
     * Function that should be executed when this button is pressed.
     * (Possibly phase out return as you can just use motion())
     *
     * @return DriveMotion The drive motion that should be implemented on the robot
     */
    public void init(){
        timer.reset();
    }

    public void reset(){
        timer.reset();
    }

    /**
     * Checks and gives the motion that this button should contribute to the bot
     * @return The vector of motion that the bot should implement
     */
    public DriveMotion motion() {
        if(timer.milliseconds() > timePerAction * actions.length){
            return DriveMotion.ZERO;
        }
        return actions[((int) (timer.milliseconds()/timePerAction)) % actions.length];
    }

    public boolean motionIsActive(){
        return !this.motion().equals(DriveMotion.ZERO);
    }

    /**
     * Equals method
     * @param o object to be compared
     * @return is equal?
     */
    @Override
    public boolean equals(Object o){
        if(o != null && o.getClass() == this.getClass()){
            if(Arrays.equals(((DriveActionSequence) o).actions, (this.actions)) && ((DriveActionSequence) o).timePerAction == this.timePerAction) {
                return true;
            }
        }
        return false;
    }
}