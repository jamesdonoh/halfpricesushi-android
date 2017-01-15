package io.github.jamesdonoh.halfpricesushi.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to load outlet definitions from a JSON file.
 */
public class OutletFileLoader {
    public static List<Outlet> getOutlets(Context context) {
        JSONArray outletData = loadJSONArray(context, "outlets.json");
        return parseOutletData(outletData);
    }

    private static Outlet parseJSONOutlet(JSONObject data) throws JSONException {
        int id = data.getInt("id");
        String name = data.getString("name");
        String openingTimes = data.getString("times");

        return new Outlet(id, name, openingTimes);
    }

    private static List<Outlet> parseOutletData(JSONArray outletData) {
        ArrayList<Outlet> outletList = new ArrayList<Outlet>();
        for (int i = 0; i < outletData.length(); i++) {
            try {
                Outlet outlet = parseJSONOutlet(outletData.getJSONObject(i));
                outletList.add(outlet);
            } catch (JSONException je) {
                je.printStackTrace();
                // ?
            }
        }

        return outletList;
    }

    public static JSONArray loadJSONArray(Context context, String filename) {
        // NB error handling here is probably awful
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            return new JSONArray(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
