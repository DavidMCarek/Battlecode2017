package art;
import battlecode.common.*;
import java.util.Random;

public class RobotPlayer {
    static RobotController rc;

    static Random random;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        random = new Random(rc.getID());

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
        }
	}

    static void runArchon() throws GameActionException {
        while (true) {
            try {
                Direction dir = randomDirection();

                if (rc.readBroadcast(0) == 0) {
                    if (rc.canHireGardener(dir)) {
                        rc.hireGardener(dir);
                        rc.broadcast(0, 1);
                    }
                }

                tryMove(randomDirection());

                Clock.yield();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

	static void runGardener() throws GameActionException {
        while (true) {
            try {
                Direction dir = randomDirection();

                if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                }

                tryMove(randomDirection());

                Clock.yield();
            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static Direction searchDirection = new Direction((float) 0.0);

    static void runSoldier() throws GameActionException {
        Team enemy = rc.getTeam().opponent();

        while (true) {
            try {
                MapLocation myLocation = rc.getLocation();

                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                if (robots.length > 0) {
                    if (rc.canFireSingleShot()) {
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }

                    if (rc.getRoundNum() > rc.readBroadcast(1)) {
                        rc.broadcast(1, rc.getRoundNum() + 50);

                        int enemyX = (int) robots[0].location.x;
                        int enemyY = (int) robots[0].location.y;

                        rc.broadcast(2, enemyX);
                        rc.broadcast(3, enemyY);
                    }
                }

                if (rc.getRoundNum() > rc.readBroadcast(1)) {
                    if (!tryMove(searchDirection)) {
                        searchDirection = randomDirection();        
                    }
                }
                else {
                    int x = rc.readBroadcast(2);
                    int y = rc.readBroadcast(3);

                    MapLocation enemyLocation = new MapLocation(x, y);
                    Direction toEnemy = rc.getLocation().directionTo(enemyLocation);

                    if (robots.length == 0) {
                        if (!tryMove(toEnemy)) {
                            if (!tryMove(searchDirection)) {
                                searchDirection = randomDirection();        
                            }
                        }
                    }
                }

                Clock.yield();
            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    static Direction randomDirection() {
        return new Direction((float)random.nextDouble() * 2 * (float)Math.PI);
    }

    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
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

    static boolean willCollideWithMe(BulletInfo bullet) {
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
}
