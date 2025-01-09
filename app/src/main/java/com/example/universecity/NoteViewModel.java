package com.example.universecity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

public class NoteViewModel extends ViewModel {
    private final MutableLiveData<JSONArray> notesLiveData = new MutableLiveData<>();

    public void setNotes(JSONArray notes) {
        notesLiveData.setValue(notes);
    }

    public LiveData<JSONArray> getNotes() {
        return notesLiveData;
    }
}
