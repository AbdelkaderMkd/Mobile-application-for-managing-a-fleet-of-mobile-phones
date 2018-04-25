package com.example.abdou.menu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Abdou on 01/04/2018.
 */

public class ConsommateurAdapter extends ArrayAdapter <Consommateur> {

    int resource;

    public ConsommateurAdapter(Context context, int resource, List <Consommateur> items) {
        super(context, resource, items);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout consommateurView;
        Consommateur consommateur = getItem(position);
        if (convertView == null) {
            consommateurView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, consommateurView, true);
        } else {
            consommateurView = (LinearLayout) convertView;
        }
        TextView consommateurNom = consommateurView.findViewById(R.id.CoNom);
        consommateurNom.setTypeface(null, Typeface.BOLD);
        consommateurNom.setTextSize(TypedValue.COMPLEX_UNIT_PX, 58);
        TextView consommateurCrd = consommateurView.findViewById(R.id.CoCrd);
        consommateurCrd.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        consommateurNom.setText(consommateur.getNom());
        consommateurCrd.setText(consommateur.getCrd());
        return consommateurView;
    }

}
