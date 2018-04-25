package com.example.abdou.menu;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {

    private static final int REQUEST_CALL = 1;
    private static final int READ_CONTACTS = 1;
    private ArrayList <Contactes> mContactes = new ArrayList <>();
    private Contactes_Adapter mAdapter;
    private ArrayList <Groupes> mGroupe = new ArrayList <>();
    private Groupes_Adapter mGroupeAdapter;
    private List <Consommateur> consommateurs = new ArrayList <Consommateur>();
    private ConsommateurAdapter adapter;
    private Button BR;
    private ListView L;
    private Spinner spinnerGroupes, spinnerContactes;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NUMBER = "number";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //pour firebase (données temporaires pour le traitement)//////////////////////////////A REVOIR//////////////////////////////////////////
    private List <String> listPer = new ArrayList();
    private String test_nom = "nom";
    private String test_crd = "00da";

    //pour firebase (données locales)
    private String[] txt_nom = new String[100];
    private String[] txt_crd = new String[100];
    private String[] jsonp = new String[100];
    private List[] listPerGOD = new ArrayList[100];


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        BR = view.findViewById(R.id.ma_btnr);
        L = view.findViewById(R.id.ma_CoList);


        /////////////////////////////// Partie Liste /////////////////////////////////
        adapter = new ConsommateurAdapter(getContext(),
                R.layout.consommateurlist, consommateurs);
        L.setAdapter(adapter);


        /////////////////////////////// Button send /////////////////////////////////
        BR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCo();
            }
        });


        ///////////////////////////////////////////Première instalation il faut vérifier les permission sinon sa crache//////////////////////////////////////////////////
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);

        } else {
            refresh(view);
        }


        //déclaration du spinner vide des groupes
        spinnerGroupes = view.findViewById(R.id.spinner2);
        mGroupeAdapter = new Groupes_Adapter(getContext(), mGroupe);
        spinnerGroupes.setAdapter(mGroupeAdapter);

        //déclaration du spinner vide des contactes
        spinnerContactes = view.findViewById(R.id.spinner1);
        mAdapter = new Contactes_Adapter(getContext(), mContactes);
        spinnerContactes.setAdapter(mAdapter);


        ////////////////////////////////////////les éléments physique de l'app/////////////////////////////////////////////////////////////////
        spinnerGroupes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                Groupes clickedGroup = (Groupes) parent.getItemAtPosition(position);
                String[] clickedGroupContactsID = clickedGroup.getCid();

                Toast.makeText(getContext(), clickedGroup.getName(), Toast.LENGTH_SHORT).show();

                ///on va remplire le spinner des contactes de se groupe séléctioner
                initContacte(clickedGroupContactsID);
                mAdapter = new Contactes_Adapter(getContext(), mContactes);
                spinnerContactes.setAdapter(mAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });

        spinnerContactes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                Contactes clickedItem = (Contactes) parent.getItemAtPosition(position);

                Toast.makeText(getContext(), clickedItem.getName() + " : " + clickedItem.getNumber(), Toast.LENGTH_SHORT).show();

                /////////////init FB/////////
                initFB();

            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });


        return view;
    }


    //////////////////////////////////////////////////////////////////////LES METHODES/////////////////////////////////////////////////////////////////////////////


    //récupére les id des contactes d'un groupe
    private String[] getContacteID(String gpid) {

        String[] contacteid = new String[100];
        int i = 0;
        String test = null;
        Cursor gCur = getContext().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{                                                       // PROJECTION
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.DISPLAY_NAME,         // contact name
                        ContactsContract.Data.DATA1                 // group
                },
                ContactsContract.Data.MIMETYPE + " = ? " + "AND " +      // SELECTION
                        ContactsContract.Data.DATA1 + " = ? ",           // set groupID
                new String[]{                                                       // SELECTION_ARGS
                        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
                        gpid
                },
                null

        );
        if ((gCur != null ? gCur.getCount() : 0) > 0) {
            while (gCur != null && gCur.moveToNext()) {
                if (test != gCur.getString(gCur.getColumnIndex(ContactsContract.Data.CONTACT_ID))) {

                    contacteid[i] = gCur.getString(gCur.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                    test = contacteid[i];
                    i++;

                }
            }
            gCur.close();
        }
        return contacteid;

    }


    //selection les contactes d'un groupe prècie
    private void initContacte(String[] listID) {
        mContactes = new ArrayList <Contactes>();

        int i = 0;
        while (listID[i] != null) {
            ContentResolver cr = getContext().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, ContactsContract.Contacts._ID + " = ? ", new String[]{listID[i]}, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext() && listID[i] != null) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            mContactes.add(new Contactes(name, phoneNo));
                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
            i++;
        }
    }


    //Remplire la liste avec les groupes
    private void initGroupe() {

        mGroupe = new ArrayList <>();
        String test = null;
        String[] testp = null;

        Cursor groupCursor = getContext().getContentResolver().query(ContactsContract.Groups.CONTENT_URI, new String[]{
                        ContactsContract.Groups._ID,
                        ContactsContract.Groups.TITLE
                }, null, null, null
        );
        if ((groupCursor != null ? groupCursor.getCount() : 0) > 0) {
            while (groupCursor != null && groupCursor.moveToNext()) {
                String gid = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups._ID));
                String gname = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.TITLE));


                Cursor gCur = getContext().getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[]{                                                       // PROJECTION
                                ContactsContract.Data.CONTACT_ID,
                                ContactsContract.Data.DISPLAY_NAME,         // contact name
                                ContactsContract.Data.DATA1                 // group
                        },
                        ContactsContract.Data.MIMETYPE + " = ? " + "AND " +      // SELECTION
                                ContactsContract.Data.DATA1 + " = ? ",           // set groupID
                        new String[]{                                                       // SELECTION_ARGS
                                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
                                gid
                        },
                        null

                );
                while (gCur.moveToNext()) {

                    if (test != gname) {
                        testp = getContacteID(gid);
                        mGroupe.add(new Groupes(gname, gid, testp));
                        test = gname;
                    }
                }
                gCur.close();
            }
        }
        if (groupCursor != null) {
            groupCursor.close();
        }
    }


    ///////////////////si on a les permission des contactes et des groupes on peut remplire les spinners//////////////////////
    public void refresh(View view) {

        initGroupe();
        spinnerGroupes = view.findViewById(R.id.spinner2);
        mGroupeAdapter = new Groupes_Adapter(getContext(), mGroupe);
        spinnerGroupes.setAdapter(mGroupeAdapter);

    }


    //////////////////////////////// remplire la liste avec les consommateurs////////////////////////////
    public void initCo() {

        int x = 0;
        consommateurs = new ArrayList <Consommateur>();

        for (int i = 0; i < mContactes.size(); i++) {

            consommateurs.add(traitementCo(x));
            x++;
        }

        adapter = new ConsommateurAdapter(getContext(),
                R.layout.consommateurlist, consommateurs);
        L.setAdapter(adapter);
    }


    ////Traitement des données pour un consommateur et le remplir
    public Consommateur traitementCo(final int x) {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String yourNP = sharedPreferences.getString(NUMBER, "");
        List <String> wait = listPerGOD[x];
        if (wait != null) {

            for (int w = 0; w < wait.size(); w++) {

                String NP = wait.get(w);
                if (NP.equals(yourNP)) {

                    return new Consommateur(txt_nom[x], txt_crd[x] + "da");
                }
            }

        }
        return new Consommateur("name", "0da");

    }


    //////////////////////////////// récupération des consommateurs depuis Firebase ////////////////////////////
    public void initFB() {

        int x = 0;
        for (int i = 0; i < mContactes.size(); i++) {

            FireBUPDATE(mContactes.get(i), x);
            x++;
        }

    }


    //récupération d'un seul consommateur depuis Firebase
    public void FireBUPDATE(Contactes s, final int x) {


        String PNumber = s.getNumber().replaceAll(" ", "");
        DatabaseReference myRefNom = database.getReference("Consommateur/" + PNumber + "/Nom");
        DatabaseReference myRefCrd = database.getReference("Consommateur/" + PNumber + "/Crédit");
        DatabaseReference myRefPerm = database.getReference("Consommateur/" + PNumber + "/Permission Liste");

        // Read from the database Nom
        myRefNom.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                test_nom = dataSnapshot.getValue(String.class);
                txt_nom[x] = test_nom;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        // Read from the database Crédit
        myRefCrd.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                test_crd = dataSnapshot.getValue(String.class);
                txt_crd[x] = test_crd;
            }


            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // Read from the database Crédit
        myRefPerm.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Gson gson = new Gson();
                String json = dataSnapshot.getValue(String.class);
                jsonp[x] = json;
                Type type = new TypeToken <ArrayList <String>>() {
                }.getType();
                listPer = gson.fromJson(jsonp[x], type);
                listPerGOD[x] = listPer;
                //Toast.makeText(getContext(), "json "+ x+ " : " +  jsonp[x], Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "jsonp "+ jsonp, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }


    /////////////Permission////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                }
            } else {
                Toast.makeText(getContext(), "Permission Refusé", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
                }

            } else {
                Toast.makeText(getContext(), "Permission Refusé", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
