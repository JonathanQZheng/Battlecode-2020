package funcsplayer2;

import battlecode.common.*;

public class Miner extends RobotPlayer {

    static void run() throws GameActionException {
        //locating the HQ for every miner
        if(HQLOC == null) {
            RobotInfo[] robotList = rc.senseNearbyRobots();
            for (RobotInfo robot : robotList) {
                //System.out.println(robot.toString());
                if (robot.type == RobotType.HQ) {
                    HQLOC = robot.location;
                    HQELEVATION = rc.senseElevation(HQLOC);
                    System.out.println("HQLOC: "+HQLOC + " " + HQELEVATION);
                }
            }
        }

        //finding where the soup is and then we want to move to the closest one
        MapLocation[] soupLocs = rc.senseNearbySoup();
        MapLocation closestSoup = null;
        int closestSoupDistance = Integer.MAX_VALUE;
        for (MapLocation location: soupLocs) {
            int distance = rc.getLocation().distanceSquaredTo(location);
            if(distance < closestSoupDistance) {
                closestSoup = location;
                closestSoupDistance = distance;
            }
        }
        System.out.println("closest soup distance" + closestSoupDistance);

        //moving towards soup
        if(closestSoup == null) {
            tryMove(randomDirection());
            System.out.println("randomly moving");
        } else if (rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
            tryMove(closestSoup);
        } else {
            tryMove(HQLOC);
            rc.depositSoup(rc.getLocation().directionTo(HQLOC), RobotType.MINER.soupLimit);
        }
        if (rc.getLocation().distanceSquaredTo(closestSoup) <= 2) {
            tryMine(rc.getLocation().directionTo(closestSoup));
        }

        //building design school

        if (rc.getTeamSoup() > 600) {
            tryBuildDesignSchool(randomDirection());
        }




        //tryBlockchain();
        //tryMove(randomDirection());
    }
    static void tryBuildDesignSchool(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(RobotType.DESIGN_SCHOOL, dir)) {
            rc.buildRobot(RobotType.DESIGN_SCHOOL, dir);
        }

    }
}
