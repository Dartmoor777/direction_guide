package com.thyme.smalam119.routeplannerapplication.Model.User;


import java.util.ArrayList;

/**
 * Created by smalam119 on 12/11/17.
 */

public class User {
    private ArrayList<SingleRoute> routeList;

    public User() {

    }

    public User(ArrayList<SingleRoute> routeList) {
        this.routeList = routeList;
    }

    public ArrayList<SingleRoute> getRouteList() {
        return routeList;
    }

    public void setRouteList(ArrayList<SingleRoute> routeList) {
        this.routeList = routeList;
    }
}
