package com.example.utente.progettomobile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import model.Setting;


public class SettingsFragment extends Fragment {

    public interface OnFragmentListener {
        void onFragmentSubmitted2(String impostazione);
    }

    private OnFragmentListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentListener)
            setListener((OnFragmentListener) activity);
    }

    public void setListener(OnFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final List<Setting> list = Arrays.asList(Setting.values());

        final ListView listview = (ListView) view.findViewById(R.id.listView4);
        SettingAdapter adapter = new SettingAdapter(getActivity().getApplicationContext(),R.layout.row_view_img, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    listener.onFragmentSubmitted2(list.get(position).getCategoryFullName());
                }
            }
        });

        return view;
    }

}
