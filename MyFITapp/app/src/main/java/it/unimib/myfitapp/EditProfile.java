package it.unimib.myfitapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.Editable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{

    Spinner SignUpSex, SignUpActivity_level;
    Button SignUpButton;

    private static final String TAG = EditProfile.class.getSimpleName();
    Button btnsave;
    private FirebaseAuth firebaseAuth;
    private TextView textViewemailname;
    private DatabaseReference databaseReference;
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextUser_ID;
    private EditText editTextDate;
    private EditText editTextWeigth;
    private EditText editTextHeigh;
    private Spinner spinnerSex;
    private Spinner spinnerActivity_level;
    private ImageView profileImageView;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextName = (EditText)findViewById(R.id.input_name);
        editTextSurname = (EditText)findViewById(R.id.input_surname);
        editTextUser_ID = (EditText)findViewById(R.id.input_user_ID);
        editTextDate = (EditText)findViewById(R.id.input_date);
        editTextWeigth = (EditText)findViewById(R.id.input_weight);
        editTextHeigh = (EditText)findViewById(R.id.input_height);
        spinnerSex = (Spinner)findViewById(R.id.input_sex);
        spinnerActivity_level = (Spinner)findViewById(R.id.input_activityLevel);

        btnsave = (Button)findViewById(R.id.button_confirm);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        btnsave.setOnClickListener(this);

        textViewemailname=(TextView)findViewById(R.id.view_email);
        textViewemailname.setText(user.getEmail());

        profileImageView = findViewById(R.id.addPhotoImage);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent();
                profileIntent.setType("image/*");
                profileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
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

    public EditProfile() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void userInformation() throws ParseException {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String userId = editTextUser_ID.getText().toString().trim();
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(editTextDate.getText().toString());
        float weight = Float.valueOf(editTextWeigth.getText().toString());
        int heigh = Integer.valueOf(editTextHeigh.getText().toString());
        String sex = spinnerSex.toString().trim();
        String activityLevel = spinnerActivity_level.toString().trim();

        String email = textViewemailname.getText().toString().trim();

        Profile profile = new Profile(name, surname, email, userId, sex, date, weight, heigh, activityLevel);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(profile);
        Toast.makeText(getApplicationContext(),"User information updated",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnsave){
            if (imagePath == null) {

                Drawable drawable = this.getResources().getDrawable(R.drawable.ic_baseline_account_circle_24);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_baseline_account_circle_24);

                // openSelectProfilePictureDialog();
                try {
                    userInformation();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // sendUserData();
                finish();
                startActivity(new Intent(EditProfile.this, MainActivity.class));
            }
            else {
                try {
                    userInformation();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sendUserData();
                finish();
                startActivity(new Intent(EditProfile.this, MainActivity.class));
            }
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Get "User UID" from Firebase > Authentification > Users.
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic"); //User id/Images/Profile Pic.jpg
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Error: Uploading profile picture", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfile.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openSelectProfilePictureDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        TextView title = new TextView(this);
        title.setText("Profile Picture");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);
        TextView msg = new TextView(this);
        msg.setText("Please select a profile picture \n Tap the sample avatar logo");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });
        new Dialog(getApplicationContext());
        alertDialog.show();
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.BLUE);
        okBT.setLayoutParams(neutralBtnLP);
    }

}