package com.example.abdou.menu;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import static android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS;

/**
 * Created by Abdou on 18/03/2018.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private static final int REQUEST_CALL = 1;
    private static final int READ_CONTACTS = 1;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NUMBER = "number";

    NavigationView navigationView;

    public RelativeLayout RL1,RL2,RL3,RL4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RL1=findViewById(R.id.goto_1);
        RL2=findViewById(R.id.goto_2);
        RL3=findViewById(R.id.goto_3);
        RL4=findViewById(R.id.goto_4);

        /////////////////////////////////Initialisation du menu latérale gauche//////////////////////////////
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /////////////////////Première instalation il faut vérifier les permission sinon sa crache et c'est pas la premier redirection vers l'onglet contact//////////////////////////////

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WelcomeFragment()).commit();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);

        } else {
            /*
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ContactsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_contacts);
            }
            */
        }


        ////////////////////////Button de l'accueil/////////////////////////////
        RL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactsFragment()).commit();

            }
        });

        RL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TransferFragment()).commit();

            }
        });

        RL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DataFragment()).commit();

            }
        });

        RL4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SendFragment()).commit();

            }
        });



    }




    /////////////////////////////////Gestion du menu latérale gauche//////////////////////////////
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_home:
                home();
                break;

            case R.id.nav_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactsFragment()).commit();
                break;

            case R.id.nav_transfer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TransferFragment()).commit();
                break;

            case R.id.nav_data:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DataFragment()).commit();
                break;

            case R.id.nav_send:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SendFragment()).commit();
                break;

            case R.id.nav_share:
                consultationCrédit();
                break;

            case R.id.nav_up:
                closeall();
                break;

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    ////fermer le fragment lorsqu'on retourne en arrière
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //ferme tous les fragments et ouvre le fragment de bienvenu
    public void closeall() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WelcomeFragment()).commit();

    }

    //ferme tous les fragments
    public void home() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }


    //utilisation du ussd pour consulter le crédit
    private void dailNumber(String code) {
        String ussdCode = "*" + code + Uri.encode("#");
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
    }

    //consultation du crédit et le sauvgarder dans les preferences
    public void consultationCrédit() {
        if (isAccessibilityEnabled(this, "com.example.abdou.menu/.USSDService") == true) {
            startService(new Intent(this, USSDService.class));

            SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String str = sharedPreferences.getString(NUMBER, "");

            String strOut = str.substring(0, 2);
            switch (strOut) {
                case "07":
                    dailNumber("710");
                    break;

                case "05":
                    dailNumber("200");
                    break;

                default:
                    Toast.makeText(this, "Ce service n'est pas encore disponible pour votre opérateur.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, 0);
            Toast.makeText(this, "veuillez allez dans 'Services' et activer 'PFE' pour utiliser cette fonction.", Toast.LENGTH_SHORT).show();
        }
    }


    //verifie si on a la permission d'utilisé l'accessibilité
    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List <AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }


    ////Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                }
            } else {
                Toast.makeText(this, "Permission Refusé", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
                }

            } else {
                Toast.makeText(this, "Permission Refusé", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



