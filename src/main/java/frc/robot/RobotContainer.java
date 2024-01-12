// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Libraries.ConsoleAuto;
import frc.robot.Subsystems.Drive;

/** Add your docs here. */
public class RobotContainer {
    private final Drive m_drive = new Drive();

    private final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
    private final ConsoleAuto m_consoleAuto = new ConsoleAuto(OperatorConstants.kAUTONOMOUS_CONSOLE_PORT);
    // private final AutonomousSubsystem m_autonomous = new
    // AutonomousSubsystem(m_consoleAuto,m_robotDrive);

    public RobotContainer() {

m_drive.setDefaultCommand(
        Commands.run(
            () -> m_drive.drive(
                -m_driverController.getLeftX(),
                -m_driverController.getLeftY(),
                -m_driverController.getRightX(), false,
                true), m_drive));

    }
    private void configureBindings() {

    }
}
