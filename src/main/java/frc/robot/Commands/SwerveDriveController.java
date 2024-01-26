// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Commands;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import frc.robot.Constants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Subsystems.Drive;

public class SwerveDriveController extends Command {
  /** Creates a new SwerveDriveController. */

  private Drive m_drive;
  private Trajectory m_trajectory;
  private boolean m_resetOdometry;

  private HolonomicDriveController thetaController = new HolonomicDriveController(new PIDController(1.0, 0.0, 0.0),
        new PIDController(1.0, 0.0, 0.0), new ProfiledPIDController(1.0, 0.0, 0.0, null));

  public SwerveDriveController(Trajectory trajectory, boolean resetOdometry, Drive drive) {
    // Use addRequirements() here to declare subsystem dependencies.

    new SwerveControllerCommand(trajectory,
        drive::getPose, // Functional interface to feed supplier
        DriveConstants.kDriveKinematics,
        // Position controllers
        thetaController,
        drive::setModuleStates,
        drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
