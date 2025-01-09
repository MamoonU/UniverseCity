package com.example.universecity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<JSONArray> eventsLiveData = new MutableLiveData<>();

    public void setEvents(JSONArray events) {
        eventsLiveData.setValue(events);
    }

    public LiveData<JSONArray> getEvents() {
        return eventsLiveData;
    }
}