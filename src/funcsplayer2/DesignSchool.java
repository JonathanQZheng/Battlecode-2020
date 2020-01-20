package funcsplayer2;

import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public class DesignSchool extends RobotPlayer {

    static void run() throws GameActionException {
        rc.buildRobot(RobotType.LANDSCAPER, randomDirection());
    }

}
