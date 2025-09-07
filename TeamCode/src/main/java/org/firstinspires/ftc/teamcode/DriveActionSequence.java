package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Holder for any button or other basic drive inputs
 */
public class DriveActionSequence {
    private final DriveMotion[] actions;
    private final int timePerAction;
    private final ElapsedTime timer = new ElapsedTime();

    /**
     * Multi-motion DriveActionSequence constructor
     *
     * @param dms           the DriveMotions to be executed by this button
     * @param timePerAction the amount of time for each action to complete in milliseconds
     */
    public DriveActionSequence(DriveMotion[] dms, int timePerAction) {

        this.timePerAction = timePerAction;
        this.actions = dms;
        timer.reset();
    }

    /**
     * Copy-Constructor
     */
    public DriveActionSequence(DriveActionSequence das) {
        this.timePerAction = das.timePerAction;
        this.actions = das.actions;
        timer.reset();
    }

    /**
     * Single Motion DriveActionSequence contructor
     *
     * @param dm   the DriveMotion for this input to execute
     * @param time the amount of time for this DriveMotion to execute
     */
    public DriveActionSequence(DriveMotion dm, int time) {
        this.timePerAction = time;
        this.actions = new DriveMotion[]{dm}; //CHECK ARRAY SPECIFICS
        timer.reset();
    }

    /**
     * Function that should be executed when this button is pressed.
     * It resets the timer of this DAS
     */
    public void init() {
        timer.reset();
    }

    /**
     * Checks and gives the motion that this button should contribute to the bot
     *
     * @return The vector of motion that the bot should implement
     */
    public DriveMotion motion() {
        if (timer.milliseconds() > timePerAction * actions.length) {
            return DriveMotion.ZERO;
        }
        return actions[((int) (timer.milliseconds() / timePerAction)) % actions.length];
    }

    /**
     * whether this DAS's motion is currently active (if motion() is not zero)
     *
     * @return whether motion() is ZERO
     */
    public boolean motionIsActive() {
        return timer.milliseconds() < timePerAction * actions.length;
    }
}