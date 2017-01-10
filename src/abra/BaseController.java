package examplefuncsplayer;

import battlecode.common.RobotController;
import examplefuncsplayer.bots.*;

public class BaseController {

    private RobotController rc;

    public BaseController(RobotController rc) {
        this.rc = rc;
    }

    public void execute() {
        switch (rc.getType()) {
            case ARCHON:
                Archon.run(rc);
                break;
            case GARDENER:
                Gardener.run(rc);
                break;
            case SOLDIER:
                Soldier.run(rc);
                break;
            case LUMBERJACK:
                Lumberjack.run(rc);
                break;
            case SCOUT:
                Scout.run(rc);
            case TANK:
                Tank.run(rc);
        }
    }
}
