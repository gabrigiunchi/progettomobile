package com.example.utente.progettomobile;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

/**
 * Created by Utente on 27/05/2016.
 */
public class ProtectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_protection, container, false);

        TableRow t1 = (TableRow) view.findViewById(R.id.tableRow1);
        t1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        TableRow t3 = (TableRow) view.findViewById(R.id.tableRow3);
        t3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
