package abra;

import battlecode.common.*;

public class MoveUtils {
    public static Direction tryMove(Direction dir, RobotController rc) throws GameActionException {
        return tryMove(dir, 15, 6, rc);
    }

    public static Direction tryMove(Direction dir, float degreeOffset, int checksPerSide, RobotController rc) throws GameActionException {

        if (rc.canMove(dir)) {
            rc.move(dir);
            return dir;
        }

        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                dir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
                rc.move(dir);
                return dir;
            }

            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                dir = dir.rotateRightDegrees(degreeOffset*currentCheck);
                rc.move(dir);
                return dir;
            }

            currentCheck++;
        }

        return null;
    }

    public static Direction trySafeMove(Direction preferredDir, RobotController rc) throws GameActionException {
        return trySafeMove(preferredDir, rc.senseNearbyBullets(), 5, 36, rc);
    }

    public static Direction trySafeMove(Direction dir, BulletInfo[] nearbyBullets, int checksPerSide, float degreeOffset, RobotController rc) throws GameActionException {

        MapLocation currentLocation = rc.getLocation();
        boolean collision = false;

        if (rc.canMove(dir)) {
            for (int i = 0; i < nearbyBullets.length; i++)
                if (Utils.willCollideWithMe(nearbyBullets[i], currentLocation.add(dir), rc.getType().bodyRadius)) {
                    collision = true;
                    break;
                }

            if (!collision) {
                rc.move(dir);
                return dir;
            }
        }

        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            collision = false;
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                for (int i = 0; i < nearbyBullets.length; i++)
                    if (Utils.willCollideWithMe(nearbyBullets[i], currentLocation.add(dir.rotateLeftDegrees(degreeOffset*currentCheck)), rc.getType().bodyRadius)) {
                        collision = true;
                        break;
                    }

                if (!collision) {
                    dir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
                    rc.move(dir);
                    return dir;
                }
            }

            collision = false;

            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {

                for (int i = 0; i < nearbyBullets.length; i++)
                    if (Utils.willCollideWithMe(nearbyBullets[i], currentLocation.add(dir.rotateRightDegrees(degreeOffset*currentCheck)), rc.getType().bodyRadius)) {
                        collision = true;
                        break;
                    }

                if (!collision) {
                    dir = dir.rotateRightDegrees(degreeOffset*currentCheck);
                    rc.move(dir);
                    return dir;
                }
            }

            currentCheck++;
        }

        return null;
    }

    public static Direction avoidBullets(Direction preferredDir, RobotController rc) throws GameActionException {
        BulletInfo[] nearbyBullets = rc.senseNearbyBullets();
        Direction tempDir;

        for (int i = 0; i < nearbyBullets.length; i++) {
            if (Utils.willCollideWithMe(nearbyBullets[i], rc.getLocation(), rc.getType().bodyRadius)) {
                tempDir = trySafeMove(preferredDir, rc);
                if (tempDir != null)
                    return tempDir;
            }
        }

        return null;
    }

    public static Direction microAway(RobotController rc) throws GameActionException {

        MapLocation robotLocation = rc.getLocation();

        Team enemyTeam = rc.getTeam().opponent();

        RobotInfo[] robotInfos = rc.senseNearbyRobots();

        float dx = 0;
        float dy = 0;

        boolean firstEnemy = true;

        for (int i = 0; i < robotInfos.length; i++) {
            if (robotInfos[i].getTeam().equals(enemyTeam)) {

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
            return null;

        Direction moveDir = new Direction(dx, dy);

        if (rc.canMove(moveDir)) {
            rc.move(moveDir);
            return moveDir;
        }

        return null;
    }

}
