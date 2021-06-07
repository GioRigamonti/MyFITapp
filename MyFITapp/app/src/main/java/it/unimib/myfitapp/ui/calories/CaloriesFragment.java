package it.unimib.myfitapp.ui.calories;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import it.unimib.myfitapp.R;

public class CaloriesFragment extends Fragment {
    PieChart pieChart;
    PieData pieData;
    List<PieEntry> pieEntryList = new ArrayList<>();

    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;

    private CaloriesViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(CaloriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calories, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        drawPieChart(root);
        lineChart = lineChart.findViewById(R.id.lineChart);
        //getEntries();
        lineDataSet = new LineDataSet(lineEntries, "");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(18f);

        return root;
    }


    /*private void getEntries() {
        lineEntries = new ArrayList<>();
        lineEntries.add(new Entry(2f, 0));
        lineEntries.add(new Entry(4f, 1));
        lineEntries.add(new Entry(6f, 1));
        lineEntries.add(new Entry(8f, 3));
        lineEntries.add(new Entry(7f, 4));
        lineEntries.add(new Entry(3f, 3));
    }*/

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