package it.unimib.myfitapp.ui.calories;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.unimib.myfitapp.ProfileActivity;
import it.unimib.myfitapp.R;
import it.unimib.myfitapp.UserInformation;

public class CaloriesFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    PieChart pieChart;
    PieData pieData;
    List<PieEntry> pieEntryList = new ArrayList<>();
    Button addCalories;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calories, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://myfitapp-a5b2b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();

        drawPieChart(root);


        Button btn = (Button) root.findViewById(R.id.button_add_calories);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View alertLayout = inflater.inflate(R.layout.layout_custum_dialog_add_calories, null);
                final EditText etCalFood = alertLayout.findViewById(R.id.et_calFromFood);
                final EditText etCalPer100 = alertLayout.findViewById(R.id.et_calPer100);

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle(getResources().getString(R.string.add_calories_consumed));
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
                        String foodName = etCalFood.getText().toString().trim();
                        int cal = Integer.parseInt(etCalPer100.getText().toString().trim());
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        databaseReference.child(user.getUid()).child("calories").child(foodName).setValue(foodName);
                        databaseReference.child(user.getUid()).child("calories").child(foodName).child("numCal").setValue(cal);
                        etCalFood.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        etCalPer100.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
        return root;
    }

    public void drawPieChart(View v) {
        pieChart = (PieChart) v.findViewById(R.id.pieChart_calories);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);
        pieEntryList.add(new PieEntry(10, getResources().getString(R.string.food)));
        pieEntryList.add(new PieEntry(5, getResources().getString(R.string.excercize)));


        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setValueTextSize(14f);
        //pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        /*Description description = new Description();
        description.setText(getString(R.string.calories));
        pieChart.setDescription(description);*/

        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.animateY(500);
    }
}