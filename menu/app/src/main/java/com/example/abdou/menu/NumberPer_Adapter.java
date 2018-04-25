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

public class NumberPer_Adapter extends ArrayAdapter <String> {

    public NumberPer_Adapter(@NonNull Context context, ArrayList <String> StringListes) {
        super(context, 0, StringListes);
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
                    R.layout.groupes, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_name);

        String currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem);
        }

        return convertView;


    }
}
