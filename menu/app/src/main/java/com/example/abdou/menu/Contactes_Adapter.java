package com.example.abdou.menu;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Contactes_Adapter extends ArrayAdapter <Contactes> {

    public Contactes_Adapter(@NonNull Context context, ArrayList <Contactes> ContactesListes) {
        super(context, 0, ContactesListes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.contactes, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_name);
        TextView textViewNumber = convertView.findViewById(R.id.text_number);

        Contactes currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
            textViewNumber.setText(currentItem.getNumber());
        }

        return convertView;

    }
}
