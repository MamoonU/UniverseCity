package com.example.universecity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.*;
import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class SecondFragment extends Fragment {

    public SecondFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_second, container, false);

        displayEvents(rootView);
        displayNotes(rootView);
        return rootView;
    }

    public void displayNotes(View rootView) {

        JSONArray notesArray = NoteStorage.loadNotes(getContext());

        TableLayout tableLayout = rootView.findViewById(R.id.notes_table);
        tableLayout.removeAllViews();

        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        headerRow.setPadding(10, 10, 10, 10);

        headerRow.addView(createStyledTextView("Title", true));
        headerRow.addView(createStyledTextView("Description", true));

        tableLayout.addView(headerRow);

        try {
            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject note = notesArray.getJSONObject(i);
                String noteName = note.optString("note_name");
                String noteDescription = note.optString("note_description");

                TableRow tableRow = new TableRow(getContext());

                if (i % 2 == 0) {
                    tableRow.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tableRow.setBackgroundColor(getResources().getColor(R.color.white));
                }

                tableRow.setPadding(10, 10, 10, 10);

                TextView noteNameTextView = createStyledTextView(noteName, false);

                TextView noteDescriptionTextView = createStyledTextView(noteDescription, false);

                tableRow.addView(noteNameTextView);
                tableRow.addView(noteDescriptionTextView);

                tableLayout.addView(tableRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TextView createStyledTextView(String text, Boolean isHeader) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        if (isHeader) {
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return textView;
    }

    public void displayEvents(View rootView) {

        JSONArray eventsArray = EventStorage.loadEvents(getContext());

        TableLayout tableLayout = rootView.findViewById(R.id.calendar_events_table);
        tableLayout.removeAllViews();
        Log.d("TableLayout", "TableLayout reference: " + tableLayout);

        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        headerRow.setPadding(10, 10, 10, 10);

        headerRow.addView(createStyledTextView("Event", true));
        headerRow.addView(createStyledTextView("Description", true));
        headerRow.addView(createStyledTextView("Date", true));
        headerRow.addView(createStyledTextView("Time", true));

        tableLayout.addView(headerRow);

        try {
            for (int i = 0; i < eventsArray.length(); i++) {
                JSONObject event = eventsArray.getJSONObject(i);
                String eventName = event.optString("name");
                String eventDescription = event.optString("description");
                String eventDate = event.optString("date");
                String startTime = event.optString("start_time");
                String endTime = event.optString("end_time");

                TableRow tableRow = new TableRow(getContext());

                Log.d("TableRow", "Creating row for event: " + eventName);

                if (i % 2 == 0) {
                    tableRow.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tableRow.setBackgroundColor(getResources().getColor(R.color.white));
                }

                tableRow.setPadding(10, 10, 10, 10);

                TextView eventNameTextView = createStyledTextView(eventName, false);
                TextView eventDescriptionTextView = createStyledTextView(eventDescription, false);
                TextView eventDateTextView = createStyledTextView(eventDate, false);
                TextView eventTimeTextView = createStyledTextView(startTime + " - " + endTime, false);

                tableRow.addView(eventNameTextView);
                tableRow.addView(eventDescriptionTextView);
                tableRow.addView(eventDateTextView);
                tableRow.addView(eventTimeTextView);

                tableLayout.addView(tableRow);
                tableLayout.requestLayout();
                Log.d("TableRow", "Row added for event: " + eventName);
            }
        } catch (Exception e) {
            Log.e("TableRow", "Error creating TableRow", e);
        }
    }

//    private void refreshEventsList() {
//
//        JSONArray eventsArray = EventStorage.loadEvents(getContext());
//
//        ArrayList<String> eventNames = new ArrayList<>();
//        ArrayList<String> eventDates = new ArrayList<>();
//
//        for (int i = 0; i < eventsArray.length(); i++) {
//            try {
//                JSONObject event = eventsArray.getJSONObject(i);
//                eventNames.add(event.optString("eventName"));
//                eventDates.add(event.optString("eventDate"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void refreshNotesList() {
//
//        JSONArray notesArray = NoteStorage.loadNotes(getContext());
//
//        ArrayList<String> noteNames = new ArrayList<>();
//        ArrayList<String> noteDescriptions = new ArrayList<>();
//
//        for (int i = 0; i < notesArray.length(); i++) {
//            try {
//                JSONObject note = notesArray.getJSONObject(i);
//                noteNames.add(note.optString("note_name"));
//                noteDescriptions.add(note.optString("note_description"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
}