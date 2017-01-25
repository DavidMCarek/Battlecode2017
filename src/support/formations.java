package support;

import battlecode.common.*;
import support.communication;

public class formations {

	public static boolean isCaptian(RobotController rc) throws GameActionException
	{
		return (getPosition(rc)==1);
		
	}
	
	public static int getPosition(RobotController rc) throws GameActionException
	{
		return communication.getPosistionInFormation(rc.getID(), rc);
	}
	
	public static boolean assignUnit(RobotController rc) throws GameActionException
	{
		int opening[]=communication.getOpenFormation(rc);
		if(opening[0]==0 && opening[1]==0 && opening[2]==0)
			return false;
		
		communication.updateUnit(rc, true, true, false, opening[0], opening[2], opening[1]);
		//update formation
		communication.updateFormation(rc, opening);
		return true;
	}
	
	public static boolean createFormation(RobotController rc, int formationType,int captainsID) throws GameActionException
	{
		 int index=communication.findInactiveFormation(rc);
		 if(index<2000)
			 return false;
		 communication.updateFormation(rc,captainsID,formationType,index);
		 return true;
	}
	
	public static void moveUnit(RobotController rc) throws GameActionException
	{
		int formationID=communication.getFormationID(rc.getID(),rc);
		int captainsID=communication.getCaptainID(formationID);
		int location=communication.getUnitLocation(rc,captainsID);
		MapLocation loc=communication.getLocation(location);
		Direction dir=new Direction(rc.getLocation(),loc);
		
		if(rc.canMove(dir))
		{
			rc.move(dir);
		}
	}
}
