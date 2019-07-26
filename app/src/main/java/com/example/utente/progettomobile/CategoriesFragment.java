package com.example.utente.progettomobile;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import model.Category;


public class CategoriesFragment extends Fragment {

    public interface OnFragmentListener {
        void categorySelected(Category categoria);
    }

    private OnFragmentListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentListener) {
            setListener((OnFragmentListener) activity);
        }
    }

    public void setListener(OnFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("applicazione", "CategoriesFragment");
        final View view = inflater.inflate(R.layout.fragment_categories, container, false);

        final ImageButton addButton = (ImageButton) view.findViewById(R.id.imageButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new AddPassFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        final ListView listview = (ListView) view.findViewById(R.id.listView);
        final List<Category> categories = Arrays.asList(Category.values());
        CategoryAdapter adapter = new CategoryAdapter(getActivity().getApplicationContext(),R.layout.row_view_img, categories);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null)
                    listener.categorySelected(categories.get(position));
            }
        });

        return view;
    }

}
