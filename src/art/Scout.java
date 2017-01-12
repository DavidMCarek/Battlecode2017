package art;
import battlecode.common.*;
import art.*;

public class Scout {
    static RobotController rc;

    static int nextRoundToShoot = 0;
    static TreeInfo[] nearbyTrees;
    static RobotInfo[] nearbyRobots;
    static boolean[] canSeeNearbyRobot;
    static Direction searchDirection;
    static RobotInfo enemyToShoot;
    static Direction runDirection;

    static void runForever(RobotController rc) throws GameActionException {
        Scout.rc = rc;
        searchDirection = Movement.randomDirection();

        while (true) {
            nearbyRobots = rc.senseNearbyRobots();
            nearbyTrees = rc.senseNearbyTrees();            
            setCanSeeNearbyRobots();
            setEnemyToShoot();
            setRunDirection();

            if (runDirection != null) {
                Movement.tryMove(runDirection);

                if (enemyToShoot != null) {
                    shootAtEnemy();
                    broadcastEnemyPosition();
                }
            }
            else {
                if (enemyToShoot != null) {
                    shootAtEnemy();
                    broadcastEnemyPosition();
                }

                moveMe();
            }

            Globals.update();
            Clock.yield();
        }
    }

    static void moveMe() throws GameActionException {
        if (enemyToShoot != null) {
            return;
        }

        if (Globals.roundNum > rc.readBroadcast(1)) {
            if (!Movement.tryMove(searchDirection)) {
                searchDirection = Movement.randomDirection();
            }
        }
        else {
            int x = rc.readBroadcast(2);
            int y = rc.readBroadcast(3);

            MapLocation enemyLocation = new MapLocation(x, y);
            Direction toEnemy = Globals.myLocation.directionTo(enemyLocation);

            if (!Movement.tryMove(toEnemy)) {
                if (!Movement.tryMove(searchDirection)) {
                    searchDirection = Movement.randomDirection();        
                }
            }
        }
    }

    static void setRunDirection() throws GameActionException {
        runDirection = null;

        RobotInfo closestLumberJack = null;
        double closestLumberJackDistance = 999999.0;

        for (int i = 0; i < nearbyRobots.length; i++) {
            if (nearbyRobots[i].type != RobotType.LUMBERJACK) {
                continue;
            }

            double distance = Globals.myLocation.distanceTo(nearbyRobots[i].location);
            if (distance < closestLumberJackDistance) {
                closestLumberJackDistance = distance;
                closestLumberJack = nearbyRobots[i];
            }
        }

        if (closestLumberJack == null || closestLumberJackDistance > 3.0) {
            return;
        }

        runDirection = Globals.myLocation.directionTo(closestLumberJack.location).opposite();
    }

    static void setEnemyToShoot() throws GameActionException {
        enemyToShoot = null;

        for (int i = 0; i < nearbyRobots.length; i++) {
            if (nearbyRobots[i].team == Globals.enemyTeam && canSeeNearbyRobot[i]) {
                enemyToShoot = nearbyRobots[i];
                return;
            }
        }
    }

    static void setCanSeeNearbyRobots() throws GameActionException {
        canSeeNearbyRobot = new boolean[nearbyRobots.length]; 

        double x0, x1, y0, y1;
        double cx, cy, cr;

        x0 = Globals.myLocation.x;
        y0 = Globals.myLocation.y;

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

            if (canSeeNearbyRobot[i] == false) {
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
    }

    static boolean doesRayIntersectCircle(double x0, double y0, double x1, double y1, double cx, double cy, double cr) {
        double a = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
        double b = 2.0 * (x1 - x0) * (x0 - cx) + 2.0 * (y1 - y0) * (y0 - cy);
        double c = (x0 - cx) * (x0 - cx) + (y0 - cy) * (y0 - cy) - cr * cr;
        double det = b * b - 4.0 * a * c;
        return det >= 0.0;
    }

    static void shootAtEnemy() throws GameActionException {
        if (rc.canFireSingleShot()) {
            Direction shotDirection = Globals.myLocation.directionTo(enemyToShoot.location);
            rc.fireSingleShot(shotDirection);
        }
    }

    static void broadcastEnemyPosition() throws GameActionException {
        if (Globals.roundNum > rc.readBroadcast(1)) {
            rc.broadcast(1, Globals.roundNum + 50);
            rc.broadcast(2, (int) enemyToShoot.location.x);
            rc.broadcast(3, (int) enemyToShoot.location.y);
        }
    }
}
