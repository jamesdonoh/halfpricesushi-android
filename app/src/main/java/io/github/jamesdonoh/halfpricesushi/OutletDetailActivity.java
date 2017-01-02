package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OutletDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO fail fast if we're in landscape mode now

        if (savedInstanceState == null) {
            OutletDetailFragment details = new OutletDetailFragment();
            // What do the next two lines actually do?
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}
