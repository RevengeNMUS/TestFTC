package org.firstinspires.ftc.teamcode;

/**
 * Vector-like class that houses not only x and y but also rotation
 */
public class DriveMotion {
    public final double drive;
    public final double strafe;
    public final double rotate;
    public static final DriveMotion ZERO = new DriveMotion(0, 0, 0);

    /**
     * Use constructor to define vars of this DriveMotion
     * @param d amount of forward movement
     * @param s amount of strafing
     * @param r amount of rotation (correlate this to smth, angles, radians, whatever)
     */
    public DriveMotion (double d, double s, double r) {
        drive = d;
        strafe = s;
        rotate = r;
    }
}