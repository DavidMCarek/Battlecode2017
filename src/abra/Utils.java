package abra;

import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public strictfp class Utils {
    public static boolean willCollideWithMe(BulletInfo bullet, RobotController rc) {
        MapLocation myLocation = rc.getLocation();

        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    public static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
}
