package com.example.utente.progettomobile;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;

import com.dropbox.chooser.android.DbxChooser;

import model.Category;
import model.SynchronizationService;

/**
 * Created by Utente on 28/04/2016.
 */
public class MenuActivity extends AppCompatActivity implements CategoriesFragment.OnFragmentListener, SettingsFragment.OnFragmentListener {

    private SharedPreferences preferences;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.preferences = getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);


        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.tabBarMenu);
        replaceFragment(new CategoriesFragment(), false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btnCategories: replaceFragment(new CategoriesFragment(), false); break;
                    case R.id.btnPref: replaceFragment(new PrefFragment(), false); break;
                    case R.id.btnSettings: replaceFragment(new SettingsFragment(), false); break;
                }
            }
        });

        try{
            Log.d("categoria",getIntent().getStringExtra("categoria") + "");
            for(Category c : Category.values()){
                if(getIntent().getStringExtra("categoria").equals(c.getCategoryFullName())){
                    replaceFragment(ViewCategoryFragment.newInstance(c.getCategoryFullName()), true);
                }
            }
            if(getIntent().getStringExtra("categoria").equals("Pref")){
                replaceFragment(new PrefFragment(), false);
                radioGroup.check(R.id.btnPref);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.checkSettings();
    }

    private void checkSettings() {
        // Recupero il metodo di sincronizzazione scelto dall'utente.
        final String syncMethodString = preferences.getString(getString(R.string.synchronization_method_preferences_entry),
                SynchronizationService.SynchronizationMethod.DISABLED.name());

        final SynchronizationService.SynchronizationMethod synchronizationMethod =
                SynchronizationService.SynchronizationMethod.valueOf(syncMethodString);

        // Se la sincronizzazione è abilitata faccio partire il service
        if(synchronizationMethod != SynchronizationService.SynchronizationMethod.DISABLED) {
            final Intent intent = new Intent(this, SynchronizationService.class);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Se la sincronizzazione in background è disattivata termino il service quando esco dalla MenuActivity.
        final boolean backgroundSynchronizationOn = this.preferences.getBoolean(getString(R.string.synchronizationInBackground), true);
        if(!backgroundSynchronizationOn) {
            stopService(new Intent(this, SynchronizationService.class));
        }
    }

    @Override
    public void categorySelected(Category categoria) {
        replaceFragment(ViewCategoryFragment.newInstance(categoria.getCategoryFullName()), true);
    }

    @Override
    public void onFragmentSubmitted2(String impostazione) {
        switch (impostazione) {
            case "Account 1Password" :
                this.currentFragment = new AccountFragment();
                break;
            case "Protezione" :
                this.currentFragment = new ProtectionFragment();
                break;
            case "Sincronizzazione" :
                this.currentFragment = new SincroFragment();
                break;
        }
        this.replaceFragment(this.currentFragment, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.currentFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void replaceFragment(Fragment fragment, boolean back) {
        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

}
