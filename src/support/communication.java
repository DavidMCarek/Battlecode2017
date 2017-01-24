package support;
import battlecode.common.*;

//class can handle communications between robots to support formations

public class communication {
	
	/*
		Channel Index 0-1999 unit information
		0-3 type
		4 health
		5 fired shot?
		6 is mobile?
		7-13 formation id
		14-17 Position in formation
		18-20 formation type
		21-31 undefined
	*/
	
	/*
	   Channel Index 2000-2999 formation info
	   0-3 turns since last move
	   4-10 captains id
	 */
	
	/*
	  Channel Index 3000-4999 location information
	  location of unit n = channel[n+3000]
	   
	 */
	
	public void updateUnit(RobotController rc)
	{
		
	}

	public int getUnitType(int index, RobotController rc) throws GameActionException
	{
		//get the int from the broadcast for the associated index and get the first 3 bits as an int
		if(index<2000)
			return getUnitTypeValue(rc.readBroadcast(index));
		else
			return 0; //convert this fromm an int to a robot type
	}

	public static int getUnitTypeValue(int index)
	{
		return index>>>28;
	}
	public boolean getUnitHealth(int index,RobotController rc) throws GameActionException
		{
			//return 1 for good, 0 for low. and operation with bit 4
			int data=rc.readBroadcast(index);
			return getBitAt(data,31-4);
					
		}
	
	public boolean firedShot(int index,RobotController rc) throws GameActionException
	{
		//and operation with bit 5
		int data=rc.readBroadcast(index);
		return getBitAt(data,31-5);
	}
	
	public boolean isMobile(int index,RobotController rc) throws GameActionException
	{
		//and operation with bit 6
		int data=rc.readBroadcast(index);
		return getBitAt(data,31-6);
	}
	
	public static boolean getBitAt(int value, int bitPosistion)
	{
		//return the bit at bitPosistion for the integer value
		int pow=(int) Math.pow(2, Math.abs(bitPosistion)); 
		return (value & pow) == pow?true:false;
	}

	public static int getIntFromBitRange(int value, int start, int end )
	{
		int id=0;
		
		if(start>end)
		{
			int temp=start;
			start=end;
			end=temp;
		}
		
		if(end-start>32)
			return -1;
		
		int counter=0;
		
		for(int i=end;i>=start;i--)
		{
			if(getBitAt(value,31-i))
				id+=(int)Math.pow(2,counter );
			
			System.out.println("id: "+id);
			counter++;
		}
		
		return id;
	}
	
	public int getFormationID(int index,RobotController rc) throws GameActionException
	{
		int data=rc.readBroadcast(index);
		return getIntFromBitRange(data,7,13);
		
	}
	
	public int getPosistionInFormation(int index,RobotController rc) throws GameActionException
	{
		//14-17 posistion in formation
		int data = rc.readBroadcast(index);
		return getIntFromBitRange(data,14,17);
	}
	
	public int getFormationType(int index,RobotController rc) throws GameActionException
	{
	
		int data = rc.readBroadcast(index);
		return getIntFromBitRange(data,18,20);
	}
	
}
	
	
