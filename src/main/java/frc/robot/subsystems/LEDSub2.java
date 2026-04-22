// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Centimeters;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import java.util.Map;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.util.HubLogic;
import frc.robot.util.RobotHealthManager;

public class LEDSub2 extends SubsystemBase {
  public double timeremain;
  private final AddressableLED m_led;
  private final AddressableLEDBuffer m_buffer;
  Distance LEDSpacing = Meters.of(1 / 50);
  // Progress Bar
  LEDPattern base = LEDPattern.gradient(GradientType.kDiscontinuous, Color.kRed, Color.kGreen);
  LEDPattern pattern = LEDPattern.progressMaskLayer(() -> HubLogic.getShiftTimeRemaining() / 25);
  LEDPattern display = base.mask(pattern);
  // Blinking
  LEDPattern base1 = LEDPattern.gradient(GradientType.kDiscontinuous, Color.kRed, Color.kGreen);
  LEDPattern pattern1 = base1.blink(Second.of(0.5));
  // Blinking 2
  LEDPattern base2 = LEDPattern.solid(Color.kRed);
  LEDPattern pattern2;
  // Blinking 3
  LEDPattern green = LEDPattern.solid(Color.kGreen);
  LEDPattern greenBlink = green.blink(Second.of(0.1));
  // Scrolling
  // Map<Double, Color> maskSteps = Map.of(0, Color.kWhite, 0.5, Color.kBlack);
  Map<Double, Color> maskMap = Map.of(0.0, Color.kWhite, 0.5, Color.kBlack);
  LEDPattern base4 = LEDPattern.rainbow(255, 255);
  LEDPattern mask4 = LEDPattern.steps(maskMap).scrollAtRelativeSpeed(Percent.per(Second).of(0.25));
  LEDPattern pattern4 = base4.mask(mask4);
  // Breathing
  LEDPattern base5 = LEDPattern.gradient(GradientType.kDiscontinuous, Color.kRed, Color.kBlue);
  LEDPattern pattern5 = base.breathe(Seconds.of(HubLogic.getShiftTimeRemaining() / 25));
  // Off
  LEDPattern off = LEDPattern.kOff;

  LEDPattern red = LEDPattern.solid(Color.kRed);
  LEDPattern redBlink = red.blink(Second.of(0.1));
  LEDPattern blue = LEDPattern.gradient(GradientType.kDiscontinuous, Color.kBlue, Color.kBlue);

  // solid(Color.kBlue);
  // Mask
  /** Creates a new LedSub. */
  public LEDSub2() {

    m_led = new AddressableLED(0);
    m_buffer = new AddressableLEDBuffer(60);
    m_led.setLength(60);
    m_led.start();
    // setDefaultCommand(runPattern(LEDPattern.solid(Color.kBlack)).withName("Off"));
    // setDefaultCommand(runPattern(display));

  }

  @Override
  public void periodic() {
    // pattern5.applyTo(m_buffer);
    // timeremain = HubLogic.getShiftTimeRemaining();
    // pattern2 = base2.blink(Second.of((timeremain/40)));
    // SmartDashboard.putNumber("T", timeremain);
    // runPattern(pattern5);
    // m_led.setData(m_buffer);
    // This method will be called once per scheduler run

    // m_led.setData(m_buffer);

    // if (HubLogic.getShiftTimeRemaining() <= 5 && HubLogic.getShiftTimeRemaining()
    // != -1) {
    // pattern3.applyTo(m_buffer);
    // } else {
    // base1.applyTo(m_buffer);
    // }

    // m_led.setData(m_buffer);

    boolean isMyHubActive = HubLogic.isMyHubActive();

    double timeLeft = HubLogic.getShiftTimeRemaining();
    if (DriverStation.isDisabled()) {

      Alliance color = DriverStation.getAlliance().orElse(Alliance.Blue);
      boolean hasActiveFault = RobotHealthManager.hasActiveFault();
      if (hasActiveFault) {

        pattern4.applyTo(m_buffer);

      } else if (color == Alliance.Red)
        red.applyTo(m_buffer);
      else
        blue.applyTo(m_buffer);
    } else if (isMyHubActive) {
      if (timeLeft <= 5) {
        // display.applyTo(m_buffer);
        greenBlink.applyTo(m_buffer);
      } else {
        green.applyTo(m_buffer);
      }
    } else {
      if (timeLeft <= 5) {
        redBlink.applyTo(m_buffer);

      } else {
        red.applyTo(m_buffer);
      }
    }

    m_led.setData(m_buffer);
  }

  public Command runPattern(LEDPattern ledPattern) {
    return run(() -> ledPattern.applyTo(m_buffer));
  }
  // public void command(LEDPattern ledPattern){
  // setDefaultCommand(runPattern(ledPattern));
  // }

  // public Command runProgress(){
  // return run(()-> display.applyTo(m_buffer));
  // }

  // public Command runBlink() {
  // return run(() -> pattern1.applyTo(m_buffer));
  // }

  public void setData() {

    m_led.setData(m_buffer);

  }

}
