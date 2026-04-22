// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.generated;

import java.util.Optional;


import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/** Add your docs here. */
public class FieldConstants {
    public final static double RedHubX= 11.92;
    public final static double RedHubY= 4.035;
    
    public final static double BlueHubX= 4.631;;
    public final static double BlueHubY= 4.035;

    



    public static final double RED_HUB_X = 12;
    public static final double RED_HUB_Y = 4;

    public static final double BLUE_HUB_X = 4.6;
    public static final double BLUE_HUB_Y = 4;

    // Example values — adjust to real field
    public static final double RED_PASS_LEFT_X = 14;
    public static final double RED_PASS_LEFT_Y = 6.0;

    public static final double RED_PASS_RIGHT_X = 14;
    public static final double RED_PASS_RIGHT_Y = 2.0;

    public static final double BLUE_PASS_LEFT_X = 2;
    public static final double BLUE_PASS_LEFT_Y = 6;

    public static final double BLUE_PASS_RIGHT_X = 2;
    public static final double BLUE_PASS_RIGHT_Y = 2;

    public static double getHubX() {
        var alliance = DriverStation.getAlliance();
        if (alliance.isPresent() && alliance.get() == Alliance.Red) {

            return RED_HUB_X;
        }
        return BLUE_HUB_X;
    }

    public static double getHubY() {
        var alliance = DriverStation.getAlliance();
        if (alliance.isPresent() && alliance.get() == Alliance.Red) {

            return RED_HUB_Y;
        }
        return BLUE_HUB_Y;
    }

    public static Translation2d getTarget(TargetType type) {

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (!alliance.isPresent())
            return new Translation2d(getHubX(), getHubY());

        boolean red = alliance.get() == Alliance.Red;

        switch (type) {

            case PASS_LEFT:
                return red
                        ? new Translation2d(RED_PASS_LEFT_X, RED_PASS_LEFT_Y)
                        : new Translation2d(BLUE_PASS_LEFT_X, BLUE_PASS_LEFT_Y);

            case PASS_RIGHT:
                return red
                        ? new Translation2d(RED_PASS_RIGHT_X, RED_PASS_RIGHT_Y)
                        : new Translation2d(BLUE_PASS_RIGHT_X, BLUE_PASS_RIGHT_Y);

            default:
                return new Translation2d(getHubX(), getHubY());
        }
    }

}
