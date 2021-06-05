package it.unimib.myfitapp;

import android.app.DatePickerDialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
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
    private static final String TAG = EditProfile.class.getSimpleName();

    private TextView textViewDate;
    Button DateOfBirthButton;
    DatePicker picker;

    Button btnsave;
    private FirebaseAuth firebaseAuth;
    private TextView textViewemailname;
    private DatabaseReference databaseReference;
    private EditText editTextName;
    private EditText editTextSurname;
    private ImageView profileImageView;
    private FirebaseStorage firebaseStorage;
    private EditText editHeight;
    private EditText editWeight;
    //private EditText editDate;
    private Spinner editTextSex;
    private Spinner editTextActivity_level;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;

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

        textViewDate = (TextView)findViewById(R.id.input_date_TextView);
        picker = (DatePicker)findViewById(R.id.datePicker);
        DateOfBirthButton = (Button)findViewById(R.id.calendarButton);
        DateOfBirthButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClickedCalendar(v);
            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://myfitapp-a5b2b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        editTextName = (EditText)findViewById(R.id.input_name);
        editTextSurname = (EditText)findViewById(R.id.input_surname);
        editTextSex = (Spinner)findViewById(R.id.input_sex);
        editTextActivity_level = (Spinner)findViewById(R.id.input_activityLevel);
        editHeight = (EditText)findViewById(R.id.input_height);
        editWeight = (EditText)findViewById(R.id.input_weight);
        FirebaseUser user=firebaseAuth.getCurrentUser();

        btnsave=(Button)findViewById(R.id.button_confirm);
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


    private void userInformation() throws ParseException {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String email = textViewemailname.getText().toString().trim();
        String sex = editTextSex.getSelectedItem().toString();
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(textViewDate.getText().toString());
        int height = Integer.parseInt(editHeight.getText().toString());
        float weight = Float.parseFloat(editWeight.getText().toString());
        String activity_level = editTextActivity_level.getSelectedItem().toString();
        UserInformation userinformation = new UserInformation(name,surname,email, sex, date,weight,height,activity_level);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userinformation);
        Toast.makeText(getApplicationContext(),"User information updated",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnsave){
            if (imagePath == null) {
                Drawable drawable = this.getResources().getDrawable(R.drawable.ic_baseline_account_circle_24);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_baseline_account_circle_24);

                openSelectProfilePictureDialog();
                try {
                    userInformation();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                sendUserData();
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

    private void sendUserData(){
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

    public void buttonClickedCalendar(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_caldendar, null);
        final DatePicker dateOfBirth = alertLayout.findViewById(R.id.datePicker);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.scegli_data));
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
                textViewDate.setText(dateOfBirth.getDayOfMonth()+"/"+ (dateOfBirth.getMonth() + 1)+"/"+dateOfBirth.getYear());
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}