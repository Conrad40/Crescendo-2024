// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Libraries;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.SparkPIDController;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.RelativeEncoder;

import frc.robot.Constants.SwerveConstants;
/** Add your docs here. */
public class SwerveCorner {private final CANSparkMax m_drivingSparkMax;
    private final CANSparkMax m_turningSparkMax;
  
    private final RelativeEncoder m_drivingEncoder;
    private final AbsoluteEncoder m_turningEncoder;
  
    private final SparkPIDController m_drivingPIDController;
    private final SparkPIDController m_turningPIDController;
  
    private double m_chassisAngularOffset = 0;
    private SwerveModuleState m_desiredState = new SwerveModuleState(0.0, new Rotation2d());
  
    /**
     * Constructs a MAXSwerveModule and configures the driving and turning motor,
     * encoder, and PID controller. This configuration is specific to the REV
     * MAXSwerve Module built with NEOs, SPARKS MAX, and a Through Bore
     * Encoder.
     */
    public SwerveCorner(int drivingCANId, int turningCANId, double chassisAngularOffset) {
      m_drivingSparkMax = new CANSparkMax(drivingCANId, MotorType.kBrushless);
      m_turningSparkMax = new CANSparkMax(turningCANId, MotorType.kBrushless);
  
      // Factory reset, so we get the SPARKS MAX to a known state before configuring
      // them. This is useful in case a SPARK MAX is swapped out.
      m_drivingSparkMax.restoreFactoryDefaults();
      m_turningSparkMax.restoreFactoryDefaults();
      m_drivingSparkMax.setOpenLoopRampRate(1);
      m_turningSparkMax.setOpenLoopRampRate(1);
      m_drivingSparkMax.setSmartCurrentLimit(20);
      m_turningSparkMax.setSmartCurrentLimit(15);
      // Setup encoders and PID controllers for the driving and turning SPARKS MAX.
      m_drivingEncoder = m_drivingSparkMax.getEncoder();
      m_turningEncoder = m_turningSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
      m_drivingPIDController = m_drivingSparkMax.getPIDController();
      m_turningPIDController = m_turningSparkMax.getPIDController();
      m_drivingPIDController.setFeedbackDevice(m_drivingEncoder);
      m_turningPIDController.setFeedbackDevice(m_turningEncoder);
  
      // Apply position and velocity conversion factors for the driving encoder. The
      // native units for position and velocity are rotations and RPM, respectively,
      // but we want meters and meters per second to use with WPILib's swerve APIs.
      m_drivingEncoder.setPositionConversionFactor(SwerveConstants.kDRIVING_ENCODER_POSITION_FACTOR);
      m_drivingEncoder.setVelocityConversionFactor(SwerveConstants.kDRIVING_ENCODER_VELOCITY_FACTOR);
  
      // Apply position and velocity conversion factors for the turning encoder. We
      // want these in radians and radians per second to use with WPILib's swerve
      // APIs.
      m_turningEncoder.setPositionConversionFactor(SwerveConstants.kTURING_ENCODER_POSTION_FACTOR);
      m_turningEncoder.setVelocityConversionFactor(SwerveConstants.kTURNING_ENCODER_VELOCITY_FACTOR);
  
      // Invert the turning encoder, since the output shaft rotates in the opposite direction of
      // the steering motor in the MAXSwerve Module.
      m_turningEncoder.setInverted(SwerveConstants.kTURNING_ENCODER_INVERTED);
  
      // Enable PID wrap around for the turning motor. This will allow the PID
      // controller to go through 0 to get to the setpoint i.e. going from 350 degrees
      // to 10 degrees will go through 0 rather than the other direction which is a
      // longer route.
      m_turningPIDController.setPositionPIDWrappingEnabled(true);
      m_turningPIDController.setPositionPIDWrappingMinInput(SwerveConstants.kTURNING_ENCODER_POSITION_PID_MIN_INPUT);
      m_turningPIDController.setPositionPIDWrappingMaxInput(SwerveConstants.kTURNING_ENCODER_POSITION_PID_MAX_INPUT);
  
      // Set the PID gains for the driving motor. Note these are example gains, and you
      // may need to tune them for your own robot!
      m_drivingPIDController.setP(SwerveConstants.kDRIVING_P);
      m_drivingPIDController.setI(SwerveConstants.kDRIVING_I);
      m_drivingPIDController.setD(SwerveConstants.kDRIVING_D);
      m_drivingPIDController.setFF(SwerveConstants.kDRIVING_FF);
      m_drivingPIDController.setOutputRange(SwerveConstants.kDRIVING_MIN_OUTPUT,
          SwerveConstants.kDRVIVING_MAX_OUTPUT);
  
      // Set the PID gains for the turning motor. Note these are example gains, and you
      // may need to tune them for your own robot!
      m_turningPIDController.setP(SwerveConstants.kTURNING_P);
      m_turningPIDController.setI(SwerveConstants.kTURNING_I);
      m_turningPIDController.setD(SwerveConstants.kTURNING_D);
      m_turningPIDController.setFF(SwerveConstants.kTURNING_FF);
      m_turningPIDController.setOutputRange(SwerveConstants.kTURNING_MIN_OUTPUT,
          SwerveConstants.kTURNING_MAX_OUTPUT);
  
      m_drivingSparkMax.setIdleMode(SwerveConstants.kDRIVING_MOTOR_IDLE_MODE);
      m_turningSparkMax.setIdleMode(SwerveConstants.kTURNING_MOTOR_IDLE_MODE);
      m_drivingSparkMax.setSmartCurrentLimit(SwerveConstants.kDRIVING_MOTOR_CURRENT_LIMIT);
      m_turningSparkMax.setSmartCurrentLimit(SwerveConstants.kTURNING_MOTOR_CURRENT_LIMIT);
  
      // Save the SPARK MAX configurations. If a SPARK MAX browns out during
      // operation, it will maintain the above configurations.
      m_drivingSparkMax.burnFlash();
      m_turningSparkMax.burnFlash();
  
      m_chassisAngularOffset = chassisAngularOffset +90;
      m_desiredState.angle = new Rotation2d(m_turningEncoder.getPosition());
      m_drivingEncoder.setPosition(0);
    }
  
    /**
     * Returns the current state of the module.
     *
     * @return The current state of the module.
     */
    public SwerveModuleState getState() {
      // Apply chassis angular offset to the encoder position to get the position
      // relative to the chassis.
      return new SwerveModuleState(m_drivingEncoder.getVelocity(),
          new Rotation2d(m_turningEncoder.getPosition() - m_chassisAngularOffset));
    }
  
    /**
     * Returns the current position of the module.
     *
     * @return The current position of the module.
     */
    public SwerveModulePosition getPosition() {
      // Apply chassis angular offset to the encoder position to get the position
      // relative to the chassis.
      return new SwerveModulePosition(
          m_drivingEncoder.getPosition(),
          new Rotation2d(m_turningEncoder.getPosition() - m_chassisAngularOffset));
    }
  
    /**
     * Sets the desired state for the module.
     *
     * @param desiredState Desired state with speed and angle.
     */
    public void setDesiredState(SwerveModuleState desiredState) {
      // Apply chassis angular offset to the desired state.
      SwerveModuleState correctedDesiredState = new SwerveModuleState();
      correctedDesiredState.speedMetersPerSecond = desiredState.speedMetersPerSecond;
      correctedDesiredState.angle = desiredState.angle.plus(Rotation2d.fromRadians(m_chassisAngularOffset));
  
      // Optimize the reference state to avoid spinning further than 90 degrees.
      SwerveModuleState optimizedDesiredState = SwerveModuleState.optimize(correctedDesiredState,
          new Rotation2d(m_turningEncoder.getPosition()));
  
      // Command driving and turning SPARKS MAX towards their respective setpoints.
      m_drivingPIDController.setReference(optimizedDesiredState.speedMetersPerSecond, CANSparkMax.ControlType.kVelocity);
      m_turningPIDController.setReference(optimizedDesiredState.angle.getRadians(), CANSparkMax.ControlType.kPosition);
  
      m_desiredState = desiredState;
    }
  
    /** Zeroes all the SwerveModule encoders. */
    public void resetEncoders() {
      m_drivingEncoder.setPosition(0);
    }}