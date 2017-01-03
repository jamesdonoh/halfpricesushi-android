package io.github.jamesdonoh.halfpricesushi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OutletDetailActivity extends AppCompatActivity {
    // See https://developer.android.com/reference/android/content/Intent.html#putExtras%28android.os.Bundle%29
    public final static String EXTRA_OUTLET_ID = "io.github.jamesdonoh.halfpricesushi.outletId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO fail fast if we're in landscape mode now

        if (savedInstanceState == null) {
            OutletDetailFragment details = new OutletDetailFragment();

            // Pass outlet ID from extra on to fragment as argument
            details.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}
