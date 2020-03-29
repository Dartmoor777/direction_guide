package com.thyme.yaslan99.routeplannerapplication.Model.User;


import java.util.ArrayList;

/**
 * Created by Yaroslava Landyga
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
