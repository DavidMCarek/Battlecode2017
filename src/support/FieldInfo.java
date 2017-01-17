package support;
import battlecode.common.*;
//class helps determine the size of the field by gathering information from units that attempted to move
//it gives a best estimate of the field parameters


  public class FieldInfo {
	  
	  public FieldInfo(){}
	  
	  private float westMost=0;
	  private float eastMost=0;
	  private float northMost=500;
	  private float southMost=0;
	  private TreeInfo trees[];
	  
	  
	  public float[] getFieldSize()
	  {
		  //returns an array of the best know height and width of the field
		  float [] ret={eastMost-westMost,northMost-southMost};
		  return ret;
	  }
	  
	  public float getKnownHeight()
	  {	
		  //returns the best known height of the field
		  return northMost-southMost;
	  }
	  
	  public float getKnownWidth()
	  {
		  //returns the best known width of the field
		  return eastMost-westMost;
	  }
	  
	  public void movedTo(float x,float y)
	  {
		  //this method should be called anytime a bot moves
		  //the data from the movements helps detemine the map size
		  if(x<westMost)
			  westMost=x;
		  else if(x>eastMost)
			  eastMost=x;
		  else if (y<southMost)
			  southMost=y;
		  else if(y>northMost)
			  northMost=y;
			  
	  }
	  
	  public boolean hasTreeInRange(RobotController rc)
	  {
		  //given a robot,return the first tree in the array that is within range of the robot
		  //there is no method to fill the array yet.
		  for(int i=0; i<trees.length;i++)
		  {
			  if(rc.canSenseTree(trees[i].ID))
				  return true;
		  }
		  return false;
	  }

	public int getNearByTree(RobotController rc)
	{
		//given a robot,return the first tree in the array that is within range of the robot
		 //there is no method to fill the array yet.
		 for(int i=0; i<trees.length;i++)
		  {
			  if(rc.canSenseTree(trees[i].ID))
				  return trees[i].ID;
		  }
		  return -1;
	}
}

