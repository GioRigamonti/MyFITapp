package it.unimib.myfitapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfidenceActivity extends AppCompatActivity {
    private TextView downstairsTextView;
    private TextView joggingTextView;
    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView upstairsTextView;
    private TextView walkingTextView;

    private TableRow bikingTableRow;
    private TableRow downstairsTableRow;
    private TableRow joggingTableRow;
    private TableRow sittingTableRow;
    private TableRow standingTableRow;
    private TableRow upstairsTableRow;
    private TableRow walkingTableRow;

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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

    /*private void setProbabilities() {
        downstairsTextView.setText(Float.toString(round(results[1], 2)));
        joggingTextView.setText(Float.toString(round(results[2], 2)));
        sittingTextView.setText(Float.toString(round(results[3], 2)));
        standingTextView.setText(Float.toString(round(results[4], 2)));
        upstairsTextView.setText(Float.toString(round(results[5], 2)));
        walkingTextView.setText(Float.toString(round(results[6], 2)));
    }

    private void setRowsColor(int idx) {
        bikingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));

        if (idx == 0)
            downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        else if (idx == 1)
            joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        else if (idx == 2)
            sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        else if (idx == 3)
            standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        else if (idx == 4)
            upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
        else if (idx == 5)
            walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color., null));
    }*/

}