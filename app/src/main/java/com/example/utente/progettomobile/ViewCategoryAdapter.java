package com.example.utente.progettomobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import model.interfaces.Element;
import model.interfaces.ElementMetadata;

/**
 * Created by Utente on 20/04/2016.
 */
public class ViewCategoryAdapter extends ArrayAdapter<ElementMetadata> {
    private LayoutInflater inflater;
    Context context;
    int resource;
    List<ElementMetadata> list = null;

    public ViewCategoryAdapter(Context context, int index, List<ElementMetadata> list){
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
            Element e = getItem(position).getElement();
            String c = e.getTitle();
            text.setText(c);
        }

        return convertView;
    }
}
