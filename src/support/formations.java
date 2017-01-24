package support;

import battlecode.common.*;
import support.communication;

public class formations {

	public static boolean isCaptian(RobotController rc) throws GameActionException
	{
		return (getPosistion(rc)==1);
		
	}
	
	public static int getPosistion(RobotController rc) throws GameActionException
	{
		return communication.getPosistionInFormation(rc.getID(), rc);
	}
	
	public static boolean assignUnit(RobotController rc) throws GameActionException
	{
		int opening[]=communication.getOpenFormation(rc);
		if(opening[0]==0 && opening[1]==0 && opening[2]==0)
			return false;
		
	}
}
