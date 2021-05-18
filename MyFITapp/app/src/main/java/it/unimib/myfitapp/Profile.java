package it.unimib.myfitapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.options_menu_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                // definisco l'intenzione di aprire l'Activity "Page1.java"
                Intent openPage1 = new Intent(Profile.this, EditProfile.class);
                // passo all'attivazione dell'activity page1.java
                startActivity(openPage1);
                break;
            case R.id.action_options:
                // definisco l'intenzione di aprire l'Activity "Page2.java"
                Intent openPage2 = new Intent(Profile.this, Settings.class);
                // passo all'attivazione dell'activity page2.java
                startActivity(openPage2);
                break;


            case android.R.id.home:
                //finish();
                //return true;
                Intent openPage3 = new Intent(Profile.this, MainActivity.class);
                // passo all'attivazione dell'activity page2.java
                startActivity(openPage3);
                break;

        }

        return false;
    }

}