// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.StrategyManager;
import frc.robot.generated.FieldConstants;
import frc.robot.generated.TargetType;

public class ShotCalculator extends SubsystemBase {
  private double Reqrot;
  private double Error;
  public double rot;
  private double LastReqRot = 0;
  private final double a = 94.09;
  private final double b = 34;
  double FlyWheelSpeed;
  public final double InitFlyWheelVel = 0;
  public double FlyWheelVelOffset = InitFlyWheelVel;
  CommandSwerveDrivetrain cd;
  public final double LoopPeriod = 0.1;
  public final double ShooterDistFromCenter = 0.3429;
  InterpolatingDoubleTreeMap tofmap = new InterpolatingDoubleTreeMap();
  FlyWheel flyWheel;

  /** Creates a new ShotCalculator. */
  public ShotCalculator(CommandSwerveDrivetrain cd, FlyWheel flyWheel) {

    this.flyWheel = flyWheel;
    this.cd = cd;
  }

  @Override
  public void periodic() {
    TargetType type = StrategyManager.getTargetType(cd.getXPose(), cd.getYPose());

    Translation2d target = FieldConstants.getTarget(type);
    Pose2d estimatPose2d = cd.GetPose();
    ChassisSpeeds robotChassisSpeeds = cd.getState().Speeds;
    ChassisSpeeds fieldSpeeds = ChassisSpeeds.fromRobotRelativeSpeeds(robotChassisSpeeds, estimatPose2d.getRotation());

    Translation2d shooterOffset = new Translation2d(0, 0)
        .rotateBy(estimatPose2d.getRotation());

    Pose2d ShooterPose = new Pose2d(
        estimatPose2d.getTranslation().plus(shooterOffset),
        estimatPose2d.getRotation());
    double shooterDistoTarget = flyWheel.getDistance(ShooterPose.getX(), ShooterPose.getY());
    double rx = shooterOffset.getX();
    double ry = shooterOffset.getY();

    double ShooterVelX = fieldSpeeds.vxMetersPerSecond - fieldSpeeds.omegaRadiansPerSecond * ry;
    double ShooterVelY = fieldSpeeds.vyMetersPerSecond + fieldSpeeds.omegaRadiansPerSecond * rx;
    double lookAheadDist = shooterDistoTarget;
    for (int i = 0; i < 15; i++) {
      double OffsetX = ShooterVelX * tofmap(lookAheadDist);
      double OffsetY = ShooterVelY * tofmap(lookAheadDist);
      Translation2d lookaheadTranslation2d = ShooterPose.getTranslation().plus(new Translation2d(OffsetX, OffsetY));
      double newDist = flyWheel.getDistance(lookaheadTranslation2d.getX(), lookaheadTranslation2d.getY());
      if (Math.abs(newDist - lookAheadDist) < 0.01) {
        lookAheadDist = newDist;
        break;
      }
      lookAheadDist = newDist;
    }
    double TOFFinal = tofmap(lookAheadDist);
    Translation2d compenTranslation2d = ShooterPose.getTranslation()
        .plus(new Translation2d(ShooterVelX * TOFFinal, ShooterVelY * TOFFinal));
    FlyWheelSpeed = flyWheel
        .getTargetRPMFromDistance(flyWheel.getDistance(compenTranslation2d.getX(), compenTranslation2d.getY()));

    Reqrot = Math.toDegrees(Math.atan2(compenTranslation2d.getY() - target.getY(),
        compenTranslation2d.getX() - target.getX()));

    Error = Reqrot - cd.GetHeading();
    Reqrot = (Reqrot + 360) % 360;
     Error = (Error + 180) % 360 - 180;

    // if (Reqrot > 0) {
    //   Error = (Error + 180) % 360 - 180;
    // } else {
    //   Error = (Error - 180) % 360 + 180;
    // }
    if (Math.abs(Error) > 2) {
      rot = (0.05 * Error) + (0.001 * ((Error - LastReqRot) / 0.02));
      LastReqRot = Error;
      rot = Math.max(-0.5, Math.min(rot, 0.5));
    } else {
      rot = 0;
    }
    SmartDashboard.putNumber("FLywheel compensated speed",FlyWheelSpeed);
  SmartDashboard.putNumber("Required rotsation for move", Reqrot);
SmartDashboard.putString("Compensated pose",compenTranslation2d.toString());
    // (compenTranslation2d.getDistance(
    // new Translation2d(cd.HubX, cd.HubY)));

    // This method will be called once per scheduler run
  }

  public double GetFlywheelSpeed() {
    return FlyWheelSpeed;
  }

  public double tofmap(double dist) {
    return 1;
  }

  public double getHeadingPow() {
    return rot;
  }

}