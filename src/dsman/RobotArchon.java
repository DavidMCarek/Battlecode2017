package dsman;
import battlecode.common.*;

import dsman.RobotGeneric;

public strictfp class RobotArchon {
    private static RobotController rc;

    public static void run(RobotController rcc) {
        rc = rcc;

        System.out.println("I'm an archon!");

        try {
            rc.hireGardener(Direction.getSouth());
        } catch (Exception e) {

        }

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = RobotGeneric.randomDirection();

                if (rc.getTeamBullets() > 100 && rc.getTreeCount() > RobotGeneric.TREE_COUNT_MAX) {
                    rc.donate(10);
                } else if (rc.getRobotCount() < 3 && rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                }

                // Move randomly
                RobotGeneric.tryMove(rc, RobotGeneric.randomDirection());

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
}
