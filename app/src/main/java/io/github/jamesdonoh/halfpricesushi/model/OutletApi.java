package io.github.jamesdonoh.halfpricesushi.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import io.github.jamesdonoh.halfpricesushi.R;

/**
 * Represents the outlet offer database API and provides convenience methods to query and
 * update data.
 *
 * Follows singleton pattern recommended by
 * https://developer.android.com/training/volley/requestqueue.html
 */
public class OutletApi {
    private static final String TAG = OutletApi.class.getSimpleName();

    // TODO deal with warning (see below)
    private static OutletApi sInstance;

    private Context mContext;

    private RequestQueue mRequestQueue;

    private String mBaseUrl;

    private OutletApi(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

        // TODO make this not depend on R (non-model concern)
        mBaseUrl = context.getString(R.string.offer_api_url);
    }

    public static synchronized OutletApi getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new OutletApi(context);
        }

        return sInstance;
    }

    public void cancelAllRequests() {
        mRequestQueue.cancelAll(TAG);
    }

    public void loadData(final DataHandler handler) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = mBaseUrl + "/outlets/all";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(TAG, "onResponse: " + response.toString().substring(0, 100));
                        handler.onDataLoaded(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onResponse - error", error);
                        handler.onError(error);
                    }
                }
        );

        // Set the tag on the request so it can be cancelled later
        jsonObjectRequest.setTag(TAG);

        Log.i(TAG, "loadData: queueing request for " + url);
        queue.add(jsonObjectRequest);
    }

    public interface DataHandler {
        void onDataLoaded(JSONArray data);
        void onError(Exception exception);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // Must use application context to ensure this lasts lifetime of app
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }
}
