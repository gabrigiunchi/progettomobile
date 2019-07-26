package com.example.utente.progettomobile;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Category;
import model.MyList;
import model.interfaces.ElementMetadata;


public class ViewCategoryFragment extends Fragment {

    private List<ElementMetadata> list = new ArrayList<>();
    private static final String CATEGORIA = "categoria";
    private String category;

    public static ViewCategoryFragment newInstance(String categoria) {
        ViewCategoryFragment viewCategoryFragment = new ViewCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORIA, categoria);
        viewCategoryFragment.setArguments(bundle);
        return viewCategoryFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_view_category, container, false);
        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.category = bundle.getString(CATEGORIA, "");
            if(this.category!="Credenziale di accesso")
                ((TextView) view.findViewById(R.id.textView6)).append((this.category).toUpperCase());
            else
                ((TextView) view.findViewById(R.id.textView6)).append((this.category));

            for(Category c : Category.values()){
                if(c.getCategoryFullName().equals(this.category))
                    ((ImageView) view.findViewById(R.id.imageView17)).setImageResource(c.getImageID());
            }
        }

        this.list = MyList.getList(view.getContext());
        Category c = Category.cartaCredito;

        switch (this.category) {
            case "Carta di credito": break;
            case "Conto bancario": c = Category.contoBancario; break;
            case "Codice fiscale": c = Category.codiceFiscale; break;
            case "Credenziale di accesso": c = Category.credenzialeAccesso; break;
        }
        final List<ElementMetadata> tempList = new ArrayList<>();

        for(ElementMetadata s : this.list) {
            if(s.getElement().getCategory() != c) {
                tempList.add(s);
            }
        }
        this.list.removeAll(tempList);
        if(this.list.size()==0)
            view.findViewById(R.id.imageView18).setVisibility(View.VISIBLE);

        final ListView listview = (ListView) view.findViewById(R.id.listView2);
        final ViewCategoryAdapter adapter = new ViewCategoryAdapter(getActivity().getApplicationContext(),R.layout.row_view,this.list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
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

                    default: break;
                }

                final Bundle bundle = new Bundle();
                bundle.putParcelable("element", list.get(position));
                intent.putExtra("extra", bundle);
                startActivity(intent);
            }
        });


        return view;
    }

}
