package it.unimib.myfitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    private DatabaseReference databaseReference;
    private TextView profileNameTextView, profileSurnameTextView,textViewemailname;
    private TextView profileSex, profileAge, profileWeight, profileActivityLevel;
    private TextView profileIMC, profileHeight;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView profilePicImageView;
    private FirebaseStorage firebaseStorage;
    Button LogoutButton;
    Button DeleteAccountButton;

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
        databaseReference = FirebaseDatabase.getInstance("https://myfitapp-a5b2b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        profilePicImageView = findViewById(R.id.user_image);
        profileNameTextView = findViewById(R.id.user_name);
        profileSurnameTextView = findViewById(R.id.user_surname);
        profileIMC = findViewById(R.id.user_IMC);
        profileActivityLevel = findViewById(R.id.user_activityLevel);
        profileAge = findViewById(R.id.user_age);
        profileSex = findViewById(R.id.user_sex);
        profileWeight = findViewById(R.id.user_weight);
        profileHeight = findViewById(R.id.user_heigh);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference();
        // Get the image stored on Firebase via "User id/Images/Profile Pic.jpg".
        storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Using "Picasso" (http://square.github.io/picasso/) after adding the dependency in the Gradle.
                // ".fit().centerInside()" fits the entire image into the specified area.
                // Finally, add "READ" and "WRITE" external storage permissions in the Manifest.
                Picasso.get().load(uri).fit().centerInside().into(profilePicImageView);
            }
        });
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userProfile = dataSnapshot.getValue(UserInformation.class);
                profileNameTextView.setText(userProfile.getUserName());
                profileSurnameTextView.setText(userProfile.getUserSurname());
                profileActivityLevel.setText(userProfile.getUserActivity_level());
                profileAge.setText(userProfile.getUserDate().toString());
                profileHeight.setText(Integer.toString(userProfile.getUserHeight()));
                profileSex.setText(userProfile.getUserSex());
                profileWeight.setText(Float.toString(userProfile.getUserWeight()));
                profileIMC.setText(Float.toString(userProfile.getIMC()));


                textViewemailname = findViewById(R.id.user_email);
                textViewemailname.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        LogoutButton = (Button) findViewById(R.id.button_logout);

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateLogOut(v);
            }
        });
        DeleteAccountButton = (Button) findViewById(R.id.button_delete_account);

        DeleteAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClickedDelete(v);
            }
        });
        
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
            case R.id.action_options:
                // definisco l'intenzione di aprire l'Activity "Page2.java"
                Intent openPage1 = new Intent(Profile.this, Settings.class);
                // passo all'attivazione dell'activity page2.java
                startActivity(openPage1);
                break;


            case android.R.id.home:
                //finish();
                //return true;
                Intent openPage2 = new Intent(Profile.this, MainActivity.class);
                // passo all'attivazione dell'activity page2.java
                startActivity(openPage2);
                break;

                case R.id.confidence:
                // definisco l'intenzione di aprire l'Activity "Page2.java"
                Intent openPage3 = new Intent(Profile.this, Settings.class);
                // passo all'attivazione dell'activity page2.java
                startActivity(openPage3);
                break;

        }

        return false;
    }

    public void navigateLogOut(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void buttonClickedEditName(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_name, null);
        final EditText etName = alertLayout.findViewById(R.id.et_name);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.edit_name));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.ok_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString().trim();
                String surname = profileSurnameTextView.getText().toString().trim();
                String email = textViewemailname.getText().toString().trim();
                String sex = profileSex.getText().toString().trim();
                int age = Integer.parseInt(profileAge.getText().toString().trim());
                int height = Integer.parseInt(profileHeight.getText().toString().trim());
                float weight = Float.parseFloat(profileWeight.getText().toString().trim());
                String activity_level = profileActivityLevel.getText().toString().trim();
                UserInformation userinformation = new UserInformation(name, surname, email, sex,
                        age, weight, height, activity_level);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference.child(user.getUid()).setValue(userinformation);
                databaseReference.child(user.getUid()).setValue(userinformation);
                etName.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickedEditSurname(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_surname, null);
        final EditText etSurname = alertLayout.findViewById(R.id.et_surname);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.edit_surname));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.ok_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = profileNameTextView.getText().toString().trim();
                String surname = etSurname.getText().toString().trim();
                String email = textViewemailname.getText().toString().trim();
                String sex = profileSex.getText().toString().trim();
                int age = Integer.parseInt(profileAge.getText().toString().trim());
                int height = Integer.parseInt(profileHeight.getText().toString().trim());
                float weight = Float.parseFloat(profileWeight.getText().toString().trim());
                String activity_level = profileActivityLevel.getText().toString().trim();
                UserInformation userinformation = new UserInformation(name, surname, email, sex,
                        age, weight, height, activity_level);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference.child(user.getUid()).setValue(userinformation);
                databaseReference.child(user.getUid()).setValue(userinformation);
                etSurname.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
    public void buttonClickedEditWeight(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_weight, null);
        final EditText etWeight = alertLayout.findViewById(R.id.et_weight);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.edit_weight));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.ok_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = profileNameTextView.getText().toString().trim();
                String surname = profileSurnameTextView.getText().toString().trim();
                String email = textViewemailname.getText().toString().trim();
                String sex = profileSex.getText().toString().trim();
                int age = Integer.parseInt(profileAge.getText().toString().trim());
                int height = Integer.parseInt(profileHeight.getText().toString().trim());
                float weight = Float.parseFloat(etWeight.getText().toString().trim());
                String activity_level = profileActivityLevel.getText().toString().trim();
                UserInformation userinformation = new UserInformation(name, surname, email, sex,
                        age, weight, height, activity_level);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference.child(user.getUid()).setValue(userinformation);
                databaseReference.child(user.getUid()).setValue(userinformation);
                etWeight.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
    public void buttonClickedEditHeight(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_height, null);
        final EditText etHeight = alertLayout.findViewById(R.id.et_height);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.edit_height));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.ok_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = profileNameTextView.getText().toString().trim();
                String surname = profileSurnameTextView.getText().toString().trim();
                String email = textViewemailname.getText().toString().trim();
                String sex = profileSex.getText().toString().trim();
                int age = Integer.parseInt(profileAge.getText().toString().trim());
                int height = Integer.parseInt(etHeight.getText().toString().trim());
                float weight = Float.parseFloat(profileWeight.getText().toString().trim());
                String activity_level = profileActivityLevel.getText().toString().trim();
                UserInformation userinformation = new UserInformation(name, surname, email, sex,
                        age, weight, height, activity_level);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference.child(user.getUid()).setValue(userinformation);
                databaseReference.child(user.getUid()).setValue(userinformation);
                etHeight.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
    public void buttonClickedEditActivityLevel(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_activity_level, null);
        final EditText etActivityLevel = alertLayout.findViewById(R.id.et_activity_level);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.edit_activity_level));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.ok_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = profileNameTextView.getText().toString().trim();
                String surname = profileSurnameTextView.getText().toString().trim();
                String email = textViewemailname.getText().toString().trim();
                String sex = profileSex.getText().toString().trim();
                int age = Integer.parseInt(profileAge.getText().toString().trim());
                int height = Integer.parseInt(profileHeight.getText().toString().trim());
                float weight = Float.parseFloat(profileWeight.getText().toString().trim());
                String activity_level = etActivityLevel.getText().toString().trim();
                UserInformation userinformation = new UserInformation(name, surname, email, sex,
                        age, weight, height, activity_level);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference.child(user.getUid()).setValue(userinformation);
                databaseReference.child(user.getUid()).setValue(userinformation);
                etActivityLevel.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickedDelete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.deleteAccount));
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.ok_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser();
                Intent openPage = new Intent(Profile.this, Login.class);
                startActivity(openPage);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


    public void deleteUser() {
        // [START delete_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child(user.getUid()).removeValue();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
        // [END delete_user]
    }


}
