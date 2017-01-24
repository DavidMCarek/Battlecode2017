package abra;

import battlecode.common.*;

public strictfp class Utils {


    public static boolean[] setCanSeeNearbyRobots(RobotInfo[] nearbyRobots, RobotController rc) throws GameActionException {

        TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
        boolean[] canSeeNearbyRobot = new boolean[nearbyRobots.length];

        double x0, x1, y0, y1;
        double cx, cy, cr;

        x0 = rc.getLocation().x;
        y0 = rc.getLocation().y;

        for (int i = 0; i < nearbyRobots.length; i++) {
            x1 = nearbyRobots[i].location.x;
            y1 = nearbyRobots[i].location.y;
            canSeeNearbyRobot[i] = true;

            for (int j = 0; j < nearbyRobots.length; j++) {
                if (i == j) {
                    continue;
                }

                cx = nearbyRobots[j].location.x;
                cy = nearbyRobots[j].location.y;
                cr = nearbyRobots[j].type.bodyRadius;

                if (doesRayIntersectCircle(x0, y0, x1, y1, cx, cy, cr)) {
                    canSeeNearbyRobot[i] = false;
                    break;
                }
            }

            if (!canSeeNearbyRobot[i]) {
                continue;
            }

            for (int j = 0; j < nearbyTrees.length; j++) {
                cx = nearbyTrees[j].location.x;
                cy = nearbyTrees[j].location.y;
                cr = nearbyTrees[j].radius;

                if (doesRayIntersectCircle(x0, y0, x1, y1, cx, cy, cr)) {
                    canSeeNearbyRobot[i] = false;
                    break;
                }
            }
        }

        return canSeeNearbyRobot;
    }

    public static boolean doesRayIntersectCircle(double x0, double y0, double x1, double y1, double cx, double cy, double cr) {
        double a = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
        double b = 2.0 * (x1 - x0) * (x0 - cx) + 2.0 * (y1 - y0) * (y0 - cy);
        double c = (x0 - cx) * (x0 - cx) + (y0 - cy) * (y0 - cy) - cr * cr;
        double det = b * b - 4.0 * a * c;
        return det >= 0.0;
    }

    public static Direction trySafeShot(RobotInfo[] nearbyBots, boolean[] botsCanSee, RobotController rc) throws GameActionException {

        for (int i = 0; i < botsCanSee.length; i++) {
            if (rc.getTeam() != nearbyBots[i].getTeam() && botsCanSee[i] && rc.canFireSingleShot()) {
                Direction shootDir = new Direction(rc.getLocation(), nearbyBots[i].getLocation());
                rc.fireSingleShot(shootDir);
                return shootDir;
            }
        }

        return null;
    }

    public static boolean willCollideWithMe(BulletInfo bullet, MapLocation location, float bodyRadius) {

        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        Direction directionToRobot = bulletLocation.directionTo(location);
        float distToRobot = bulletLocation.distanceTo(location);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta));

        return (perpendicularDist <= bodyRadius);
    }

    public static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

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

    public static boolean tryBuild(Direction dir, RobotType robotType, RobotController rc) throws GameActionException {
        return tryBuild(dir, 3, 30, robotType, rc);
    }

    public static boolean tryBuild(Direction dir, int checksPerSide, float degreeOffset, RobotType robotType, RobotController rc) throws GameActionException {

        if (rc.canBuildRobot(robotType, dir)) {
            rc.buildRobot(robotType, dir);
            return true;
        }

        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            if(rc.canBuildRobot(robotType, dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.buildRobot(robotType, dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }

            if(rc.canBuildRobot(robotType, dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.buildRobot(robotType, dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }

            currentCheck++;
        }

        return false;
    }

    public static Direction trySafeMove(Direction preferredDir, RobotController rc) throws GameActionException {
        return trySafeMove(preferredDir, rc.senseNearbyBullets(), 5, 30, rc);
    }

    public static Direction trySafeMove(Direction dir, BulletInfo[] nearbyBullets, int checksPerSide, float degreeOffset, RobotController rc) throws GameActionException {

        MapLocation currentLocation = rc.getLocation();
        boolean collision = false;

        if (rc.canMove(dir)) {
            for (int i = 0; i < nearbyBullets.length; i++)
                if (willCollideWithMe(nearbyBullets[i], currentLocation.add(dir), rc.getType().bodyRadius)) {
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
                    if (willCollideWithMe(nearbyBullets[i], currentLocation.add(dir.rotateLeftDegrees(degreeOffset*currentCheck)), rc.getType().bodyRadius)) {
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
                    if (willCollideWithMe(nearbyBullets[i], currentLocation.add(dir.rotateRightDegrees(degreeOffset*currentCheck)), rc.getType().bodyRadius)) {
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
        Direction tempDir = null;

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
            return null;

        Direction moveDir = new Direction(dx, dy);

        if (rc.canMove(moveDir)) {
            rc.move(moveDir);
            return moveDir;
        }

        return null;
    }

}
