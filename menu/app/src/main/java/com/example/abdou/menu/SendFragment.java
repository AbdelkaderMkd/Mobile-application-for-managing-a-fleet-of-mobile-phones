package com.example.abdou.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SendFragment extends Fragment {

    private TextView TV1, TV2, TV3;
    private Button B, BB;
    private ListView L;

    public SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String BALANCE = "balance";
    public static final String PERMISSION = "permission";

    private List <String> list = new ArrayList <String>();
    private ArrayAdapter <String> adapter = null;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference consRef;

    private static final String TAG = "DataActivity";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_send, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        consRef = db.collection("Utilisateur").document(sharedPreferences.getString(NUMBER, ""));

        TV1 = view.findViewById(R.id.sname);
        TV2 = view.findViewById(R.id.snumber);
        TV3 = view.findViewById(R.id.scredit);
        B = view.findViewById(R.id.co_btn);
        BB = view.findViewById(R.id.fb_data_btn);
        L = view.findViewById(R.id.slist);


        /////////////////////////////// Button uploadUser /////////////////////////////////
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConsommateur();
            }
        });


        /////////////////////////////// Button deletUser /////////////////////////////////
        BB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConsommateur();

            }
        });

        loadConsommateur();

        return view;
    }

    /////////sauvegarder les données dans firebase
    public void saveConsommateur() {

        Consommateur consommateur = new Consommateur(sharedPreferences.getString(NAME, ""), sharedPreferences.getString(NUMBER, ""), sharedPreferences.getString(BALANCE, ""), sharedPreferences.getString(PERMISSION, null));

        consRef.set(consommateur)
                .addOnSuccessListener(new OnSuccessListener <Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Utilisateur enregistré !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        TV1.setText(consommateur.getNom());
        TV2.setText(consommateur.getNuméro());
        TV3.setText(consommateur.getCrédit());

        Gson gson = new Gson();
        String json = consommateur.getListPermission();
        Type type = new TypeToken <ArrayList <String>>() {
        }.getType();
        list = gson.fromJson(json, type);
        adapter = new NumberPer_Adapter(getContext(), R.layout.groupes, (ArrayList <String>) list);
        L.setAdapter(adapter);

    }


    /////////supprimer les données dans firebase
    public void deleteConsommateur() {

        TV1.setText("nom");
        TV2.setText("numéro");
        TV3.setText("crédit");
        list = new ArrayList <String>();
        adapter = new NumberPer_Adapter(getContext(), R.layout.groupes, (ArrayList <String>) list);
        L.setAdapter(adapter);

        consRef.delete();
        Toast.makeText(getContext(), "Utilisateur supprimé !", Toast.LENGTH_SHORT).show();

    }


    /////////charger les données depuis firebase
    public void loadConsommateur() {
        consRef.get()
                .addOnSuccessListener(new OnSuccessListener <DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Consommateur consommateur = documentSnapshot.toObject(Consommateur.class);

                            TV1.setText(consommateur.getNom());
                            TV2.setText(consommateur.getNuméro());
                            TV3.setText(consommateur.getCrédit());

                            Gson gson = new Gson();
                            String json = consommateur.getListPermission();
                            Type type = new TypeToken <ArrayList <String>>() {
                            }.getType();

                            list = gson.fromJson(json, type);
                            adapter = new NumberPer_Adapter(getContext(), R.layout.groupes, (ArrayList <String>) list);
                            L.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Aucun Utilisateur trouvé !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

}
