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

    public static Direction trySafeShot(RobotInfo[] nearbyBots, boolean[] botsCanSee, int shotType, RobotController rc) throws GameActionException {

        for (int i = 0; i < botsCanSee.length; i++) {
            if (shotType == 5 &&
                    (nearbyBots[i].getType().equals(RobotType.ARCHON) || nearbyBots[i].getType().equals(RobotType.TANK))) {
                if (rc.getTeam() != nearbyBots[i].getTeam() && botsCanSee[i] && rc.canFirePentadShot()) {
                    Direction shootDir = new Direction(rc.getLocation(), nearbyBots[i].getLocation());
                    rc.firePentadShot(shootDir);
                    return shootDir;
                }

            } else if (shotType == 3) {
                if (rc.getTeam() != nearbyBots[i].getTeam() && botsCanSee[i] && rc.canFireTriadShot()) {
                    Direction shootDir = new Direction(rc.getLocation(), nearbyBots[i].getLocation());
                    rc.fireTriadShot(shootDir);
                    return shootDir;
                }

            } else if (shotType == 1) {
                if (rc.getTeam() != nearbyBots[i].getTeam() && botsCanSee[i] && rc.canFireSingleShot()) {
                    Direction shootDir = new Direction(rc.getLocation(), nearbyBots[i].getLocation());
                    rc.fireSingleShot(shootDir);
                    return shootDir;
                }

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
}
