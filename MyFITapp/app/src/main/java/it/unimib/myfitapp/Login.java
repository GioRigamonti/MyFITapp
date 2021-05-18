package it.unimib.myfitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Login extends AppCompatActivity {
    private boolean logged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button btnHome =(Button)findViewById(R.id.login);
        btnHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if (logged){
                    // definisco l'intenzione
                    Intent openPage1 = new Intent(Login.this,MainActivity.class);
                    logged = true;
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(openPage1);}
                else{
                    // definisco l'intenzione
                    Intent openPage = new Intent(Login.this,Profile.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(openPage);}

            }

        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

    public boolean onCreateOptionsMenu(Menu menu) {
            return true;
        }

}

