package org.usfirst.frc.team2345.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;

import com.ctre.CANTalon;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	// For example to map the left and right motors, you could define the
	// following variables to use with your drivetrain subsystem.
	// public static int leftMotor = 1;
	// public static int rightMotor = 2;

	// If you are using multiple modules, make sure to define both the port
	// number and the module. For example you with a rangefinder:
	// public static int rangefinderPort = 1;
	// public static int rangefinderModule = 1;
	public static CANTalon backRightDrive = new CANTalon(3);
	public static CANTalon frontRightDrive = new CANTalon(1);
	public static CANTalon frontLeftDrive = new CANTalon(0);
	public static CANTalon backLeftDrive = new CANTalon(2);
	
	public static VictorSP frontLeftTurn = new VictorSP(0);
	public static VictorSP backLeftTurn = new VictorSP(1);
	public static VictorSP frontRightTurn = new VictorSP(2);
	public static VictorSP backRightTurn = new VictorSP(3);
    
	public static Encoder frontLeftEnc = new Encoder(0, 1);
    public static Encoder frontRightEnc = new Encoder(2, 3);
    public static Encoder backLeftEnc = new Encoder(4, 5);
    public static Encoder backRightEnc = new Encoder(6, 7);
}
