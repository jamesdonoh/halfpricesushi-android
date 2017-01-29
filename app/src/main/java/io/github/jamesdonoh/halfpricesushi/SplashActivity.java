package io.github.jamesdonoh.halfpricesushi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Branded launch screen, following advice at
 * https://material.io/guidelines/patterns/launch-screens.html#launch-screens-branded-launch
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start finder activity immediately
        Intent intent = new Intent(this, OutletFinderActivity.class);
        startActivity(intent);
        finish();
    }
}
