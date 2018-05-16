package com.example.abdou.menu;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NumberPer_Adapter extends ArrayAdapter <String> {
//achfa aliha mena bdat lkarita

    ArrayList <String> String_Listes;

    public NumberPer_Adapter(@NonNull Context context, int resource,ArrayList <String> String_Listes) {
        super(context, 0, String_Listes);
        this.String_Listes=String_Listes;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.groupes, parent, false);

        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.numéro = convertView.findViewById(R.id.text_name);

            convertView.setTag(viewHolder);
        }

        String currentItem = getItem(position);

        viewHolder.numéro.setText(currentItem);

        return convertView;
    }


    public class ViewHolder {
        TextView numéro;
    }







/*
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
    */
}


