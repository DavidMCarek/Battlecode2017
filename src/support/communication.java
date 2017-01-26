package support;
import battlecode.common.*;

//class can handle communications between robots to support formations

public class communication {
	
	/*
		Channel Index 0-1999 unit information
		0-3 type (Archon, LumberJack...)
		4 health (high or low)
		5 fired shot? (true or false)
		6 is mobile? (true or false)
		7-13 formation id (which formation does unit belong to)
		14-17 Position in formation 
		18-20 formation type (triad, pentad, line, semicircle...)
		21-31 undefined (but how does it know?)
	*/
	
	/*
	   Channel Index 2000-2999 formation info
	   0-3 turns since last move
	   4-10 captains id
	   11 has opening
	   12-31 position open or not if applicable
	   
	 */
	
	/*
	  Channel Index 3000-4999 location information
	  location of unit n = channel[n%2000+3000]
	   
	 */
	
	 /*
	  * 1 -- triad
	  * 2 -- pentad
	  * 3 -- line
	  * 4 -- semicircle
	  * 5 -- decad
	  * 6
	  * 7
	  */
	public static void updateUnit(RobotController rc) throws GameActionException
	{
		int data=0;
		
		//encode type in the first 3 (MSB) bits
		data+=getUnitTypeValue(rc)<<28;
		
		//encode bit 4 for health
		data^=0x10000000; //hardcodes health as high (this needs refinment)
		
		//encode bit 5 for fired shot
		if(!rc.canFirePentadShot() || !rc.canFireSingleShot() || !rc.canFireTriadShot())
		data^=0x08000000;
		
		//encode 6 for mobile
		if(rc.hasMoved())
		data^=0x00800000;
		
		//encode bits 7-13 for formation id (shift 18)
		data=appendFormationID(data,getFormationID(rc.getID(),rc));
		
		//encode bits 14-17 for position in formation
		data=appendPosistionInFormation(data,getPosistionInFormation(rc.getID(),rc));
		
		//encode bits 18-20 for formation type
		data=appendFormationType(data,getFormationType(rc.getID(),rc));
		
		updateLocation(rc);
		rc.broadcast(rc.getID(), data);
		
		
	}

	public static void updateUnit(RobotController rc, boolean health, boolean mobile, boolean shotsFired,int formationID,int formationPosistion, int formationType) throws GameActionException
	{
		int data=0;
		
		//encode type in the first 3 (MSB) bits
		data+=getUnitTypeValue(rc)<<28;
		
		//encode bit 4 for health
		if(health)
			data^=0x10000000; 
		
		//encode bit 5 for fired shot
		if(shotsFired)
			data^=0x08000000;
		
		//encode 6 for mobile
		if(mobile)
			data^=0x00800000;
		
		//encode bits 7-13 for formation id (shift 18)
		data=appendFormationID(data,formationID);
		
		//encode bits 14-17 for position in formation
		data=appendPosistionInFormation(data,formationPosistion);
		
		//encode bits 18-20 for formation type
		data=appendFormationType(data,formationType);
		
		rc.broadcast(rc.getID(), data);
		updateLocation(rc);
	}
	
	public static void updateLocation(RobotController rc,int index,int location) throws GameActionException
	{
		rc.broadcast(index%2000+3000, location);
	}
	
	public static void updateLocation(RobotController rc) throws GameActionException
	{
		updateLocation(rc,rc.getID(),convertLocationToInt(rc.getLocation().x,rc.getLocation().y));
	}
	
	public static int appendFormationID(int data, int formationID)
	{
		return data;
	}
	
	public static int appendPosistionInFormation(int data, int posistion)
	{
		return data;
	}
	
	public static int appendFormationType(int data, int type)
	{
		return data;
	}
	public static int getUnitTypeValue(RobotController rc)
	{
		switch(rc.getType())
		{
		case ARCHON: return 1;
		case GARDENER: return 2;
		case LUMBERJACK: return 3;
		case SOLDIER: return 4;
		case SCOUT: return 5;
		case TANK: return 6;
		default: return 0;
		}
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
			
			counter++;
		}
		
		return id;
	}
	
	public static int getFormationID(int index,RobotController rc) throws GameActionException
	{
		int data=rc.readBroadcast(index);
		return getIntFromBitRange(data,7,13);
		
	}
	
	public static int getPosistionInFormation(int index,RobotController rc) throws GameActionException
	{
		//14-17 posistion in formation
		int data = rc.readBroadcast(index);
		return getIntFromBitRange(data,14,17);
	}
	
	public static int getFormationType(int index,RobotController rc) throws GameActionException
	{
	
		int data = rc.readBroadcast(index);
		return getIntFromBitRange(data,18,20);
	}

	public static int getUnitLocation(RobotController rc, int id) throws GameActionException
	{
		return rc.readBroadcast(id%2000+3000);
	}
	public static  MapLocation getLocation(int location)
	{
		//gets the location of a given unit (index) from an int
		MapLocation ml=new MapLocation(getXCoordinate(location%2000+3000),getYCoordinate(location%2000+3000));
		return ml;
	}
	
	public static float getXCoordinate(int location)
	{
		//first 4 bytes are the x coordinate
		return ((float)getIntFromBitRange(location,0,15));
	}
	
	public static float getYCoordinate(int location)
	{
		//last 4 bytes are the Y coordinate
		return ((float)getIntFromBitRange(location,16,31));
	}
	
	public static int convertLocationToInt(float x,float y)
	{
		return (int)x+(int)y;
	}
	
	public static int[] getOpenFormation(RobotController rc) throws GameActionException
	{
		//find an open spot in a formation and return the formation id
		//array response
		//0=formation index
		//1=formation type
		//2=formation position
		int res[]={0,0,0};
		for(int i=2000; i<3000;i++)
		{
			int data=rc.readBroadcast(2000+i);
			if(hasCaptain(data))
			{
				if(hasOpening(data))
				{
					//get opening
					int captainID=getCaptainID(data);
					int formationType=getFormationTypeFromCaptain(rc,captainID);
					res[0]=i;
					res[1]=formationType;
					res[2]=openingInFormation(data,getFormationQuantity(formationType));
					return res;
				}
			}
			return res;
				
		}
		return res;
	}
	
	public static boolean hasCaptain(int data)
	{
		//4-10 Captain's id
		return getIntFromBitRange(data,4,10)>0;
	}
	
	public static int getCaptainID(int data)
	{
		return getIntFromBitRange(data,4,10);
	}

	public static boolean hasOpening(int data)
	{
		return getBitAt(data,31-11);
	}
	
	public static int openingInFormation(int data,int maxUnits)
	{
		//12-31 open/filled positions
		//returns open posistion
		
		maxUnits+=12;
		for(int i =12;i<maxUnits;i++)
		{
			if(!getBitAt(data,31-i))
				return i-12;
		}
		
		return 0;
	}
	
	public static int getFormationTypeFromCaptain(RobotController rc,int id) throws GameActionException
	{
		return getFormationType(id,rc);
	}
	
	public static int getFormationQuantity(int type)
	{
		 /*
		  * 1 -- triad (3)
		  * 2 -- pentad (5)
		  * 3 -- line (5)
		  * 4 -- semicircle (5)
		  * 5 -- decad (10)
		  * 6
		  * 7
		  */
		switch(type)
		{
		case 1: return 3;
		case 2:
		case 3: 
		case 4: return 5;
		case 5: return 10;
	    default: return 0;
		}
	}
			
	public static void updateFormation(RobotController rc,int [] formationInfo)
	{
		//formations not ready to dissolve when needed
		
		/*
		   Channel Index 2000-2999 formation info
		   0-3 turns since last move
		   4-10 captains id
		   11 has opening
		   12-31 position open or not if applicable
		   
		 */
		
		//array values
		//0=formation index
		//1=formation type
		//2=formation position
		
		int data =0;
		
		//update turn count
		
		//update captains id
		//data=appendBitsToRange(data,4,10,.)
		
		//set the full bit if needed, set position of newly added unit
		if(formationInfo[2]>getFormationQuantity(formationInfo[1]))
		{
			//this is a problem
			data^=0X00100000;
		}
		else if(formationInfo[2]==getFormationQuantity(formationInfo[1]))
		{
			//encode bit 11 as true
			data^=0X00100000;
			data=setBitAt(data,12+formationInfo[2]);
			
		}
		else
		{
			data=setBitAt(data,12+formationInfo[2]);
		}
			
		
	}
	
	public static void updateFormation(RobotController rc,int captainsID,int formationType,int formationID) throws GameActionException
	{
	//formations not ready to dissolve when needed
		
		/*
		   Channel Index 2000-2999 formation info
		   0-3 turns since last move
		   4-10 captains id
		   11 has opening
		   12-31 position open or not if applicable (counting the captain)
		   
		 */
		
		//array values
		//0=formation index
		//1=formation type
		//2=formation position
		
		int data =0;
		
		//update turn count
		
		//update captains id
		data=appendBitsToRange(data,4,10,captainsID);
		rc.broadcast(formationID, data);
		
		updateUnit( rc, true, true, false, formationID,1, formationType);
			
		
	}
	public static int setBitAt(int data,int bitPosition)
	{
		int set=(int) Math.pow(2, 31-bitPosition);
		data ^= set;
		return data;
	}
	public static int appendBitsToRange(int input, int start,int end, int data)
	{
		if(start>end)
		{
			int temp=start;
			start=end;
			end=temp;
		}
		data=data<<31-end;
		input ^= data;
		return input;
	}

	public static int findInactiveFormation(RobotController rc) throws GameActionException
	{
		for(int i=2000; i<3000;i++)
		{
			int index=rc.readBroadcast(i);
			if(!hasCaptain(index))
			{
				return index;
			}
		}
		
		return 0;
	}
}
	
	
