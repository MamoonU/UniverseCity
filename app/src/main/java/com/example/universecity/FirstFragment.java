package com.example.universecity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class FirstFragment extends Fragment {

    public FirstFragment() {
    }

    public CalendarView calendar;
    public TextView calendarText;
    public Button addEventButton;
    public Button removeEventButton;
    public String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        // Initialize UI components
        calendar = rootView.findViewById(R.id.calendar);
        calendarText = rootView.findViewById(R.id.calendar_text);
        addEventButton = rootView.findViewById(R.id.add_event_button);
        removeEventButton = rootView.findViewById(R.id.remove_event_button);

        // Set date change listener
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                calendarText.setText(selectedDate); // Update TextView with selected date
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDialog();
            }
        });

        removeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveEventDialog();
            }
        });

        displayEvents(rootView);
        return rootView;
    }
    public void eventDialog() {

        Dialog dialog =  new Dialog(getContext(), R.style.DialogStyle);
        dialog.setContentView(R.layout.add_event_dialog_layout);

        Button dialogDateButton = dialog.findViewById(R.id.date_reset_button);
        Switch timeSwitch = dialog.findViewById(R.id.time_switch);

        TextView timeText = dialog.findViewById(R.id.time_text);
        TextView dayText = dialog.findViewById(R.id.day_text);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button confirmButton = dialog.findViewById(R.id.confirm_button);

        EditText eventNameInput = dialog.findViewById(R.id.event_name_input);
        EditText eventDescriptionInput = dialog.findViewById(R.id.event_description_input);
        EditText startTimeInput = dialog.findViewById(R.id.start_time);
        EditText endTimeInput = dialog.findViewById(R.id.end_time);


        if (selectedDate != null) {
            dialogDateButton.setText(selectedDate);
        } else {
            dialogDateButton.setText("No Date Selected");
        }

        dialogDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //method to close the popup when re-selecting a date
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(v -> {

            String eventName = eventNameInput.getText().toString().trim();
            String eventDescription = eventDescriptionInput.getText().toString().trim();
            String startTime = startTimeInput.getText().toString().trim();
            String endTime = endTimeInput.getText().toString().trim();
            boolean isAllDay = timeSwitch.isChecked();

            String startTimeText = isAllDay ? "All Day" : startTime;
            String endTimeText = isAllDay ? "All Day" : endTime;

            EventStorage.saveEvent(getContext(), eventName, eventDescription, selectedDate, startTimeText, endTimeText);
            dialog.dismiss();
            Toast.makeText(getContext(), "Event Saved Successfully!", Toast.LENGTH_SHORT).show();
            displayEvents(getView());

        });

        timeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startTimeInput.setEnabled(false);
                endTimeInput.setEnabled(false);
                startTimeInput.setAlpha(0.3f);
                endTimeInput.setAlpha(0.3f);
                timeText.setAlpha(0.3f);
                dayText.setAlpha(1f);
            } else {
                startTimeInput.setEnabled(true);
                endTimeInput.setEnabled(true);
                startTimeInput.setAlpha(1f);
                endTimeInput.setAlpha(1f);
                timeText.setAlpha(1f);
                dayText.setAlpha(0.3f);
            }
        });

        dialog.show();

    }

    public void displayEvents(View rootView) {
        JSONArray eventsArray = EventStorage.loadEvents(getContext());

        Log.d("EventsArray", "Loaded events: " + eventsArray.length());

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

                tableRow.setPadding(10,10,10,10);

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

    private TextView createStyledTextView(String text, Boolean isHeader) {

        Log.d("TextView", "Creating TextView for text: " + text);

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


    private AlertDialog removeEventDialog;
    private void showRemoveEventDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.remove_event_dialog_layout, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.remove_event_list);
        ArrayList<String> eventNames = new ArrayList<>();
        ArrayList<String> eventDates = new ArrayList<>();

        JSONArray eventsArray = EventStorage.loadEvents(getContext());

        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject event = eventsArray.getJSONObject(i);
                String eventName = event.getString("name");
                String eventDate = event.getString("date");

                eventNames.add(eventName);
                eventDates.add(eventDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                getContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                eventNames
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text2.setText(eventDates.get(position));
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEvent = eventNames.get(position);
            String selectedDate = eventDates.get(position);

            showDeleteConfirmationDialog(selectedEvent, selectedDate);
        });

        builder.setCancelable(true);

        Button cancelButton = dialogView.findViewById(R.id.cancel_remove_button);
        cancelButton.setOnClickListener(v -> {
            removeEventDialog.dismiss();
        });

        removeEventDialog = builder.create();
        removeEventDialog.show();

    }

    private void showDeleteConfirmationDialog(String eventName, String eventDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Confirm Remove Event: " + eventName + "?").setPositiveButton("Confirm", (dialog, id) -> {
                    removeEvent(eventName);
                    dialog.dismiss();
                    displayEvents(getView());

                    if (removeEventDialog != null && removeEventDialog.isShowing()) {
                        removeEventDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create().show();
    }

    private void removeEvent(String eventName) {

        JSONArray eventsArray = EventStorage.loadEvents(getContext());
        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject event = eventsArray.getJSONObject(i);
                if (event.getString("name").equals(eventName)) {
                    EventStorage.removeEvent(getContext(), i);
                    Toast.makeText(getContext(), "Event Removed Successfully!", Toast.LENGTH_SHORT).show();

                    displayEvents(getView());
                    refreshEventsList();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void refreshEventsList() {

        JSONArray eventsArray = EventStorage.loadEvents(getContext());

        ArrayList<String> eventNames = new ArrayList<>();
        ArrayList<String> eventDates = new ArrayList<>();

        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject event = eventsArray.getJSONObject(i);
                eventNames.add(event.getString("eventName"));
                eventDates.add(event.getString("eventDate"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, eventNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                text2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                text1.setPadding(10, 0, 10, 0);
                text2.setPadding(10, 0, 10, 0);

                text2.setText(eventDates.get(position));
                return view;
            }
        };

        ListView listView = removeEventDialog.findViewById(R.id.remove_event_list);
        listView.setAdapter(adapter);
    }

//    private void saveToFile(String data) {
//        try {
//
//            File directory = new File(getContext().getFilesDir(), "events");
//
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            FileOutputStream fos = new FileOutputStream(new File(directory, "calendar_data.txt"), true);
//            fos.write(data.getBytes());
//            fos.close();
//            Toast.makeText(getContext(), "Event Saved Successfully!", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getContext(), "Error: Event Not Saved!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private String readFromFile() {
//
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            FileInputStream fis = new FileInputStream(new File(getContext().getFilesDir(), "events/calendar_data.txt"));
//            int character;
//            while ((character = fis.read()) != -1) {
//                stringBuilder.append((char) character);
//
//            }
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getContext(), "Error: Could not read event file", Toast.LENGTH_SHORT).show();
//        }
//            return stringBuilder.toString(); // Return the file content as a String
//    }

}
