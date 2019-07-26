package com.example.utente.progettomobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import model.interfaces.Element;

/**
 * Created by Utente on 25/04/2016.
 */
public class PrefAdapter extends ArrayAdapter<Element> {
    private LayoutInflater inflater;
    Context context;
    int resource;
    List<Element> list = null;

    public PrefAdapter(Context context, int index, List<Element> list){
        super(context,index,list);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.resource = index;
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = inflater.inflate(resource, parent, false);
        }
        if(!isEmpty()) {
            TextView text = (TextView) convertView.findViewById(R.id.textView4);
            Element e = getItem(position);
            String titolo = e.getTitle();
            String categoria = e.getCategory().getCategoryFullName();
            text.setText(categoria+": "+titolo);
        }

        return convertView;
    }
}