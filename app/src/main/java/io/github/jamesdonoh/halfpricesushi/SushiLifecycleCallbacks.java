package io.github.jamesdonoh.halfpricesushi;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class SushiLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "lifecycle";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        String bundleInfo = savedInstanceState == null ? "null" : "Bundle";
        Log.d(TAG, activity.getClass().getSimpleName() + " onCreate(" + bundleInfo + ")");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + " onStart()");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + " onResume()");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + " onPause()");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + " onStop()");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, activity.getClass().getSimpleName() + " onSaveInstanceState(Bundle)");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, activity.getClass().getSimpleName() + " onDestroy()");
    }
}
