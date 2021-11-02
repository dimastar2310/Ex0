package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import java.util.*;
// ID: Netanel Cohen - 205569890 , Dimitri Sanin - 324466671
public class ElevatorTester_3 implements ElevatorAlgo {
    private Building building;
    private Elevator_DB[] l_ElevatorDB;

    public ElevatorTester_3(Building b) {
        building = b;
        l_ElevatorDB = new Elevator_DB[b.numberOfElevetors()];
        for (int i = 0; i < l_ElevatorDB.length; i++) {
            l_ElevatorDB[i] = new Elevator_DB();
        }
    }

    @Override
    public Building getBuilding() {return this.building;}

    @Override
    public String algoName() {return "Elevator Tester";}

    @Override
    public int allocateAnElevator(CallForElevator c) {
        int chosenElevtor = -1;
        double chosenTime = Double.MAX_VALUE;
        boolean isSameDirc = false;
        for (int i = 0; i < l_ElevatorDB.length; i++) {
            Elevator el = building.getElevetor(i);
            if(el.getState() == el.ERROR) {
                continue;
            }
            if (el.getState() == el.LEVEL || isInPath(c,el)) {
                double thisTime = caculateDisTime(c, i);
                if (thisTime < chosenTime) {
                    if (!isSameDirc || el.getState() == c.getType()) {
                        isSameDirc = el.getState() == c.getType();
                        chosenElevtor = i;
                        chosenTime = thisTime;
                    }
                }
            }
        }

        if (chosenElevtor != -1) {
            l_ElevatorDB[chosenElevtor].registerCall(c,building.getElevetor(chosenElevtor));
            return chosenElevtor;
        }
        chosenTime = Double.MAX_VALUE;
        for (int i = 0; i < l_ElevatorDB.length; i++) { // Checks if the Source of the call is closeset to elevator last destenation
            Elevator el = building.getElevetor(i);
            if(el.getState() == el.ERROR) {
                continue;
            }
                double thisTime = caculateDisTime2(c, i);
                if (thisTime < chosenTime) {
                    chosenElevtor = i;
                    chosenTime = thisTime;
            }
        }
        l_ElevatorDB[chosenElevtor].registerCall(c,building.getElevetor(chosenElevtor));
        return chosenElevtor;
    }

    @Override
    public void cmdElevator(int elev) { // Commands Elevator to goTo the head of the current Priority Queue
        if (l_ElevatorDB[elev].currentQ.size() == 0) {
            if (!l_ElevatorDB[elev].switchQueue()) {
                return;
            }
        }
        Elevator el = building.getElevetor(elev);
        if (el.getState() == el.LEVEL) {
            if (el.getPos() == l_ElevatorDB[elev].currentQ.peek()) {
                while (l_ElevatorDB[elev].currentQ.size() > 0 && l_ElevatorDB[elev].currentQ.peek() == el.getPos()) { // Removing Duplicates in the Queue
                    l_ElevatorDB[elev].currentQ.poll();
                }
            }
            if (l_ElevatorDB[elev].currentQ.size() == 0) {
                if (!l_ElevatorDB[elev].switchQueue()) {
                    return;
                }
            }
            el.goTo(l_ElevatorDB[elev].currentQ.peek());
            l_ElevatorDB[elev].lastsentPos = l_ElevatorDB[elev].currentQ.peek();
        } else {
            if (l_ElevatorDB[elev].currentQ == l_ElevatorDB[elev].pUp &&l_ElevatorDB[elev].currentQ.peek() < l_ElevatorDB[elev].lastsentPos) {
                el.stop(l_ElevatorDB[elev].currentQ.peek());
                l_ElevatorDB[elev].lastsentPos = l_ElevatorDB[elev].currentQ.peek();
            }
            if(l_ElevatorDB[elev].currentQ == l_ElevatorDB[elev].pDown &&l_ElevatorDB[elev].currentQ.peek() > l_ElevatorDB[elev].lastsentPos){
                el.stop(l_ElevatorDB[elev].currentQ.peek());
                l_ElevatorDB[elev].lastsentPos = l_ElevatorDB[elev].currentQ.peek();
            }
        }
    }

    public double caculateDisTime(CallForElevator x, int elev) { // Caculate time according to time = distance/speed according to the current postion of the elevator
        Elevator curr = this.getBuilding().getElevetor(elev);
        int src = x.getSrc();
        int dest = x.getDest();
        int pos = curr.getPos();
        double speed = curr.getSpeed();
        double time1 = Math.abs((dest - src) / speed) + curr.getTimeForClose() + curr.getTimeForOpen() + curr.getStartTime() + curr.getStopTime();
        double time2 = Math.abs((src - pos) / speed) + curr.getTimeForClose() + curr.getTimeForOpen() + curr.getStartTime() + curr.getStopTime();
        return time1 + time2;
    }

    public double caculateDisTime2(CallForElevator c, int elev) { // Caculate the time it would take to an elevator to get to the last destenation in its call list compare to the new call Src
        Elevator curr = this.getBuilding().getElevetor(elev);
        int src = l_ElevatorDB[elev].lastDest;
        int dest = c.getSrc();
        int pos = curr.getPos();
        double speed = curr.getSpeed();
        double time1 = Math.abs((dest - src) / speed) + curr.getTimeForClose() + curr.getTimeForOpen() + curr.getStartTime() + curr.getStopTime();
        double time2 = Math.abs((src - pos) / speed) + curr.getTimeForClose() + curr.getTimeForOpen() + curr.getStartTime() + curr.getStopTime();
        return time1 + time2;
    }

        public boolean isInPath (CallForElevator c, Elevator el) { // Checks if Eelevator is in Path
            if(c.getType() == c.UP) {
                if(el.getState() == el.UP) {
                    return el.getPos() < c.getSrc() && c.getDest() <= l_ElevatorDB[el.getID()].maxUpDest && l_ElevatorDB[el.getID()].pUp.size() > 0;
                }else if(el.getState() == el.DOWN) {
                    return l_ElevatorDB[el.getID()].maxDownDest < c.getSrc() && c.getDest() <= l_ElevatorDB[el.getID()].maxUpDest && l_ElevatorDB[el.getID()].pUp.size() > 0;
                }

         }else {
                if(el.getState() == el.DOWN) {
                    return el.getPos() > c.getSrc() && c.getDest() >= l_ElevatorDB[el.getID()].maxDownDest && l_ElevatorDB[el.getID()].pDown.size() > 0;
                }else if(el.getState() == el.UP) {
                    return l_ElevatorDB[el.getID()].maxUpDest > c.getSrc() && c.getDest() >= l_ElevatorDB[el.getID()].maxDownDest && l_ElevatorDB[el.getID()].pDown.size() > 0;
                }
            }
            return false;
        }

    }


