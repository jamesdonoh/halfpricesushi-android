package io.github.jamesdonoh.halfpricesushi;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;

import io.github.jamesdonoh.halfpricesushi.model.OutletApi;
import io.github.jamesdonoh.halfpricesushi.model.OutletCache;

/**
 * Branded launch screen, following advice at
 * https://material.io/guidelines/patterns/launch-screens.html#launch-screens-branded-launch
 */
public class SplashActivity extends AppCompatActivity
        implements DataErrorDialogFragment.DataErrorDialogListener, OutletApi.DataHandler {
    private OutletApi mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApi = OutletApi.getInstance(this);
        requestApiData();
    }

    @Override
    protected void onStop () {
        super.onStop();

        if (mApi != null) {
            mApi.cancelAllRequests();
        }
    }

    public void onDataLoaded(JSONArray jsonArray) {
        OutletCache.updateOutletData(this, jsonArray);

        showDataUpdatedNotice();
        startFinderActivity();
    }

    public void onError(Exception error) {
        if (OutletCache.hasOutletData(this)) {
            showUsingCachedDataWarning();
            startFinderActivity();
        } else {
            // Allow user to retry request
            showDataErrorDialog();
        }
    }

    public void onRetryClick() {
        requestApiData();
    }

    private void showDataErrorDialog() {
        DialogFragment dialog = new DataErrorDialogFragment();
        dialog.show(getSupportFragmentManager(), "DataErrorDialogFragment");
    }

    private void requestApiData() {
        mApi.loadData(this);
    }

    private void showDataUpdatedNotice() {
        Toast.makeText(this, R.string.data_updated, Toast.LENGTH_SHORT).show();
    }

    private void showUsingCachedDataWarning() {
        Toast.makeText(this, R.string.cached_data_warning, Toast.LENGTH_LONG).show();
    }

    private void startFinderActivity() {
        // Start finder activity immediately
        Intent intent = new Intent(this, OutletFinderActivity.class);
        startActivity(intent);
        finish();
    }
}
