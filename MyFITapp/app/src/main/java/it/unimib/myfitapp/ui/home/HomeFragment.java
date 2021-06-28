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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

    private static final int MAX_X_VALUE = 24;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 0;
    private static final String WALKING = "walking";
    private static final String JOGGING = "jogging";
    private static final String STANDING = "standing";
    private static final String SITTING = "sitting";
    private static final String UPSTAIRS = "upstairs";
    private static final String DOWNSTAIRS = "downstairs";
    private static final String SET_LABEL = "Set ABC";
    private BarChart chart;

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
        drawPieChart(root);

        chart = (BarChart)root.findViewById(R.id.stackedbarchart_allActivities);
        BarData data = createChartData();
        //configureChartAppearance();
        prepareChartData(data);

        return root;

    }


    public void drawPieChart(View v) {
        pieChart = (PieChart) v.findViewById(R.id.pieChart_allActivities);
        pieChart.setUsePercentValues(true);
        pieEntryList.add(new PieEntry(10, getResources().getString(R.string.walking)));
        pieEntryList.add(new PieEntry(5, getResources().getString(R.string.sitting)));
        pieEntryList.add(new PieEntry(7, getResources().getString(R.string.standing)));
        pieEntryList.add(new PieEntry(3, getResources().getString(R.string.jogging)));
        pieEntryList.add(new PieEntry(3, getResources().getString(R.string.upstairs)));
        pieEntryList.add(new PieEntry(3, getResources().getString(R.string.downstairs)));
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setColors(getResources().getColor(R.color.primaryLightColor_blue), getResources().getColor(R.color.rose2), getResources().getColor(R.color.secondaryColor_cobalto), getResources().getColor(R.color.rose1), getResources().getColor(R.color.primaryColor_blue),  getResources().getColor(R.color.ic_launcher_myfitapp_background));
        //Description description = new Description();
        //description.setText(getString(R.string.activity));
        //pieChart.setDescription(description);
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.animateY(500);
    }

    private void configureChartAppearance() {
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(false);

        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < MAX_X_VALUE; i++) {
            float value1 = Math.max(MIN_Y_VALUE, (float) Math.random() * (MAX_Y_VALUE + 1));
            float value2 = Math.max(MIN_Y_VALUE, (float) Math.random() * (MAX_Y_VALUE + 1));
            float value3 = Math.max(MIN_Y_VALUE, (float) Math.random() * (MAX_Y_VALUE + 1));
            float value4 = Math.max(MIN_Y_VALUE, (float) Math.random() * (MAX_Y_VALUE + 1));
            float value5 = Math.max(MIN_Y_VALUE, (float) Math.random() * (MAX_Y_VALUE + 1));
            float value6 = Math.max(MIN_Y_VALUE, (float) Math.random() * (MAX_Y_VALUE + 1));
            values.add(new BarEntry(i, new float[]{value1, value2, value3, value4, value5, value6}));
        }

        BarDataSet set1 = new BarDataSet(values, SET_LABEL);

        set1.setColors(new int[] {getResources().getColor(R.color.primaryLightColor_blue), getResources().getColor(R.color.rose2), getResources().getColor(R.color.secondaryColor_cobalto), getResources().getColor(R.color.rose1), getResources().getColor(R.color.primaryColor_blue),  getResources().getColor(R.color.ic_launcher_myfitapp_background)});
        set1.setStackLabels(new String[] {WALKING, SITTING,  STANDING, JOGGING,UPSTAIRS, DOWNSTAIRS});

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        return data;
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(12f);
        chart.setData(data);
        chart.invalidate();
    }


}





