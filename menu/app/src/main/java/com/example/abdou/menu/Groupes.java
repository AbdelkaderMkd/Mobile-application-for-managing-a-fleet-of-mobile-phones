package com.example.abdou.menu;

/**
 * Created by Abdou on 18/03/2018.
 */

public class Groupes {

    private String name;
    private String gid;
    private String[] cid;

    public Groupes(String na, String gi, String[] ci) {
        name = na;
        gid = gi;
        cid = ci;
    }

    public String getName() {
        return name;
    }

    public String getGid() {
        return gid;
    }

    public String[] getCid() {
        return cid;
    }

    public void setCid(String[] conid) {
        cid = conid;
    }

}

