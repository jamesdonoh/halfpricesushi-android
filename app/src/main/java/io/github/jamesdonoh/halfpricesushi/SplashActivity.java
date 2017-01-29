package io.github.jamesdonoh.halfpricesushi;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.jamesdonoh.halfpricesushi.model.OutletStore;

/**
 * Branded launch screen, following advice at
 * https://material.io/guidelines/patterns/launch-screens.html#launch-screens-branded-launch
 */
public class SplashActivity extends AppCompatActivity
        implements DataErrorDialogFragment.DataErrorDialogListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkOutletDataAndContinue();
    }

    private void checkOutletDataAndContinue() {
        if (OutletStore.getNumOutlets(this) == 0) {
            // A problem occurred fetching data, prompt to retry
            showDataErrorDialog();
        } else {
            // Otherwise continue to main finder activity
            startFinderActivity();
        }
    }

    private void showDataErrorDialog() {
        DialogFragment dialog = new DataErrorDialogFragment();
        dialog.show(getSupportFragmentManager(), "DataErrorDialogFragment");
    }

    public void startFinderActivity() {
        // Start finder activity immediately
        Intent intent = new Intent(this, OutletFinderActivity.class);
        startActivity(intent);
        finish();
    }

    public void onRetryClick() {
        checkOutletDataAndContinue();
    }
}
