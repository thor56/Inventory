package com.example.inventory;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ShowNameList extends ArrayAdapter<NameList> {
    private Activity context;
    List<NameList> namelists;
    public ShowNameList(Activity context, List<NameList> namelists) {
        super(context, R.layout.layout_name_list, namelists);
        this.context = context; this.namelists = namelists;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_name_list, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        NameList namelist = namelists.get(position);
        textViewName.setText(namelist.getName());
        return listViewItem;
    }
}