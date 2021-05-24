package it.unimib.myfitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Profile extends AppCompatActivity {
    private String userID;
    private String name;
    private String surname;
    private String email;
    private String sex;
    private Date date;
    private int age;
    private float weight;
    private int height;
    private String activity_level;
    private float IMC;
    private DatabaseReference databaseReference;
    private TextView profileNameTextView, profileSurnameTextView,textViewemailname;
    private TextView profileSex, profileAge, profileWeight, profileActivityLevel;
    private TextView profileIMC,profileUserID, profileHeight;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView profilePicImageView;
    private FirebaseStorage firebaseStorage;


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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profilePicImageView = findViewById(R.id.user_image);
        profileNameTextView = findViewById(R.id.user_name);
        profileSurnameTextView = findViewById(R.id.user_surname);
        profileIMC = findViewById(R.id.user_IMC);
        profileActivityLevel = findViewById(R.id.user_activityLevel);
        profileAge = findViewById(R.id.user_age);
        profileSex = findViewById(R.id.user_sex);
        profileWeight = findViewById(R.id.user_weight);
        profileHeight = findViewById(R.id.user_heigh);
        profileUserID = findViewById(R.id.user_ID);
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
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                Profile userProfile = dataSnapshot.getValue(Profile.class);
                profileNameTextView.setText(userProfile.getUserName());
                profileSurnameTextView.setText(userProfile.getUserSurname());
                profileActivityLevel.setText(userProfile.getUserActivity_level());
                profileAge.setText(userProfile.getUserAge().toString());
                profileHeight.setText(Integer.toString(userProfile.getUserHeight()));
                profileSex.setText(userProfile.getUserSex());
                profileWeight.setText(Float.toString(userProfile.getUserWeight()));
                profileIMC.setText(Float.toString(userProfile.getIMC()));
                profileUserID.setText(userProfile.getUserID());


                textViewemailname = findViewById(R.id.user_email);
                textViewemailname.setText(user.getEmail());
            }
            @Override
            public void onCancelled( DatabaseError databaseError) {
                Toast.makeText(Profile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
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
    public Profile(){
    }

    public Profile(String userID, String name,String surname, String email, String sex,
                    Date date, float weight, int height, String activity_level) {
        this.userID = userID;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.sex = sex;
        this.date = date;
        this.age = setUserAge(date);
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
        this.IMC = setIMC(sex,age,weight,height);
    }

    public String getUserID(){
        return userID;
    }
    public String getUserName(){
        return name;
    }
    public String getUserSurname(){
        return surname;
    }
    public String getUserEmail(){
        return email;
    }
    public String getUserSex(){
        return sex;
    }
    public Date getUserAge(){
        return date;
    }
    public int setUserAge(Date date){
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar bday = new GregorianCalendar();
        GregorianCalendar bdayThisYear = new GregorianCalendar();

        bday.setTime(date);
        bdayThisYear.setTime(date);
        bdayThisYear.set(Calendar.YEAR, today.get(Calendar.YEAR));

            int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);

            if(today.getTimeInMillis() < bdayThisYear.getTimeInMillis())
                age--;

            return age;
        }
    public float getUserWeight(){
        return weight;
    }
    public int getUserHeight(){
        return height;
    }
    public String getUserActivity_level(){
        return activity_level;
    }
    public float getIMC(){
        return IMC;
    }
    public float setIMC(String sex, int age, float weight, int height){
        return weight / ((height/100)*(height/100));
    }


}