package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OutletDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO fail fast if we're in landscape mode now

        if (savedInstanceState == null) {
            // Activity is being initialised for first time, so create details fragment
            OutletDetailsFragment detailsFragment = new OutletDetailsFragment();

            // Pass outlet ID from extra to fragment as argument
            detailsFragment.setArguments(getIntent().getExtras());

            // Add fragment to the root view of the activity
            int rootViewId = android.R.id.content;
            getSupportFragmentManager().beginTransaction().add(rootViewId, detailsFragment).commit();
        }
    }
}
