package io.github.jamesdonoh.halfpricesushi;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class OutletDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation  == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        setContentView(R.layout.activity_outlet_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (savedInstanceState == null) {
            // Activity is being initialised for first time, so create details fragment
            OutletDetailsFragment detailsFragment = new OutletDetailsFragment();

            // Pass outlet ID from extra to fragment as argument
            detailsFragment.setArguments(getIntent().getExtras());

            // Add fragment to content frame of activity layout
            getSupportFragmentManager().beginTransaction().add(R.id.content, detailsFragment).commit();
        }
    }
}
