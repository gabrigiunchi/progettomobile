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
import java.util.Arrays;
import java.util.List;

import model.Category;


public class AddPassFragment extends Fragment {

    private List<Category> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pass, container, false);

        if(list.isEmpty()){
            list.addAll(Arrays.asList(Category.values()));
        }

        final ListView listview = (ListView) view.findViewById(R.id.listView3);
        CategoryAdapter adapter = new CategoryAdapter(getActivity().getApplicationContext(),R.layout.row_view_img, this.list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (list.get(position).getCategoryFullName()){
                    case ("Carta di credito"):
                        Intent intent1 = new Intent(getActivity(), InsertCreditCardActivity.class);
                        intent1.putExtra("modalità","nuova");
                        intent1.putExtra("numero","");
                        startActivity(intent1);
                        break;
                    case ("Conto bancario"):
                        Intent intent2 = new Intent(getActivity(), InsertBankAccountActivity.class);
                        intent2.putExtra("modalità","nuova");
                        intent2.putExtra("nome","");
                        startActivity(intent2);
                        break;
                    case ("Codice fiscale"):
                        Intent intent3 = new Intent(getActivity(), InsertPersonalCodeActivity.class);
                        intent3.putExtra("modalità","nuova");
                        intent3.putExtra("nome","");
                        startActivity(intent3);
                        break;
                    case ("Credenziale di accesso"):
                        Intent intent4 = new Intent(getActivity(), InsertCredentialsActivity.class);
                        intent4.putExtra("modalità","nuova");
                        intent4.putExtra("sito","");
                        startActivity(intent4);
                        break;
                }
            }
        });

        return view;
    }

}
