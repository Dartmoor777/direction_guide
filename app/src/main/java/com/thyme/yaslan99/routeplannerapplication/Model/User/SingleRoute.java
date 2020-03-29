package com.thyme.yaslan99.routeplannerapplication.Model.User;

import java.util.ArrayList;

/**
 * Created by Yaroslava Landyga
 */

public class SingleRoute {

    private float totalDuration;
    private double totalDistance;
    private ArrayList<SinglePath> pathList;

    public SingleRoute() {
    }

    public SingleRoute(double totalDistance, float totalDuration, ArrayList<SinglePath> pathList) {
        this.totalDuration = totalDuration;
        this.pathList = pathList;
        this.totalDistance = totalDistance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public float getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(float totalDuration) {
        this.totalDuration = totalDuration;
    }

    public ArrayList<SinglePath> getPathList() {
        return pathList;
    }

    public void setPathList(ArrayList<SinglePath> pathList) {
        this.pathList = pathList;
    }
}
