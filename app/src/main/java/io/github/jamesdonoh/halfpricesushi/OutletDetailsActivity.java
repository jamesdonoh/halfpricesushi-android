package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class OutletDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_outlet_details);

        // Set up toolbar as action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (savedInstanceState == null) {
            // Activity is being initialised for first time, so create details fragment
            OutletDetailsFragment detailsFragment = new OutletDetailsFragment();

            // Pass outlet ID from extra to fragment as argument
            detailsFragment.setArguments(getIntent().getExtras());

            // Add fragment to content frame of activity layout
            getSupportFragmentManager().beginTransaction().add(R.id.details, detailsFragment).commit();
        }
    }
}
