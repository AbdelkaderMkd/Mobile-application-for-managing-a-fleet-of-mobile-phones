package com.example.abdou.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SendFragment extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private TextView TV1, TV2;
    private EditText ET;
    private Button B;
    private ListView L;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String PERMISSION = "permission";

    private List <String> list = new ArrayList <String>();
    private ArrayAdapter <String> adapter = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        TV1 = view.findViewById(R.id.sname);
        TV1.setText(sharedPreferences.getString(NAME, ""));
        TV2 = view.findViewById(R.id.snumber);
        TV2.setText(sharedPreferences.getString(NUMBER, ""));
        ET = view.findViewById(R.id.co_crd);
        B = view.findViewById(R.id.co_btn);
        L = view.findViewById(R.id.slist);

        ////////////////////Gestion de la list des numéros permis//////////////////////
        Gson gson = new Gson();
        final String json = sharedPreferences.getString(PERMISSION, null);
        Type type = new TypeToken <ArrayList <String>>() {
        }.getType();
        if (json != null) {
            list = gson.fromJson(json, type);
            adapter = new NumberPer_Adapter(getContext(), (ArrayList <String>) list);
            L.setAdapter(adapter);

        } else {
            adapter = new NumberPer_Adapter(getContext(), (ArrayList <String>) list);
            L.setAdapter(adapter);
            Toast.makeText(getContext(), "veuillez remplir vos données dans l'onglet 'Données Utilisateur' ", Toast.LENGTH_SHORT).show();
        }

        /////////////////////Gestion du button send/////////////////////////////////////
        B.setEnabled(false);

        ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                B.setEnabled((s.toString().length() != 0) && (json != null));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /////////////////////////////// Button update /////////////////////////////////
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(sharedPreferences, json);
            }
        });


        return view;
    }


    //envoyer le crédit
    public void update(SharedPreferences sharedPreferences, String json) {

        DatabaseReference myRefNom = database.getReference("Consommateur/" + sharedPreferences.getString(NUMBER, "") + "/Nom");
        DatabaseReference myRefCrd = database.getReference("Consommateur/" + sharedPreferences.getString(NUMBER, "") + "/Crédit");
        DatabaseReference myRefPerm = database.getReference("Consommateur/" + sharedPreferences.getString(NUMBER, "") + "/Permission Liste");

        myRefNom.setValue(sharedPreferences.getString(NAME, ""));
        myRefCrd.setValue(ET.getText().toString());
        myRefPerm.setValue(json);
    }

}
