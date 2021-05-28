package it.unimib.myfitapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private boolean logged = false;
    private EditText SignInMail, SignInPass;
    private FirebaseAuth auth;
    private Button SignInButton, ForgetPasswordButton, NotRegisteredButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SignInMail = (EditText) findViewById(R.id.input_email_login);
        SignInPass = (EditText) findViewById(R.id.user_password_text_login);
        SignInButton = (Button) findViewById(R.id.button_confirm);
        ForgetPasswordButton = (Button) findViewById(R.id.button_forgetPassword);
        NotRegisteredButton = (Button)findViewById(R.id.button_goToSignUp);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = SignInMail.getText().toString();
                final String password = SignInPass.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your mail address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 8) {
                                        Toast.makeText(getApplicationContext(),"Password must be more than 8 digit",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        ForgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigateForgetMyPassword(v);
            }
        });
        NotRegisteredButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigateSignUp(v);
            }
        });
    }

    public void NavigateForgetMyPassword(View v) {
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }
    public void NavigateSignUp(View v) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }


}

