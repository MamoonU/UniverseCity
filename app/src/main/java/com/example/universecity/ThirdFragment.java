package com.example.universecity;

import androidx.annotation.NonNull;
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


public class ThirdFragment extends Fragment {

    public ThirdFragment(){
    }

    public Button addNoteButton;
    public Button removeNoteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_third, container, false);

        addNoteButton = rootView.findViewById(R.id.add_note_button);
        removeNoteButton = rootView.findViewById(R.id.remove_note_button);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteDialog();
            }
        });

        removeNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNoteDialog();
            }
        });

        displayNotes(rootView);
        return rootView;
    }

    public void addNoteDialog() {

        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
        dialog.setContentView(R.layout.add_note_dialog_layout);

        Button confirmButton = dialog.findViewById(R.id.confirm_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        EditText noteNameInput = dialog.findViewById(R.id.note_name_input);
        EditText noteDescriptionInput = dialog.findViewById(R.id.note_description_input);

        confirmButton.setOnClickListener( v ->  {

            String noteName = noteNameInput.getText().toString().trim();
            String noteDescription = noteDescriptionInput.getText().toString().trim();

            NoteStorage.saveNote(getContext(), noteName, noteDescription);
            dialog.dismiss();

            Toast.makeText(getContext(), "Note Saved Successfully!", Toast.LENGTH_SHORT).show();

            displayNotes(getView());

        });

        dialog.show();

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
                String noteName = note.getString("note_name");
                String noteDescription = note.getString("note_description");

                TableRow tableRow = new TableRow(getContext());

                if (i % 2 == 0) {
                    tableRow.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tableRow.setBackgroundColor(getResources().getColor(R.color.white));
                }

                tableRow.setPadding(10,10,10,10);

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

    private AlertDialog removeNoteDialog;
    public void removeNoteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.remove_note_dialog_layout, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.remove_note_list);
        ArrayList<String> noteNames = new ArrayList<>();
        ArrayList<String> noteDescriptions = new ArrayList<>();

        JSONArray notesArray = NoteStorage.loadNotes(getContext());
        Log.d("NoteStorage", "Loaded notes: " + notesArray.toString());

        for (int i = 0; i < notesArray.length(); i++) {
            try {
                JSONObject note = notesArray.getJSONObject(i);
                String noteName = note.getString("note_name");
                String noteDescription = note.getString("note_description");

                noteNames.add(noteName);
                noteDescriptions.add(noteDescription);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("NoteStorage", "Note Names: " + noteNames.toString());
        Log.d("NoteStorage", "Note Descriptions: " + noteDescriptions.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                getContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                noteNames
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text2.setText(noteDescriptions.get(position));
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedNote = noteNames.get(position);
            String selectedDate = noteDescriptions.get(position);

            showDeleteConfirmationDialog(selectedNote, selectedDate);
        });

        builder.setCancelable(true);

        Button cancelButton = dialogView.findViewById(R.id.cancel_note_remove_button);
        cancelButton.setOnClickListener(v -> {
            removeNoteDialog.dismiss();
        });

        removeNoteDialog = builder.create();
        removeNoteDialog.show();

    }

    private void showDeleteConfirmationDialog(String noteName, String noteDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Confirm Remove Note: " + noteName + "?").setPositiveButton("Confirm", (dialog, id) -> {
                    removeNote(noteName);
                    dialog.dismiss();

                    if (removeNoteDialog != null && removeNoteDialog.isShowing()) {
                        removeNoteDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create().show();
    }

    private void removeNote(String noteName) {

        JSONArray notesArray = NoteStorage.loadNotes(getContext());

        for (int i = 0; i < notesArray.length(); i++) {
            try {
                JSONObject note = notesArray.getJSONObject(i);
                if (note.getString("note_name").equals(noteName)) {

                    NoteStorage.removeNote(getContext(), i);
                    Toast.makeText(getContext(), "Note Removed Successfully!", Toast.LENGTH_SHORT).show();

                    displayNotes(getView());
                    refreshNotesList();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshNotesList() {

        JSONArray notesArray = NoteStorage.loadNotes(getContext());

        ArrayList<String> noteNames = new ArrayList<>();
        ArrayList<String> noteDescriptions = new ArrayList<>();

        for (int i = 0; i < notesArray.length(); i++) {
            try {
                JSONObject note = notesArray.getJSONObject(i);
                noteNames.add(note.getString("note_name"));
                noteDescriptions.add(note.getString("note_description"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, noteNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                text2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                text1.setPadding(10, 0, 10, 0);
                text2.setPadding(10, 0, 10, 0);

                text2.setText(noteDescriptions.get(position));
                return view;
            }
        };

        ListView listView = removeNoteDialog.findViewById(R.id.remove_note_list);
        listView.setAdapter(adapter);
    }
    
    

}