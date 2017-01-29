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
import org.json.JSONObject;

import io.github.jamesdonoh.halfpricesushi.R;

/**
 * Represents the outlet offer database API and provides convenience methods to query and
 * update data.
 *
 * Follows singleton pattern recommended by
 * https://developer.android.com/training/volley/requestqueue.html
 *
 * TODO client should not have to update database and API separately
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
        String url = mBaseUrl + "/outlets/all";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
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
                        Log.e(TAG, "onResponse - error: " + error.toString(), error);
                        handler.onError(error);
                    }
                }
        );

        addToQueue(jsonArrayRequest);
    }

    public void setRating(int outletId, int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5");

        String url = mBaseUrl + "/ratings/outlet/" + outletId;
    }

    private void addToQueue(Request request) {
        // Set tag on request so it can be cancelled later
        request.setTag(TAG);

        Log.i(TAG, "queueing request for " + request.getUrl());
        mRequestQueue.add(request);
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
