package com.example.universecity;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class NoteStorage {

    private static final String FILE_NAME = "notes.json";

    public static void saveNote(Context context, String noteName, String noteDescription) {

        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            JSONArray notesArray = new JSONArray();

            if (file.exists()) {
                StringBuilder jsonString = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                reader.close();

                if (!jsonString.toString().isEmpty()) {
                    notesArray = new JSONArray(jsonString.toString());
                }
            }

            JSONObject noteObject = new JSONObject();
            noteObject.put("note_name", noteName);
            noteObject.put("note_description", noteDescription);

            notesArray.put(noteObject);

            FileWriter writer = new FileWriter(file);
            writer.write(notesArray.toString());
            writer.close();

        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray loadNotes(Context context) {

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

    public static void removeNote(Context context, int noteIndex) {

        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) return;

            JSONArray notesArray = loadNotes(context);
            if (noteIndex < 0 || noteIndex >= notesArray.length()) return;

            JSONArray newNotesArray = new JSONArray();
            for (int i = 0; i < notesArray.length(); i++) {
                if (i != noteIndex) {
                    newNotesArray.put(notesArray.getJSONObject(i));
                }
            }

            FileWriter writer = new FileWriter(file, false);
            writer.write(newNotesArray.toString());
            writer.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }





}
