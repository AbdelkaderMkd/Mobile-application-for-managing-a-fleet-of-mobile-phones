package com.example.abdou.menu;

/**
 * Created by Abdou on 01/04/2018.
 */

public class Consommateur {
    private String nom;
    private String numéro;
    private String crédit;
    private String ListPermission;

    public Consommateur() {

    }

    public Consommateur(String a, String b) {
        nom = a;
        numéro = null;
        crédit = b;
        ListPermission = null;
    }

    public Consommateur(String a, String b, String c, String d) {
        nom = a;
        numéro = b;
        crédit = c;
        ListPermission = d;
    }


    public String getNom() {
        return nom;
    }

    public String getNuméro() { return numéro; }

    public String getCrédit() {
        return crédit;
    }

    public String getListPermission() { return ListPermission; }

    public void setNom(String x) {
        nom = x;
    }

    public void setNuméro(String y) { numéro = y; }

    public void setCrédit(String z) {
        crédit = z;
    }

    public void setListPermission(String w) { ListPermission = w; }

}
