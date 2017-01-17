package abra;

import battlecode.common.*;

public strictfp class Utils {
    public static boolean willCollideWithMe(BulletInfo bullet, MapLocation location, float bodyRadius) {

        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        Direction directionToRobot = bulletLocation.directionTo(location);
        float distToRobot = bulletLocation.distanceTo(location);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= bodyRadius);
    }

    public static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    public static boolean tryMove(Direction dir, RobotController rc) throws GameActionException {
        return tryMove(dir,20,3, rc);
    }

    public static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide, RobotController rc) throws GameActionException {

        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }

            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }

            currentCheck++;
        }

        return false;
    }

    public static boolean tryBuild(Direction dir, int steps, RobotType robotType, boolean moving, RobotController rc) throws GameActionException {

        if (steps == 0)
            return false;

        float stepSize = 360f / steps;

        for (int i = 1; i <= steps; i++) {
            if (rc.canBuildRobot(robotType, dir.rotateRightDegrees(i * stepSize))) {
                if (i == steps && moving)
                    return  false;
                rc.buildRobot(robotType, dir.rotateRightDegrees(i * stepSize));
                return true;
            }
        }

        return false;
    }

    public static Direction trySafeMove(int collisionBullet, BulletInfo[] nearbyBullets, int steps, RobotController rc) throws GameActionException {

        MapLocation currentLocation = rc.getLocation();

        if (steps == 0)
            return null;

        float stepSize = 360f / steps;

        Direction dir = nearbyBullets[collisionBullet].dir;

        dir = dir.rotateRightDegrees(90);

        for (int i = 1; i <= steps; i++) {
            for (int j = 0; j < nearbyBullets.length; j++)
                if (!willCollideWithMe(nearbyBullets[j], currentLocation.add(dir), rc.getType().bodyRadius) && rc.canMove(dir)) {
                    rc.move(dir);
                    return dir;
                }
            dir = dir.rotateLeftDegrees(stepSize);
        }

        return null;
    }

    public static boolean microAway(RobotController rc) throws GameActionException {

        MapLocation robotLocation = rc.getLocation();

        Team enemyTeam = rc.getTeam().opponent();

        RobotInfo[] robotInfos = rc.senseNearbyRobots();

        float dx = 0;
        float dy = 0;

        boolean firstEnemy = true;

        for (int i = 0; i < robotInfos.length; i++) {
            if (robotInfos[i].getTeam().equals(enemyTeam) && robotInfos[i].getType().canAttack()) {

                if (firstEnemy) {
                    dx = robotInfos[i].getLocation().directionTo(robotLocation).getDeltaX(rc.getType().strideRadius);
                    dy = robotInfos[i].getLocation().directionTo(robotLocation).getDeltaY(rc.getType().strideRadius);
                    firstEnemy = false;
                } else {
                    dx += robotInfos[i].getLocation().directionTo(robotLocation).getDeltaX(rc.getType().strideRadius);
                    dy += robotInfos[i].getLocation().directionTo(robotLocation).getDeltaY(rc.getType().strideRadius);

                    dx = dx / 2f;
                    dy = dy / 2f;
                }
            }
        }

        if (firstEnemy)
            return false;

        Direction moveDir = new Direction(dx, dy);

        if (rc.canMove(moveDir)) {
            rc.move(moveDir);
            return true;
        }

        return false;
    }
}
