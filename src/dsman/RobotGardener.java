package dsman;
import battlecode.common.*;

import dsman.RobotGeneric;

public strictfp class RobotGardener {
    private static RobotController rc;

    public static void moveToTopLeft() throws GameActionException {
        while (true) {
            System.out.println("In Loop");
            if (rc.canMove(Direction.getWest())) {
                System.out.println("Moving West!");

                rc.move(Direction.getWest());
                Clock.yield();
                continue;
            }

            if (rc.canMove(Direction.getSouth())) {
                System.out.println("Moving North!");

                rc.move(Direction.getSouth());
                Clock.yield();
                continue;
            }
            break;
        }

        System.out.println("Done moving");
    }

    public static void firstGardenerLoop() throws GameActionException {
        int treeCount = 4;
        System.out.println("I'm the first Gardener!");

        moveToTopLeft();

        int i;
        for (i = 0; i < treeCount; i++) {
            rc.move(Direction.getEast());
            Clock.yield();
            rc.move(Direction.getEast());
            Clock.yield();
            rc.move(Direction.getEast(), .05f);
            Clock.yield();
            while (!rc.canPlantTree(Direction.getWest()))
                ;
            rc.plantTree(Direction.getWest());
            Clock.yield();
        }

        rc.move(Direction.getNorth());
        Clock.yield();
        rc.move(Direction.getNorth());
        Clock.yield();
        rc.move(Direction.getNorth(), .05f);
        Clock.yield();

        rc.move(Direction.getWest());
        Clock.yield();
        rc.move(Direction.getWest());
        Clock.yield();
        rc.move(Direction.getWest(), .25f);
        Clock.yield();

        while (true) {
            for (i = 0; i < treeCount - 1; i++) {
                MapLocation loc = rc.getLocation();
                MapLocation tree = loc.add(Direction.getSouth(), 1.5f);

                try {
                    rc.water(tree);
                } catch (Exception e) {
                    System.out.println("Can't water tree");
                }

                rc.move(Direction.getWest());
                Clock.yield();
                rc.move(Direction.getWest());
                Clock.yield();
            }

            for (i = 0; i < treeCount - 1; i++) {
                MapLocation loc = rc.getLocation();
                MapLocation tree = loc.add(Direction.getSouth(), 1.5f);

                try {
                    rc.water(tree);
                } catch (Exception e) {
                    System.out.println("Can't water tree");
                }

                rc.move(Direction.getEast());
                Clock.yield();
                rc.move(Direction.getEast());
                Clock.yield();
            }
        }
    }

    public static void firstGardenerLoop2() throws GameActionException {
        Direction[] trees = new Direction[] {
            new Direction(0f, 1f),
            new Direction(1f, 0f),
            new Direction(-1f, 0f),
        };
        MapLocation myLocation;
        int i;

        moveToTopLeft();

        rc.move(Direction.getEast());
        Clock.yield();
        rc.move(Direction.getEast());
        Clock.yield();

        myLocation = rc.getLocation();

        for (Direction tree: trees) {
            while (!rc.canPlantTree(tree))
                Clock.yield();
            rc.plantTree(tree);
        }

        while (true) {
            for (Direction tree: trees) {
                rc.water(myLocation.add(tree, 1.5f));
                Clock.yield();
            }
        }
    }

    public static void run(RobotController rcc) {
        int gardeners;
        rc = rcc;

        System.out.println("I'm a gardener!");

        try {
            gardeners = rc.readBroadcast(RobotGeneric.GARDENER_COUNT_CHANNEL);
            rc.broadcast(RobotGeneric.GARDENER_COUNT_CHANNEL, gardeners + 1);
        } catch (Exception e) {
            System.out.println("First Gardener Broadcast Exception");
            e.printStackTrace();
            return ;
        }

        if (gardeners == 0) {
            try {
                firstGardenerLoop2();
            } catch (Exception e) {
                System.out.println("First Gardener Exception");
                e.printStackTrace();
            }
        }

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                int trees;
                MapLocation archonLoc = new MapLocation(xPos,yPos);

                trees = rc.getTreeCount();

                if (trees < RobotGeneric.TREE_COUNT_MAX) {
                    Direction dir = RobotGeneric.randomDirection();
                    if (rc.canPlantTree(dir)) {
                        rc.plantTree(dir);
                    }
                }

                // Generate a random direction

                //// Randomly attempt to build a soldier or lumberjack in this direction
                //if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                //    rc.buildRobot(RobotType.SOLDIER, dir);
                //} else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                //    rc.buildRobot(RobotType.LUMBERJACK, dir);
                //}

                // Move randomly
                RobotGeneric.tryMove(rc, RobotGeneric.randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
}
