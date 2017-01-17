package SilverWarhammer;
import battlecode.common.*;
import support.FieldInfo;
import support.Unit;;

public strictfp class RobotPlayer {
    static RobotController rc;    
    static Unit UnitManager = new Unit();
    static int gardeners=0;
    static int soldiers=0;
    static int lumberjacks=0;
    static int tanks=0;
    static int scouts=0;
    static FieldInfo field=new FieldInfo();
 
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                runLumberjack();
                break;
            case TANK:
            	runTank();
            case SCOUT:
            	runScout();
            	break;
            
        }
	}

    static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(dir) && gardeners<3) {
                    UnitManager.buildGardener(rc, dir);
                    
                }
                else if (rc.canHireGardener(dir) && Math.random()<.01)
                	UnitManager.buildGardener(rc,dir);

                // Move randomly
                tryMove(randomDirection());

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(0,(int)myLocation.x);
                rc.broadcast(1,(int)myLocation.y);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

	static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                
                TreeInfo[] nearbyTrees=rc.senseNearbyTrees();

                // Generate a random direction
                Direction dir = randomDirection();
                
                if(nearbyTrees.length<1)
                {
                    // Move randomly
                  //  tryMove(randomDirection());
                    UnitManager.buildTree(rc, dir);

                }
                else if(nearbyTrees.length<2)
                {
                	UnitManager.buildTree(rc, dir);
                	
                }
                else
                {
                	//if there are more than 2 trees nearby
                	if(nearbyTrees.length>0 && rc.canWater(nearbyTrees[0].ID))
                	{
                		//be sure to check any nearby tree, not just one
                		rc.water(nearbyTrees[0].ID);
                	}
                }
                
                if(UnitManager.getLumberjacks()<3 && rc.getTreeCount()>=3)
                	UnitManager.buildLumberjack(rc,dir); //build lumber jacks when there are more than 3 trees
                
                if( UnitManager.getTrees()>=3)
                {
	                if (rc.canBuildRobot(RobotType.SOLDIER, dir)) 
	                    UnitManager.buildSoldier(rc, dir);
	                else if(rc.canBuildRobot(RobotType.SCOUT, dir) )
	                	UnitManager.buildScout(rc, dir);
	           /*     else
	                {
	                	if(UnitManager.getLumberjacks()<3)
	                	{
	                		if( rc.canBuildRobot(RobotType.LUMBERJACK, dir) )
	                			UnitManager.buildLumberjack(rc, dir);
	                	}
	                	
	                	else if(Math.round(Math.random())==1?true:false)
	                	{
	                		if( rc.canBuildRobot(RobotType.SCOUT, dir) && (UnitManager.getScouts()/(float)UnitManager.getTotalUnits())<.33)
	                			UnitManager.buildScout(rc,dir);
	                		else if( rc.canBuildRobot(RobotType.LUMBERJACK, dir) && (UnitManager.getScouts()/(float)UnitManager.getTotalUnits())<.4 )
	                			UnitManager.buildLumberjack(rc,dir);
	                	}
	                	else if(Math.round(Math.random())==1?true:false && rc.getTeamBullets()>75)
	                	{
	                		UnitManager.buildTree(rc,dir);
	                	}
	                } */
                }
                else
                {
                	//UnitManager.buildTree(rc, dir);
                }
                
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();
        Direction firedShot=null;

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                    
                
                }
                else
                {
                	TreeInfo[] trees=rc.senseNearbyTrees();
                	// int tree=field.getNearByTree(rc);
                	 if(trees.length>0)
                	 {
                		 if(rc.canFireSingleShot())
                		 {
                			 rc.fireSingleShot(rc.getLocation().directionTo(trees[0].location));
                			 firedShot=rc.getLocation().directionTo(trees[0].location);
                		 }
                			 
                	 }
                		 
                }
                

                if(firedShot!=null)
                tryMove(firedShot.rotateLeftDegrees(90));
                else{
                // Move randomly
                tryMove(randomDirection());
                }
                

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    static void runLumberjack() throws GameActionException {
        System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();
        
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,enemy);

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        MapLocation myLocation = rc.getLocation();
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        tryMove(toEnemy);
                    } else {
                        // Move Randomly
                        tryMove(randomDirection());
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }

    static void runTank() throws GameActionException{
    	System.out.println("SIR YES SIR!");
    	while(true)
    	{
    		try
    		{
    			tryMove(randomDirection());
    	    	  Team enemy = rc.getTeam().opponent();
    	    	  RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

    	          // If there are some...
    	          if (robots.length > 0) {
    	              // And we have enough bullets, and haven't attacked yet this turn...
    	              if (rc.canFireSingleShot()) {
    	                  // ...Then fire a bullet in the direction of the enemy.
    	                  rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
    	              }
    	          }
    	          
    	          tryMove(randomDirection());
    	          Clock.yield();
    		}
    		catch(Exception e)
    		{
    			   System.out.println("Tank Exception");
                   e.printStackTrace();
    		}
    	}
    	
    }
    /**
     * Returns a random Direction
     * @return a random Direction
     */
    
    static void runScout() throws GameActionException{
    	while(true)
    	{
    		try
    		{
    			  // See if there are any nearby enemy robots
    			Team enemy = rc.getTeam().opponent();
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                    
    			//tryMove(randomDirection());
               // forceMove(rc);
                
    			Clock.yield();
    		}
                tryMove(randomDirection());
    		}
    		catch (Exception e)
    		{
    			System.out.println("Scout Exception");
    			e.printStackTrace();
    		}
    		
    	}
    	  
    }
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    static boolean forceMove(RobotController rc) throws GameActionException
    {
    	//method does it's best to force a move
    	/*
    	 * divide a full circle (2pi) by incrementing values of i, and try each direction in that set
    	 * example. i=3 try 2pi/3, 4pi/3, and 6pi/3
    	 * 
    	 * should remove last instance of each j loop since that should always be 2pi, which can be tried before 
    	 * the loop as the initial random directions
    	 */
    	Direction dir=randomDirection();
    	for(int i=1;i<10; i++)
    	{
    		for(int j=3;j<i;j++)
    		{
    			if(rc.canMove(new Direction((float) (dir.radians+(2*Math.PI/(i*j))) ) ) )
    			{
    				rc.move(new Direction((float) (dir.radians+(2*Math.PI/(i*j))) ) );
    				return true;
    			}
    		}
    	}
    	
    	return false;
    	
    }
    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
}
