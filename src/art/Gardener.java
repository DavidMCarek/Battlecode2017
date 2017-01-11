package art;
import battlecode.common.*;

class Gardener {
    static int numTreesPlanted = 0;
    static int numScoutsCreated = 0;
    static TreeInfo[] nearbyTrees;
    static RobotController rc;

    static void runForever(RobotController rc) throws GameActionException {
        Gardener.rc = rc;

        while (true) {
            nearbyTrees = rc.senseNearbyTrees();            

            if (numTreesPlanted < 2) {
                plantTree();
            }
            else {
                createScout();
            }

            waterNearbyTrees();

            Clock.yield();
        }
    }

    static void plantTree() throws GameActionException {
        Direction dir = Movement.randomDirection();

        if (rc.canPlantTree(dir)) {
            rc.plantTree(dir);
            numTreesPlanted++;
        }
    }

    static void waterNearbyTrees() throws GameActionException {
        for (int i = 0; i < nearbyTrees.length; i++) {
            TreeInfo tree = nearbyTrees[i];

            if (tree.health < 35 && rc.canWater(tree.ID)) {
                rc.water(tree.ID);
            }
        }
    }

    static void createScout() throws GameActionException {
        Direction dir = Movement.randomDirection();

        if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
            rc.buildRobot(RobotType.SCOUT, dir);
            numScoutsCreated++;
        }
    }
}
