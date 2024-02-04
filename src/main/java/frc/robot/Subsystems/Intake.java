// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CANIDConstants;

public class Intake extends SubsystemBase {
  /** Creates a new Intake. */

  //one neo to spin intake and one to move intake
  //two limit switchs for in/out pos of intake, likly on the motor controller itself
  //possiblity of limit switch to detect if the intake is full

  private CANSparkMax m_spinMotor;
  private CANSparkMax m_liftMotor;

  
  public Intake() {
    m_spinMotor = new CANSparkMax(CANIDConstants.kINTAKE_SPIN_MOTOR_ID, MotorType.kBrushless);
    m_liftMotor = new CANSparkMax(CANIDConstants.kINTAKE_LIFT_MOTOR_ID, MotorType.kBrushless);

    m_spinMotor.setIdleMode(IdleMode.kCoast);
    m_liftMotor.setIdleMode(IdleMode.kBrake);

    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
