package io.github.jamesdonoh.halfpricesushi.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Represents the outlet offer database API and provides convenience methods to query and
 * update data.
 *
 * Follows singleton pattern recommended by
 * https://developer.android.com/training/volley/requestqueue.html
 */
class OutletApi {
    private static final String TAG = OutletApi.class.getSimpleName();

    // TODO deal with warning (see below)
    private static OutletApi sInstance;

    private Context mContext;

    private RequestQueue mRequestQueue;

    private OutletApi(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized OutletApi getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new OutletApi(context);
        }

        return sInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // Must use application context to ensure this lasts lifetime of app
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    void fetchOutlets(Context context, String baseUrl) {
        RequestQueue queue = Volley.newRequestQueue(context);
        //String baseUrl = getString(R.string.offer_api_url);
        String url = baseUrl + "/outlets/all";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response.substring(0, 100));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onResponse - error", error);
                    }
                }
        );

        Log.i(TAG, "loadData: queueing request for " + url);
        queue.add(stringRequest);
    }
}
