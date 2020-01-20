package funcsplayer2;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public class HQ extends RobotPlayer {
    static int MINERCOUNT = 0;

    static void run() throws GameActionException {
        //for (Direction dir : directions)
        if (MINERCOUNT < 6) {
            rc.buildRobot(RobotType.MINER, randomDirection());
            //tryBuild(RobotType.MINER, randomDirection());
            MINERCOUNT++;
        }

        //System.out.println();
        //int height = rc.senseElevation(rc.getLocation());
        //System.out.println(height);

    }
}
