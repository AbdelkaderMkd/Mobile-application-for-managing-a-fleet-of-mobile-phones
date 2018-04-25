package com.example.abdou.menu;

/**
 * Created by Abdou on 01/04/2018.
 */

public class Consommateur {
    private String nom;
    private String crédit;


    public Consommateur(String a, String b) {
        nom = a;
        crédit = b;
    }

    public String getNom() {
        return nom;
    }

    public String getCrd() {
        return crédit;
    }

    public void setNom(String x) {
        nom = x;
    }

    public void setCrd(String y) {
        crédit = y;
    }
}
