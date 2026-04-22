// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.util.RobotHealthManager;

public class Hopper extends SubsystemBase {
  boolean hasReached = false;
  public boolean at45;
  public double pos;
  public boolean holdenabled = false;
  public final double Kp = 5;
  public double output;
  public final SparkMax hopper;
  SparkClosedLoopController closedLoopController;
  SparkMaxConfig configure = new SparkMaxConfig();
  public RelativeEncoder hoppereEncoder;
  private boolean isRunning = false;
  private double outputCurrent = 0;

  /** Creates a new Intake. */
  public Hopper() {
    hopper = new SparkMax(16, MotorType.kBrushless);
    hoppereEncoder = hopper.getEncoder();
    configure
        .inverted(false)
        .smartCurrentLimit(50)
        .idleMode(IdleMode.kBrake);
    configure.encoder
        .velocityConversionFactor(1)
        .positionConversionFactor(1);
    configure.closedLoop
        .pid(0, 0, 0)
        .velocityFF(0)
        .outputRange(-0.8, 0.8);

    configure.softLimit
        .forwardSoftLimit(50)
        .reverseSoftLimit(5)
        .forwardSoftLimitEnabled(true)
        .reverseSoftLimitEnabled(true);

    hopper.configure(configure, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    outputCurrent = hopper.getOutputCurrent();

    SmartDashboard.putBoolean("HopperStatus", isRunning);
    SmartDashboard.putNumber("Hopper_Current ", outputCurrent);

  }

  @Override
  public void periodic() {
    if (hopper.getForwardLimitSwitch().isPressed()) {
      hoppereEncoder.setPosition(49);
    }
    SmartDashboard.putBoolean("Limit switch pressed", hopper.getForwardLimitSwitch().isPressed());
    if (hopper.getForwardLimitSwitch().isPressed() || pos >= 48) {
      at45 = true;
    }
    if (pos > 29 && pos < 31) {
      at45 = false;
    }
    pos = hoppereEncoder.getPosition();
    SmartDashboard.putBoolean("at45", at45);
    SmartDashboard.putNumber("Hopper position", pos);

    // if ((pos < -0.27 && pos > -0.32) && (joystick.getRightY() < 0.15 &&
    // joystick.getRightY() > -0.15)) {
    // hopper.set(0.3);
    // } else if ((pos < -0.32 || pos > -0.27) && (joystick.getRightY() < 0.15 &&
    // joystick.getRightY() > -0.15)) {
    // hopper.set(0);
    // }

    outputCurrent = hopper.getOutputCurrent();

    isRunning = outputCurrent > 0.2;

    SmartDashboard.putBoolean("HopperStatus", isRunning);
    SmartDashboard.putNumber("Hopper output ", hopper.getAppliedOutput());

    if (DriverStation.isDisabled()) {

      boolean fault = hopper.hasActiveFault();

      String faultString = RobotHealthManager
          .getFaultString(hopper.getFaults());

      RobotHealthManager.updateHopper(fault, false,
          "Hopper Can Id - " + hopper.getDeviceId() + "Faults :- " + faultString);
    }

    // if(pos < -0.265 && pos > -0.275 && joystick.getRightY()<0.1 &&
    // joystick.getRightY() >-0.1){
    // hopper.set(0.25);
    // }
    // else if(pos >= -0.285 && joystick.getRightY()<0.1 && joystick.getRightY()
    // >-0.1){
    // hopper.set(0);
    // }
    // This method will be called once per scheduler run
  }

  public void runHopper(double pow) {

    if (pos > 3 && pow < -0.2) { // 0
      hopper.set(pow);
    } else if (pos < 44 && pow > 0.2) { // 45
      hopper.set(pow);
    } else {
      hopper.set(0);
    }

  }

  public void run(double pow) {
    hopper.set(pow);

    // SmartDashboard.putNumber("Hopper position", pos);
  }

  public void stop() {

    hopper.set(0);

  }

  public void stopandHold() {
    holdenabled = true;
  }

  public void armRelease() {
    holdenabled = false;
  }

  public void clearFaults() {
    hopper.clearFaults();
  }
}
