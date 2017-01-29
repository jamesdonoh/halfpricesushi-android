package io.github.jamesdonoh.halfpricesushi.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Deserializes instances of Outlet from API's JSON representation.
 * TODO consider using GSON instead?
 */
public class OutletJsonLoader {
    static List<Outlet> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Outlet> outletList = new ArrayList<Outlet>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Outlet outlet = parseJsonOutlet(jsonArray.getJSONObject(i));
                outletList.add(outlet);
            } catch (JSONException je) {
                // TODO add better handling
                je.printStackTrace();
            }
        }

        return outletList;
    }

    private static Outlet parseJsonOutlet(JSONObject data) throws JSONException {
        int id = data.getInt("id");
        String name = data.getString("name");

        JSONObject location = data.getJSONObject("location");
        double latitude = location.getDouble("lat");
        double longitude = location.getDouble("long");

        // TODO make this nicer
        Outlet outlet = new Outlet(id, name, latitude, longitude);

        JSONObject times = data.getJSONObject("times");
        String[] dayNames = new String[] { "mon", "tue", "wed", "thu", "fri", "sat", "sun" };
        for (int day = 1; day <= 7; day++) {
            String dayName = dayNames[day - 1];
            if (times.has(dayName)) {
                JSONObject dayInfo = times.getJSONObject(dayName);

                String opens = dayInfo.getString("opens");
                String closes = dayInfo.getString("closes");

                outlet.setOpeningTimes(day, opens, closes);
            }
        }

        return outlet;
    }
}
