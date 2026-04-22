// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.RobotHealthManager;

public class Intake extends SubsystemBase {
  private final SparkMax leftIntake;
  private final SparkMax rightIntake;

  private boolean isRunning = false;
  private double outputCurrent = 0;
  SparkMaxConfig leftConfig = new SparkMaxConfig();

  private final Alert leftIntakeError = new Alert("Left Intake Motor Fault", AlertType.kError);
  private final Alert rightIntakeError = new Alert("Right Intake Motor Fault", AlertType.kError);
  private final Alert intakeCurrentWarning = new Alert(" High Current Warning", AlertType.kWarning);

  /** Creates a new Intake. */
  public Intake() {
    leftIntake = new SparkMax(14, MotorType.kBrushless);
    rightIntake = new SparkMax(15, MotorType.kBrushless);
    SparkMaxConfig rightConfig = new SparkMaxConfig();

    leftConfig
        .secondaryCurrentLimit(60)
        .idleMode(IdleMode.kCoast);
    rightConfig
        .secondaryCurrentLimit(60)
        .apply(leftConfig)
        .follow(leftIntake, true);
    leftIntake.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    rightIntake.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    outputCurrent = leftIntake.getOutputCurrent();
    SmartDashboard.putBoolean("IntakeStatus", isRunning);
    SmartDashboard.putNumber("Intake_Current ", outputCurrent);
  }

  @Override
  public void periodic() {
    // SmartDashboard.putNumber("Left IntakeCurrent", GetCurrentL());
    // SmartDashboard.putNumber("Right IntakeCurrent", GetCurrentR());
    // This method will be called once per scheduler run

    outputCurrent = leftIntake.getOutputCurrent();

    isRunning = outputCurrent > 5;

    SmartDashboard.putBoolean("IntakeStatus", isRunning);
    SmartDashboard.putNumber("Intake_Current ", outputCurrent);

    if (DriverStation.isDisabled()) {

      boolean leftFault = leftIntake.hasActiveFault();
      boolean rightFault = rightIntake.hasActiveFault();

      String lefString = RobotHealthManager
          .getFaultString(leftIntake.getFaults());

      String rightString = RobotHealthManager
          .getFaultString(rightIntake.getFaults());

      RobotHealthManager.updateLeftIntake(leftFault, false,
          "Left Intake Can Id - " + leftIntake.getDeviceId() + "Faults :- " +
              lefString);
      RobotHealthManager.updateRightIntake(rightFault, false,
          "Right Intake Can Id - " + rightIntake.getDeviceId() + "Faults :- " +
              rightString);
    }
  }

  public void runIntake(double speed) {
    leftIntake.set(speed);
  }

  public void StopIntake() {
    leftIntake.set(0);
  }

  public void runIntakeForHop(double speed) {
    // if (speed>0){
    leftIntake.set(speed);
    // }
  }

  public double GetCurrentL() {
    return leftIntake.getOutputCurrent();
  }

  public double GetCurrentR() {
    return rightIntake.getOutputCurrent();
  }

  public void clearFaults() {
    leftIntake.clearFaults();
    rightIntake.clearFaults();
  }
}
