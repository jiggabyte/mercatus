package com.jigga.mercatus.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mLocation;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("My Location");
        mLocation = new MutableLiveData<>();
        mLocation.setValue("Location: Fetching...");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getLocation() {
        return mLocation;
    }

    public void setLocation(String latitude, String longitude) {
        mLocation.setValue("Latitude: " + latitude + "\nLongitude: " + longitude);
    }

}
