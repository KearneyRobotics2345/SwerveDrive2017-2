//FRC Team-2345
//Kearney Robotcs -- Animal Control
//Kearney Robotics - Animal Control
//RecycleRush 2015

package org.usfirst.frc.team2345.robot.commands;

import org.usfirst.frc.team2345.robot.OI;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;

import org.usfirst.frc.team2345.robot.RobotMap;
//Commented out code is left for posterities' sake
public class swerveDrive extends Command {
	double uRT;		//Upper Right Turn
    double uLT;		//Upper Left Turn
    double dRT;		//Down Right Turn
    double dLT;		//Down Left Turn
    //leftover code. Delete?
    //double uRTDir;	
    //double uLTDir;
    //double dRTDir;
    //double dLTDir;
    // multiplier ratio of encoder ticks to degrees
    double mult = 1.15278;
    boolean commandStatus = false;

    //enabling all the encoders
    CANTalon upLeftDrive = RobotMap.frontLeftDrive;
    CANTalon upRightDrive = RobotMap.frontRightDrive;
    CANTalon downLeftDrive = RobotMap.backLeftDrive;
    CANTalon downRightDrive = RobotMap.backRightDrive;

    VictorSP upLeftTurn = RobotMap.frontLeftTurn;
    VictorSP upRightTurn = RobotMap.frontRightTurn;
    VictorSP downLeftTurn = RobotMap.backLeftTurn;
    VictorSP downRightTurn = RobotMap.backRightTurn;

    //all joystick code
    Joystick stick = OI.stick;		//Enables joystick 1
    Joystick schtick = OI.schtick;	//Enables joystick 2
    //calls encoders from Robot Map
    Encoder upRightEnc = RobotMap.frontRightEnc;
    Encoder upLeftEnc = RobotMap.frontLeftEnc;
    Encoder downRightEnc = RobotMap.backRightEnc;
    Encoder downLeftEnc = RobotMap.backLeftEnc;
 
    //Math to determine hypotenues of frame
    static double l = 14.375;//19; //(wheelbase, inches)
    
    static double w = 25.75;//32; //(trackwidth, inches)
    double r = Math.sqrt(l * l + w * w);

    public swerveDrive() {
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//if (auto = true) {
        
    	// used to change encoder ticks to degrees
    	double upRightEncoder = (upRightEnc.get() / mult);
    	double upLeftEncoder = (upLeftEnc.get() / mult);
    	double downRightEncoder = (downRightEnc.get() / mult);
    	double downLeftEncoder = (downLeftEnc.get() / mult);
    
    	//fixes the problem of negative ticks on the encoder converting to inverted degrees(-1* translates to 359* opposed to -1* before)
    	double uRE = (upRightEncoder < 0) ? 360 - Math.abs(upRightEncoder % 360) : upRightEncoder % 360;
    	double uLE = (upLeftEncoder < 0) ? 360 - Math.abs(upLeftEncoder % 360) : upLeftEncoder % 360; //upLeftEncoder%= 360;
    	double dLE = (downLeftEncoder < 0) ? 360 - Math.abs(downLeftEncoder % 360) : downLeftEncoder % 360; //downLeftEncoder %= 360;
    	double dRE = (downRightEncoder < 0) ? 360 - Math.abs(downRightEncoder % 360) : downRightEncoder % 360; //downRightEncoder %= 360;
    	
    	
    	
    	double str = stick.getX() * Math.abs(stick.getX()) * -1; // stick.getThrottle();(forward/reverse command, -1 to +1)
        double fwd = stick.getY() * Math.abs(stick.getY()); // stick.getThrottle();(strafe right command, -1 to +1)
        double rcw = schtick.getX() * Math.abs(schtick.getX()); // schtick.getThrottle();(rotate clockwise command, -1 to +1)


        //for any questions on code in lines 106-126, refer to Ether on cheif delphi, because thats where we got this trig sorcery
        //math for finding the degrees of all the wheels and their vectors
        double A = str-rcw*(l/r);
        double B = str+rcw*(l/r);
        double C = fwd-rcw*(w/r);
        double D = fwd+rcw*(w/r);

        double ws1 = Math.sqrt(B*B + C*C);
        double ws2 = Math.sqrt(B*B + D*D);
        double ws3 = Math.sqrt(A*A + D*D);
        double ws4 = Math.sqrt(A*A + C*C);
        double max = Math.max(ws1, Math.max(ws2, Math.max(ws3, ws4)));

        //sets all the angles and powers from the above to the wheels
        // for the time being, this is fine, but in the future, I would suggest putting the relative positions of the wheels here instead of putting in generic numbers, because it is slightly unclear.
        double wheelSpeedOne = (max>1) ? ws1/max : ws1;
        double wheelSpeedTwo = (max>1) ? ws2/max : ws2;
        double wheelSpeedThree = (max>1) ? ws3/max : ws3;
        double wheelSpeedFour = (max>1) ? ws4/max : ws4;
        double wheelAngleOne = ( C == 0 && B == 0) ? 0 : Math.toDegrees(Math.atan2(B,C));
        double wheelAngleTwo = ( D == 0 && B == 0) ? 0 : Math.toDegrees(Math.atan2(B,D));
        double wheelAngleThree = ( D == 0 && A == 0) ? 0 : Math.toDegrees(Math.atan2(A,D));
        double wheelAngleFour = ( C == 0 && A == 0) ? 0 : Math.toDegrees(Math.atan2(A,C));
        
        
        //this takes the difference of the wanted angle and current angle to see the shorter
        //distance, then it modifies motor value based on angle
        double wA1 = (wheelAngleOne < 0) ? 360 - Math.abs(wheelAngleOne) : wheelAngleOne;
        double uRTdr;
        
        if(Math.abs(uRE-wA1) < 1) {
        	uRTdr = 0;
        }else if (Math.abs(uRE - wA1) <= 180){			// if absoulte of (current angle - wanted angle) is less than or equal to 180
        	uRTdr = ((uRE -wA1) < 0) ? -1 : 1;		//Direction = (if (current angle - wanted angle) is less than 0) true: - False: +
        }else if(Math.abs(uRE - wA1) > 180){		
        	uRTdr = ((uRE -wA1) < 0) ? 1 : -1;
        //
        }else if(Math.abs(uRE - wA1) == 180) {
        	uRTdr = 1;
        //
        }else{
        	uRTdr = 0;
        }
        //these section are just repeats of the above section for the other wheels
        double wA2 = (wheelAngleTwo < 0) ? 360 - Math.abs(wheelAngleTwo) : wheelAngleTwo;
        double uLTdr;
        
        if (Math.abs(uLE - wA2) <= 180){			
        	uLTdr = ((uLE -wA2) < 0) ? -1 : 1;
        }else if(Math.abs(uLE - wA2) > 180){
        	uLTdr = ((uLE -wA2) < 0) ? 1 : -1;
        //
        }else if(Math.abs(uLE - wA2) == 180) {
        	uLTdr = 1;
        //  
        }else{
        	uLTdr = 0;
        }
        
        double wA3 = (wheelAngleThree < 0) ? 360 - Math.abs(wheelAngleThree) : wheelAngleThree;
        double dLTdr;
        
        if (Math.abs(dLE - wA3) <= 180){
        	dLTdr = ((dLE -wA3) < 0) ? -1 : 1;
        }else if(Math.abs(dLE - wA3) > 180){
        	dLTdr = ((dLE - wA3) < 0) ? 1 : -1;
        //
        }else if(Math.abs(dLE - wA3) == 180) {
        	dLTdr = 1;
        //
        }else{
        	dLTdr = 0;
        }
        
        double wA4 = (wheelAngleFour < 0) ? 360 - Math.abs(wheelAngleFour) : wheelAngleFour;
        double dRTdr;
        
        if (Math.abs(dRE - wA4) <= 180){
        	dRTdr = ((dRE -wA4) < 0) ? -1 : 1;
        }else if(Math.abs(dRE - wA4) > 180){
        	dRTdr = ((dRE -wA4) < 0) ? 1 : -1;
        //
        }else if(Math.abs(dRE - wA4) == 180) {
        	dRTdr = 1;
        //
        }else{
        	dRTdr = 0;
        }
        
        double rampmod = OI.stick.getRawButton(3) ? .5 : 1;
        double rampmodX = OI.schtick.getRawButton(3) ? .25 : 1;
        double throttle = (OI.stick.getZ() * -0.5) + 0.9;
        
        // I added a 0.7 to slow down the robot, as it was discovered that at full power, the robot would tip over.
        upLeftDrive.set(wheelSpeedTwo * 0.7 * rampmod * rampmodX * throttle); 
        upRightDrive.set(wheelSpeedOne * 0.7 * rampmod * rampmodX * throttle);
        downLeftDrive.set(wheelSpeedThree * 0.7 * rampmod * rampmodX * throttle);
        downRightDrive.set(wheelSpeedFour * 0.7 * rampmod * rampmodX * throttle);
        
        // This displays inputs and outputs on the Dashboard for troubleshooting purposes
        SmartDashboard.putNumber("uL", (double) upLeftEnc.get());
        SmartDashboard.putNumber("uR", (double) upRightEnc.get());
        SmartDashboard.putNumber("dL", (double) downLeftEnc.get());
        SmartDashboard.putNumber("dR", (double) downRightEnc.get());
        SmartDashboard.putNumber("wS1", (double) wheelSpeedOne);
        SmartDashboard.putNumber("wS2", (double) wheelSpeedTwo);
        SmartDashboard.putNumber("wS3", (double) wheelSpeedThree);
        SmartDashboard.putNumber("wS4", (double) wheelSpeedFour);
        SmartDashboard.putNumber("wA1", (double) wheelAngleOne);
        SmartDashboard.putNumber("wA2", (double) wheelAngleTwo);
        SmartDashboard.putNumber("wA3", (double) wheelAngleThree);
        SmartDashboard.putNumber("wA4", (double) wheelAngleFour);
        
        //Test that Mod is working
        SmartDashboard.putNumber("uLe", (double) upLeftEncoder);
        SmartDashboard.putNumber("uRe", (double) upRightEncoder);
        SmartDashboard.putNumber("dLe", (double) downLeftEncoder);
        SmartDashboard.putNumber("dRe", (double) downRightEncoder);
        
        //exponential decrease of motor input based on distance from wanted degree
        double uRT = (Math.abs(uRE - wA1) > 80) ? 0.7 : (Math.abs(uRE - wA1)) / 70;
        double uLT = (Math.abs(uLE - wA2) > 80) ? 0.7 : (Math.abs(uLE - wA2)) / 70;
        double dLT = (Math.abs(dLE - wA3) > 80) ? 0.7 : (Math.abs(dLE - wA3)) / 70;
        double dRT = (Math.abs(dRE - wA4) > 80) ? 0.7 : (Math.abs(dRE - wA4)) / 70;

        //takes motor power and mods from lesser angle code
        upRightTurn.set(uRT * uRTdr);
        upLeftTurn.set(uLT * uLTdr);
        downLeftTurn.set(dLT * dLTdr);
        downRightTurn.set(dRT * dRTdr);
        
        //DEBUGGING (yay)
        double uRTC = uRT * uRTdr;
        double uLTC = uLT * uLTdr;
        double dLTC = dLT * dLTdr;
        double dRTC = dRT * dRTdr;
        
        SmartDashboard.putNumber("uRTC", (double) uRTC);
        SmartDashboard.putNumber("uLTC", (double) uLTC);
        SmartDashboard.putNumber("dLTC", (double) dLTC);
        SmartDashboard.putNumber("dRTC", (double) dRTC);

        commandStatus = true;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return commandStatus;
    }

    // Called once after isFinished returns true
    protected void end() {

    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
