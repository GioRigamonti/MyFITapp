package it.unimib.myfitapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import it.unimib.adl_library.*;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.widget.Toast;


public class ConfidenceActivity extends AppCompatActivity {
    private Map<String, Float> map = new HashMap<>();
    private String index;

    public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            map = (Map<String, Float>) intent.getSerializableExtra("confidence");
            index = intent.getStringExtra("label");
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


    private SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confidence);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        Intent i = new Intent(ConfidenceActivity.this, BackgroundAccelerometerService.class);
        startService(i);
        try {
            setProbabilities();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void setProbabilities() throws Exception {
        setRowsColor(index);
        //setRowsColor("stairs down");
        downstairsTextView.setText(Float.toString(round(map.get("stairs down"), 2)));
        joggingTextView.setText(Float.toString(round(map.get("jogging"), 2)));
        sittingTextView.setText(Float.toString(round(map.get("sitting"), 2)));
        standingTextView.setText(Float.toString(round(map.get("standing"), 2)));
        upstairsTextView.setText(Float.toString(round(map.get("stairs up"), 2)));
        walkingTextView.setText(Float.toString(round(map.get("walking"), 2)));
    }

    private void setRowsColor(String idx) {
        /*downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));*/

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

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorsValuesBroadcastReceiver = new SensorsValuesBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("it.unimib.myfitapp");
        registerReceiver(mSensorsValuesBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mSensorsValuesBroadcastReceiver);
    }
}