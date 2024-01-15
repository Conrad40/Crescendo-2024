package frc.robot.Subsystems;

import java.util.List;
import java.util.Map;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Commands.WaitForCount;
import frc.robot.Libraries.AutonomousCommandSelector;
import frc.robot.Libraries.AutonomousCommands;
import frc.robot.Libraries.AutonomousSteps;
import frc.robot.Libraries.ConsoleAuto;
import frc.robot.Libraries.StepState;

//There is a 95% chance that it will crash if you try to run auto so, like dont
public class AutonomousSubsystem {
        private enum Paths {
                BASIC(0, 0, 2.5, 0),
                BEND(0, 0, 1, .5, 2, 0);

                private final double m_dStartX;
                private final double m_DStartY;
                private final double m_DMidX;
                private final double m_dMidY;
                private final double m_dEndX;
                private final double m_dEndY;

                private Paths(double dStartX, double dStartY, double dEndX, double dEndY) {
                        this.m_dStartX = dStartX;
                        this.m_DStartY = dStartY;
                        this.m_DMidX = (dStartX + dEndX) / 2;
                        this.m_dMidY = (dStartY + dEndY) / 2;
                        this.m_dEndX = dEndX;
                        this.m_dEndY = dEndY;
                }

                private Paths(double dStartX, double dStartY, double dMidX, double dMidY, double dEndX, double dEndY) {
                        this.m_dStartX = dStartX;
                        this.m_DStartY = dStartY;
                        this.m_DMidX = dMidX;
                        this.m_dMidY = dMidY;
                        this.m_dEndX = dEndX;
                        this.m_dEndY = dEndY;
                }

                double getStartX() {
                        return m_dStartX;
                }

                double getStartY() {
                        return m_DStartY;
                }

                double getMidX() {
                        return m_DMidX;
                }

                double getMidY() {
                        return m_dMidY;
                }

                double getEndX() {
                        return m_dEndX;
                }

                double getEndY() {
                        return m_dEndY;
                }
        }

        private Drive m_drive;

        AutonomousCommandSelector<AutonomousSteps> m_autoCommand;
        private String kAUTO_TAB = "Autonomous";
        private String kSTATUS_PEND = "PEND";
        private String kSTATUS_ACTIVE = "ACTV";
        private String kSTATUS_DONE = "DONE";
        private String kSTATUS_SKIP = "SKIP";
        private String kSTATUS_NULL = "NULL";

        private int kSTEPS = 5;
        private boolean kRESET_ODOMETRY = true;

        ConsoleAuto m_ConsoleAuto;
        AutonomousCommands m_autoSelectCommand[] = AutonomousCommands.values();
        AutonomousCommands m_selectedCommand;

        private String m_strCommand = " ";
        private String[] m_strStepList = { "", "", "", "", "" };
        private boolean[] m_bStepSWList = { false, false, false, false, false };
        private String[] m_strStepStatusList = { "", "", "", "", "" };

        private ShuffleboardTab m_tab = Shuffleboard.getTab(kAUTO_TAB);

        private GenericEntry m_autoCmd = m_tab.add("Selected Pattern", "")
                        .withPosition(0, 0)
                        .withSize(2, 1)
                        .getEntry();

        private GenericEntry m_iWaitLoop = m_tab.add("WaitLoop", 0)
                        .withWidget(BuiltInWidgets.kDial)
                        .withPosition(0, 1)
                        .withSize(2, 2)
                        .withProperties(Map.of("min", 0, "max", 5))
                        .getEntry();

        private GenericEntry m_allianceColor = m_tab.add("Alliance", true)
                        .withWidget(BuiltInWidgets.kBooleanBox)
                        .withProperties(Map.of("colorWhenTrue", "Red", "colorWhenFalse", "Blue"))
                        .withPosition(0, 3)
                        .withSize(1, 1)
                        .getEntry();

        private GenericEntry m_step[] = { m_tab.add("Step0", m_strStepList[0])
                        .withWidget(BuiltInWidgets.kTextView)
                        .withPosition(2, 0)
                        .withSize(1, 1)
                        .getEntry(),
                        m_tab.add("Step1", m_strStepList[1])
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .withPosition(3, 0)
                                        .withSize(1, 1)
                                        .getEntry(),
                        m_tab.add("Step2", m_strStepList[2])
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .withPosition(4, 0)
                                        .withSize(1, 1)
                                        .getEntry(),
                        m_tab.add("Step3", m_strStepList[3])
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .withPosition(5, 0)
                                        .withSize(1, 1)
                                        .getEntry(),
                        m_tab.add("Step4", m_strStepList[4])
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .withPosition(6, 0)
                                        .withSize(1, 1)
                                        .getEntry()
        };

        public AutonomousSubsystem(GenericEntry[] m_step) {
                this.m_step = m_step;
        }

        private GenericEntry m_sw[] = { m_tab.add("Step0Sw", m_bStepSWList[0])
                        .withPosition(2, 1)
                        .withSize(1, 1)
                        .withWidget(BuiltInWidgets.kBooleanBox)
                        .getEntry(),
                        m_tab.add("Step1Sw", m_bStepSWList[1])
                                        .withPosition(3, 1)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kBooleanBox)
                                        .getEntry(),
                        m_tab.add("Step2Sw", m_bStepSWList[2])
                                        .withPosition(4, 1)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kBooleanBox)
                                        .getEntry(),
                        m_tab.add("Step3Sw", m_bStepSWList[3])
                                        .withPosition(5, 1)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kBooleanBox)
                                        .getEntry(),
                        m_tab.add("Step4Sw", m_bStepSWList[4])
                                        .withPosition(6, 1)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kBooleanBox)
                                        .getEntry()
        };

        private GenericEntry m_st[] = { m_tab.add("Stat0", m_strStepStatusList[0])
                        .withPosition(2, 2)
                        .withSize(1, 1)
                        .withWidget(BuiltInWidgets.kTextView)
                        .getEntry(),
                        m_tab.add("Stat1", m_strStepStatusList[1])
                                        .withPosition(3, 2)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry(),
                        m_tab.add("Stat2", m_strStepStatusList[2])
                                        .withPosition(4, 2)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry(),
                        m_tab.add("Stat3", m_strStepStatusList[3])
                                        .withPosition(5, 2)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry(),
                        m_tab.add("Stat4", m_strStepStatusList[4])
                                        .withPosition(6, 2)
                                        .withSize(1, 1)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry()
        };

        private int m_iPatternSelect;

        private Command m_currentCommand;
        private boolean m_bIsCommandDone = false;
        private int m_stepIndex;
        private int m_iWaitCount;
        private Trajectory m_driveTrajectory;
        private WaitCommand m_wait1;
        private StepState m_stepWait1Sw1;
        private WaitCommand m_wait2;
        private StepState m_stepWait2Sw1;
        private StepState m_stepWait2Sw2;
        private StepState m_stepWait2SwAB;
        private WaitForCount m_waitForCount;
        private StepState m_stepWaitForCount;
        private Command m_placeConeM;
        private StepState m_stepPlaceConeM;

        // find a replacement for ramseteDrive
        // private RamseteDrivePath m_drive3Path;
        private StepState m_stepDrivePath;
        private StepState m_stepMoveArm;
        private Command m_moveArm;
        private Command m_balance;
        private StepState m_stepBalance;

        private Command m_turnPath;
        private StepState m_stepturnPath;
        // private String m_path1JSON = "paths/Path1.wpilib.json";
        // private Trajectory m_trajPath1;

        private AutonomousSteps m_currentStepName;
        private StepState[][] m_cmdSteps;

        public AutonomousSubsystem(ConsoleAuto consoleAuto, Drive drive) {

                m_ConsoleAuto = consoleAuto;
                m_drive = drive;

                m_selectedCommand = m_autoSelectCommand[0];
                m_strCommand = m_selectedCommand.toString();
                m_autoCommand = new AutonomousCommandSelector<AutonomousSteps>();
                m_iPatternSelect = -1;

                m_stepWait1Sw1 = new StepState(AutonomousSteps.WAIT1, m_ConsoleAuto.getSwitchSupplier(1));

                m_stepWait2Sw1 = new StepState(AutonomousSteps.WAIT2, m_ConsoleAuto.getSwitchSupplier(1));
                m_stepWait2Sw2 = new StepState(AutonomousSteps.WAIT2, m_ConsoleAuto.getSwitchSupplier(2));

                m_stepWaitForCount = new StepState(AutonomousSteps.WAITLOOP);

                // make it work. How - IDK. I just want it to have a clean build for now.
                // ln 221
                /*
                 * m_drivePath = new RamseteDrivePath(genTrajectory(Paths.BASIC),
                 * kRESET_ODOMETRY, m_drive);
                 * m_autoCommand.addOption(AutonomousSteps.DRIVE3, m_drivePath);
                 * m_stepDrivePath = new StepState(AutonomousSteps.DRIVE3,
                 * m_ConsoleAuto.getSwitchSupplier(ConsoleConstants.kDRIVE_PATTERN_1_SW));
                 */

                /*
                 * m_autoCommand.addOption(AutonomousSteps.TURNPATH, new
                 * RamseteDrivePath(genTrajectory(Paths.BEND), kRESET_ODOMETRY, m_drive));
                 * m_stepturnPath = new
                 * StepState(AutonomousSteps.TURNPATH,m_ConsoleAuto.getSwitchSupplier(3));
                 */

                m_cmdSteps = new StepState[][] {
                                { m_stepWaitForCount, m_stepPlaceConeM, m_stepDrivePath },
                                { m_stepWaitForCount, m_stepPlaceConeM, m_stepBalance },
                                { m_stepWaitForCount, m_stepPlaceConeM, m_stepturnPath }
                                // { m_stepWaitForCount, m_stepMoveArm, m_stepPlaceConeM, m_stepDrive3Path }
                };
        }

        private Trajectory genTrajectory(Paths path) {
                // System.out.println(path.getEndX());
                return TrajectoryGenerator.generateTrajectory(
                                new Pose2d(path.getStartX(), path.getStartY(), new Rotation2d(0)),
                                List.of(new Translation2d(path.getMidX(), path.getMidY())),
                                new Pose2d(path.getEndX(), path.getEndY(), new Rotation2d(0)),
                                m_drive.getTrajConfig());
        }

}
