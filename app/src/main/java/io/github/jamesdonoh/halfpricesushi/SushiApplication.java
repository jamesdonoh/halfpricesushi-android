package io.github.jamesdonoh.halfpricesushi;

import android.app.Application;

/**
 * Custom Application class for the Sushi application.
 *
 * Implemented in order to allow activity lifecycle to be logged for debugging purposes.
 */
public class SushiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new SushiLifecycleCallbacks());
    }
}
