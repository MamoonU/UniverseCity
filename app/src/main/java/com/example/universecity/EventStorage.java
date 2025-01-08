package com.example.universecity;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class EventStorage {

    private static final String FILE_NAME = "events.json";

    public static void saveEvent(Context context, String eventName, String eventDescription, String eventDate, String startTime, String endTime) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            JSONArray eventsArray = new JSONArray();

            if (file.exists()) {
                StringBuilder jsonString = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                reader.close();

                if (!jsonString.toString().isEmpty()) {
                    eventsArray = new JSONArray(jsonString.toString());
                }
            }

            JSONObject eventObject = new JSONObject();
            eventObject.put("name", eventName);
            eventObject.put("description", eventDescription);
            eventObject.put("date", eventDate);
            eventObject.put("start_time", startTime);
            eventObject.put("end_time", endTime);

            eventsArray.put(eventObject);

            FileWriter writer = new FileWriter(file);
            writer.write(eventsArray.toString());
            writer.close();

        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    static JSONArray loadEvents(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                return new JSONArray();
            }

            StringBuilder jsonString = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            return new JSONArray(jsonString.toString());

        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void removeEvent(Context context, int eventIndex) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) return;

            JSONArray eventsArray = loadEvents(context);
            if (eventIndex >= 0  && eventIndex < eventsArray.length()) {
                eventsArray.remove(eventIndex);
            }

            FileWriter writer = new FileWriter(file);
            writer.write(eventsArray.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
