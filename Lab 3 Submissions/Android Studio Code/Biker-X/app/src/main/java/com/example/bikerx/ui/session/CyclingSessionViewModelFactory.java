package com.example.bikerx.ui.session;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Helper class to initialise CyclingSessionViewModel using desired arguments.
 */
public class CyclingSessionViewModelFactory implements ViewModelProvider.Factory {
    private Context context;
    private AppCompatActivity activity;

    public CyclingSessionViewModelFactory(Context context, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new CyclingSessionViewModel(this.context, this.activity);
    }
}
