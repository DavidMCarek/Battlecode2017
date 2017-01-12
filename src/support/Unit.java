package support;
import battlecode.common.*;

public class Unit {
	
    static int gardeners=0;
    static int soldiers=0;
    static int lumberjacks=0;
    static int tanks=0;
    static int scouts=0;
    
    public Unit()
    {}
	public boolean createUnit(RobotType robotType, RobotController rc, FieldInfo field,Direction dir) throws GameActionException
	{
		switch(robotType)
		{
		case TANK: return buildTank(rc,dir); 
		case GARDENER: return buildGardener(rc,dir);
		case SCOUT: return buildScout(rc,dir); 
		case LUMBERJACK: return buildScout(rc,dir); 
		}
		
		return false;
	}
	 
	public boolean buildSoldier(RobotController rc, Direction dir) throws GameActionException
	 {
		 if(rc.canBuildRobot(RobotType.SOLDIER, dir))
		 {
			 rc.buildRobot(RobotType.SOLDIER,dir);
			 soldiers++;
			 return true;
		 }
		
		 return false;
	 }
	 
	public boolean buildTank(RobotController rc,Direction dir) throws GameActionException
	 {
		 if(rc.canBuildRobot(RobotType.TANK, dir))
		 {
			 rc.buildRobot(RobotType.TANK,dir);
			 tanks++;
			 return true;
		 }
		
		 return false;
	 }
	 
	public boolean buildGardener(RobotController rc, Direction dir) throws GameActionException
	{
		 if(rc.canBuildRobot(RobotType.GARDENER, dir))
		 {
			 rc.buildRobot(RobotType.GARDENER,dir);
			 gardeners++;
			 return true;
		 }
		
		 return false;
	}

	public boolean buildScout(RobotController rc, Direction dir) throws GameActionException
	 {
		 if(rc.canBuildRobot(RobotType.SCOUT, dir))
		 {
			 rc.buildRobot(RobotType.SCOUT,dir);
			 scouts++;
			 return true;
		 }
		
		 return false;
	 }
	 
	public boolean buildLumberjack(RobotController rc, Direction dir) throws GameActionException
	 {
		 if(rc.canBuildRobot(RobotType.LUMBERJACK, dir))
		 {
			 rc.buildRobot(RobotType.LUMBERJACK,dir);
			 lumberjacks++;
			 return true;
		 }
		
		 return false;
	 }
		 
	public int getGardeners() { return gardeners;}
	public int getSoldiers() {return soldiers;}
	public int getLumberjacks() {return lumberjacks;}
	public int getTanks() {return tanks;}
	public int getScouts() {return scouts;}
}


