package com.example.abdou.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {

    private EditText ET1, ET2, ET3;
    private Button B, A, BR;
    private ListView L;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String PERMISSION = "permission";

    private List <String> list = new ArrayList <String>();
    private ArrayAdapter <String> adapter = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        ET1 = view.findViewById(R.id.name);
        ET2 = view.findViewById(R.id.number);
        ET3 = view.findViewById(R.id.permission_number);
        A = view.findViewById(R.id.btn_add);
        B = view.findViewById(R.id.btn);
        BR = view.findViewById(R.id.btn_remove);
        L = view.findViewById(R.id.lview);

        //intitialisation liste
        adapter = new NumberPer_Adapter(getContext(), (ArrayList <String>) list);
        L.setAdapter(adapter);


        //gestion des bouttons
        BR.setEnabled(false);
        A.setEnabled(false);

        ET2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                B.setEnabled((s.toString().length() != 0) && (ET1.getText().toString()!=null));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ET3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                A.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        L.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, final int position, long id) {

                checkList();
                /////////////////////////////// Button remove /////////////////////////////////
                BR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove(position);
                    }
                });

            }
        });


        /////////////////////////////// Button add /////////////////////////////////
        A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();

            }
        });

        /////////////////////////////// Button send /////////////////////////////////
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                load();

            }
        });


        load();


        return view;

    }


    ///////////////LES METHODES///////////////////

    //supprimer de la liste un num des permissions
    public void remove(int pos) {

        list.remove(pos);
        adapter.notifyDataSetChanged();
        BR.setEnabled(false);

    }

    //ajouter un num à la liste des permissions
    public void add() {

        list.add(ET3.getText().toString());
        ET3.setText(null);
        adapter.notifyDataSetChanged();

    }


    //sauvegarder les données de l'utilisateur en locale
    public void save() {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME, ET1.getText().toString());
        editor.putString(NUMBER, ET2.getText().toString().replaceAll(" ", ""));

        Gson gson = new Gson();
        String json = gson.toJson(list.toArray());

        editor.putString(PERMISSION, json);
        editor.apply();

    }

    //récupérer les données de l'utilisateur stocké en locale
    public void load() {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        ET1.setText(sharedPreferences.getString(NAME, ""));
        ET2.setText(sharedPreferences.getString(NUMBER, ""));


        Gson gson = new Gson();
        String json = sharedPreferences.getString(PERMISSION, null);
        Type type = new TypeToken <ArrayList <String>>() {
        }.getType();
        if (json != null) {
            list = gson.fromJson(json, type);
            adapter = new NumberPer_Adapter(getContext(), (ArrayList <String>) list);
            L.setAdapter(adapter);

        } else {
            Toast.makeText(getContext(), "veuillez entrer vos données personnelles et les sauvegarder ", Toast.LENGTH_SHORT).show();
        }

    }

    //check List of number permission is empty or not
    public void checkList() {

        if (list.isEmpty()) {

            BR.setEnabled(false);

        } else {

            BR.setEnabled(true);
        }

    }


}