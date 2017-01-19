package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Gardener {
    public static void run(RobotController rc) {

        int turnCount = 1;
        Direction preferredDir = null;

        while (true) {
            try {

                if (turnCount > 10) {
                    TreeInfo[] trees = rc.senseNearbyTrees();
                    if (trees.length > 2) {
                        // build robots

                        // water trees
                    } else {
                        if (rc.hasTreeBuildRequirements()) {

                        }
                        // build more trees
                    }
                }

                Direction dir = Utils.randomDirection();

                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }

                Utils.tryMove(Utils.randomDirection(), rc);

                turnCount++;

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    private static boolean tryBuild(float degreeOffset, RobotController rc) throws GameActionException {

//        int currentCheck = 1;
//        int checksPerSide = 3;
//        float degreeOffset = 45;
//
//        while(currentCheck<=checksPerSide) {
//            if(rc.canPlantTree(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
//                rc.plantTree(dir.rotateLeftDegrees(degreeOffset*currentCheck));
//                return true;
//            }
//
//            if(rc.canBuildRobot(robotType, dir.rotateRightDegrees(degreeOffset*currentCheck))) {
//                rc.buildRobot(robotType, dir.rotateRightDegrees(degreeOffset*currentCheck));
//                return true;
//            }
//
//            currentCheck++;
//        }

        return false;
    }
}
