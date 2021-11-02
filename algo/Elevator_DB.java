package ex0.algo;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

import ex0.CallForElevator;
import ex0.Elevator;
import java.util.*;

public class Elevator_DB {
    public PriorityQueue<Integer> pUp;
    public int maxUpDest;
    public PriorityQueue<Integer> pDown;
    public int maxDownDest;
    public  PriorityQueue<Integer> currentQ;
    public int lastsentPos;
    public boolean goingUp;
    public int lastDest;

    public Elevator_DB() {
        pUp = new PriorityQueue<Integer>(10);
        pDown = new PriorityQueue<Integer>(10,Collections.reverseOrder());
        currentQ = pUp;
    }
    public void registerCall(CallForElevator c,Elevator el) { // Reguister a call and also also sets limits on the new incoming Src call if we go in the oppiste direction of an incoming call
        if(c.getType() == c.UP) {
            if(el.getPos() > c.getSrc()) {
                pDown.add(c.getSrc());
                pUp.add(c.getDest());
                currentQ = pDown;
                maxDownDest = c.getSrc();
                maxUpDest = el.getMaxFloor();
            }else {
                if(pUp.size() == 0) {
                    maxDownDest = el.getMinFloor();
                    maxUpDest = el.getMaxFloor();
                }
                currentQ = pUp;
                pUp.add(c.getSrc());
                pUp.add(c.getDest());
            }
            lastDest = Math.max(c.getDest(),lastDest);

        }else {
            if(el.getPos() < c.getSrc()){
                pUp.add(c.getSrc());
                pDown.add(c.getDest());
                currentQ = pUp;
                maxUpDest = c.getSrc();
                maxDownDest = el.getMinFloor();
            }else {
                if (pDown.size() == 0) {
                    maxDownDest = el.getMinFloor();
                    maxUpDest = el.getMaxFloor();
                }
                currentQ = pDown;
                pDown.add(c.getSrc());
                pDown.add(c.getDest());
                lastDest = Math.min(c.getDest(), lastDest);
            }


        }
    }

    public boolean switchQueue() { // Switch Pointer Queue
        if (pUp.size() > 0) {
            currentQ = pUp;
            return true;
        } else if (pDown.size() > 0) {
            currentQ = pDown;
            return true;
        }else
            return false;
    }
}
