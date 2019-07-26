package com.example.utente.progettomobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import model.Setting;

/**
 * Created by Utente on 27/05/2016.
 */
public class SettingAdapter extends ArrayAdapter<Setting> {

    private LayoutInflater inflater;
    Context context;
    int resource;
    List<Setting> list;

    public SettingAdapter(Context context, int index, List<Setting> l){
        super(context,index,l);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.resource = index;
        this.list = l;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = inflater.inflate(resource, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.textView7);
        text.setText(getItem(position).getCategoryFullName());
        ImageView img = (ImageView) convertView.findViewById(R.id.iconCategory);
        img.setImageResource(getItem(position).getImageID());
        return convertView;
    }
}
