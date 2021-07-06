package it.unimib.myfitapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import it.unimib.adl_library.*;

import android.widget.Toast;


public class ConfidenceActivity extends AppCompatActivity {
    private static final String TAG = "ConfidenceActivity";
    public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*map = (HashMap<String, Float>) intent.getSerializableExtra("confidence");*/
            /*index = intent.getStringExtra("label");
            downstairsTextView.setText(index);*/
            HashMap<String, Float> activitiesProbabilitiesMap = (HashMap<String, Float>) intent.getSerializableExtra("map");

            Log.d(TAG, activitiesProbabilitiesMap.toString());

            downstairsTextView.setText(String.valueOf(activitiesProbabilitiesMap.get("stairs down")));
            joggingTextView.setText(String.valueOf(activitiesProbabilitiesMap.get("jogging")));
            sittingTextView.setText(String.valueOf(activitiesProbabilitiesMap.get("sitting")));
            standingTextView.setText(String.valueOf(activitiesProbabilitiesMap.get("standing")));
            upstairsTextView.setText(String.valueOf(activitiesProbabilitiesMap.get("stairs up")));
            walkingTextView.setText(String.valueOf(activitiesProbabilitiesMap.get("walking")));
        }
    };

    private TextView downstairsTextView;
    private TextView joggingTextView;
    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView upstairsTextView;
    private TextView walkingTextView;

    private TableRow downstairsTableRow;
    private TableRow joggingTableRow;
    private TableRow sittingTableRow;
    private TableRow standingTableRow;
    private TableRow upstairsTableRow;
    private TableRow walkingTableRow;

    private String index;

    SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confidence);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent i = new Intent(ConfidenceActivity.this, BackgroundAccelerometerService.class);
        startService(i);

        downstairsTextView = (TextView) findViewById(R.id.downstairs_prob);
        joggingTextView = (TextView) findViewById(R.id.jogging_prob);
        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
        standingTextView = (TextView) findViewById(R.id.standing_prob);
        upstairsTextView = (TextView) findViewById(R.id.upstairs_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);

        downstairsTableRow = (TableRow) findViewById(R.id.downstairs_row);
        joggingTableRow = (TableRow) findViewById(R.id.jogging_row);
        sittingTableRow = (TableRow) findViewById(R.id.sitting_row);
        standingTableRow = (TableRow) findViewById(R.id.standing_row);
        upstairsTableRow = (TableRow) findViewById(R.id.upstairs_row);
        walkingTableRow = (TableRow) findViewById(R.id.walking_row);

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

    private void setProbabilities(HashMap<String, Float> activitiesProbabilitiesMap) {
        setRowsColor(index);
        if (activitiesProbabilitiesMap != null) {

            if (activitiesProbabilitiesMap.get("stairs down") != null) {
                downstairsTextView.setText(String.valueOf(round(activitiesProbabilitiesMap.get("stairs down"), 2)));
            }

            if (activitiesProbabilitiesMap.get("jogging") != null) {
                joggingTextView.setText(String.valueOf(round(activitiesProbabilitiesMap.get("jogging"), 2)));
            }

            if (activitiesProbabilitiesMap.get("sitting") != null) {
                sittingTextView.setText(String.valueOf(round(activitiesProbabilitiesMap.get("sitting"), 2)));
            }

            if (activitiesProbabilitiesMap.get("standing") != null) {
                standingTextView.setText(String.valueOf(round(activitiesProbabilitiesMap.get("standing"), 2)));;
            }

            if (activitiesProbabilitiesMap.get("stairs up") != null) {
                upstairsTextView.setText(String.valueOf(round(activitiesProbabilitiesMap.get("stairs up"), 2)));
            }

            if (activitiesProbabilitiesMap.get("walking") != null) {
                walkingTextView.setText(String.valueOf(round(activitiesProbabilitiesMap.get("walking"), 2)));
            }
        }
    }

    private void setRowsColor(String idx) {
        /*downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));*/

        if (idx != null) {
            if (idx.equalsIgnoreCase("stairs down"))
                downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.ic_launcher_myfitapp_background, null));
            else if (idx.equalsIgnoreCase("jogging"))
                joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.ic_launcher_myfitapp_background, null));
            else if (idx.equalsIgnoreCase("sitting"))
                sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.ic_launcher_myfitapp_background, null));
            else if (idx.equalsIgnoreCase("standing"))
                standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.ic_launcher_myfitapp_background, null));
            else if (idx.equalsIgnoreCase("stairs up"))
                upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.ic_launcher_myfitapp_background, null));
            else if (idx.equalsIgnoreCase("walking"))
                walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.ic_launcher_myfitapp_background, null));
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    protected void onStart() {
        super.onStart();
        mSensorsValuesBroadcastReceiver = new SensorsValuesBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("it.unimib.myservice");
        registerReceiver(mSensorsValuesBroadcastReceiver, intentFilter);
    }

    protected void onStop() {
        super.onStop();
        unregisterReceiver(mSensorsValuesBroadcastReceiver);
    }
}