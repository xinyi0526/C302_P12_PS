package com.myapplicationdev.android.c302_p12_ps;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class incidentAdapter extends ArrayAdapter<Incident> {

    private ArrayList<Incident> incidents;
    private Context context;
    private TextView tvTypes, tvMsg;


    public incidentAdapter( Context context, ArrayList<Incident> objects) {
        super(context, 0,objects);
        incidents = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // "Inflate" the row.xml as the layout for the View object
        View rowView = inflater.inflate(R.layout.row, parent, false);


        tvTypes = (TextView) rowView.findViewById(R.id.tvType);
        tvMsg = (TextView) rowView.findViewById(R.id.tvMessage);

        Incident currentIncident = incidents.get(position);
        tvTypes.setText(currentIncident.getType());
        tvMsg.setText(currentIncident.getMessage());

        return rowView;
    }
}
