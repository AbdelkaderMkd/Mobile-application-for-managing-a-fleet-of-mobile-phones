package com.example.abdou.menu;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class TransferFragment extends Fragment {
    private static final int REQUEST_CALL = 1;
    private static final int READ_CONTACTS = 1;
    private ArrayList <Contactes> mContactes = new ArrayList <>();
    private Contactes_Adapter mAdapter;
    private ArrayList <Groupes> mGroupe = new ArrayList <>();
    private Groupes_Adapter mGroupeAdapter;
    private Spinner spinnerGroupes, spinnerContactes;
    private EditText mTNumber, codePIN;
    private Button BT;
    private String NumberP;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        ///////////////////////////////////////////Première instalation il faut vérifier les permission sinon sa crache//////////////////////////////////////////////////

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);

        } else {
            refresh(view);
        }


        //déclaration
        mTNumber = view.findViewById(R.id.edit_text_number);
        codePIN = view.findViewById(R.id.edit_text_code);
        BT = view.findViewById(R.id.tr_btn);

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

                NumberP = clickedItem.getNumber().replaceAll(" ", "");
                Toast.makeText(getContext(), clickedItem.getName() + " : " + clickedItem.getNumber(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });

        //////////Gestion du button pour envoyer du crédit
        BT.setEnabled(false);

        mTNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BT.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transCall();
            }
        });


        return view;
    }


    ///////////////////////////////////////////////////////LES METHODES///////////////////////////////////////////////////////

    //Envoyer du crédit
    private void transCall() {
        String numberT = mTNumber.getText().toString();
        String codeP = codePIN.getText().toString();
        if (codeP.isEmpty()) codeP = "0000";
        if (NumberP.regionMatches(0, "+213", 0, 4)) {
            NumberP = NumberP.replace("+213", "0");
        }
        if (numberT.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {

                if (NumberP.regionMatches(0, "06", 0, 2)) {
                    String dial = "tel:" + "*610*1*" + NumberP + "*" + numberT + "*" + codeP + Uri.encode("#");
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    Toast.makeText(getContext(), "MOBILIS, code PIN: " + codeP, Toast.LENGTH_SHORT).show();
                } else {
                    if (NumberP.regionMatches(0, "05", 0, 2)) {
                        String dial = "tel:" + "*115*" + NumberP + "*" + numberT + Uri.encode("#");
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        Toast.makeText(getContext(), "OOREDOO", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NumberP.regionMatches(0, "07", 0, 2)) {
                            String dial = "tel:" + "*760*" + NumberP + "*" + numberT + "*0000" + Uri.encode("#");
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                            Toast.makeText(getContext(), "DJEZZY", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Sélectionner un bon numero de téléphone", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }

        } else {
            Toast.makeText(getContext(), "Sélectionner un bon numero de téléphone", Toast.LENGTH_SHORT).show();
        }
    }


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


    //Permission
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
