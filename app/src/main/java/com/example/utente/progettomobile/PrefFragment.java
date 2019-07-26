package com.example.utente.progettomobile;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseManagerImpl;
import model.Category;
import model.MyList;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

/**
 * Created by Utente on 19/05/2016.
 */
public class PrefFragment extends Fragment {

    private List<Element> list = new ArrayList<>();
    private List<ElementMetadata> fullList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pref, container, false);

        this.list = DatabaseManagerImpl.getDatabaseManager(view.getContext()).getFavoritesElements();
        if(this.list.size()==0){
            view.findViewById(R.id.imageView13).setVisibility(View.VISIBLE);
        }
        this.fullList = MyList.getList(view.getContext());
        final ListView listview = (ListView) view.findViewById(R.id.listView3);
        PrefAdapter adapter = new PrefAdapter(getActivity().getApplicationContext(),R.layout.row_view,this.list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Category c = list.get(position).getCategory();
                String category = list.get(position).getCategory().getCategoryFullName();
                Intent intent = new Intent();
                switch (category) {
                    case "Carta di credito":
                        intent = new Intent(getActivity(),ViewCreditCardActivity.class);
                        break;
                    case "Conto bancario":
                        intent = new Intent(getActivity(),ViewBankAccountActivity.class);
                        break;
                    case "Codice fiscale":
                        intent = new Intent(getActivity(),ViewPersonalCodeActivity.class);
                        break;
                    case "Credenziale di accesso":
                        intent = new Intent(getActivity(),ViewCredentialsActivity.class);
                        break;

                }
                intent.putExtra("id", fullList.get(position).getID());
                intent.putExtra("onBackPressed", "preferiti");
                startActivity(intent);
            }
        });

        return view;
    }

}
