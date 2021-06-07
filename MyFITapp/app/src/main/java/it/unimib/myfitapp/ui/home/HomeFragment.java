package it.unimib.myfitapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import it.unimib.myfitapp.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private int progr = 0;
    PieChart pieChart;
    PieData pieData;
    List<PieEntry> pieEntryList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

                
        /*final TextView textView = root.findViewById(R.id.text_view_progress_num);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/


        pieChart = (PieChart)root.findViewById(R.id.pieChart_allActivities);
        pieChart.setUsePercentValues(true);
        pieEntryList.add(new PieEntry(10,getResources().getString(R.string.walking)));
        pieEntryList.add(new PieEntry(5,getResources().getString(R.string.sitting)));
        pieEntryList.add(new PieEntry(7,getResources().getString(R.string.standing)));
        pieEntryList.add(new PieEntry(3,getResources().getString(R.string.jogging)));
        pieEntryList.add(new PieEntry(3,getResources().getString(R.string.upstairs)));
        pieEntryList.add(new PieEntry(3,getResources().getString(R.string.downstairs)));

        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        pieDataSet.setValueTextSize(12f);
        //pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE,Color.CYAN, Color.MAGENTA );
        Description description = new Description();
        description.setText(getString(R.string.activity));
        pieChart.setDescription(description);
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.animateY(5000);
        return root;
    }

}