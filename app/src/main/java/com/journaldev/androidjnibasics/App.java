package com.journaldev.androidjnibasics;

import android.app.Application;

import com.journaldev.androidjnibasics.error.UCEHandler;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
         new UCEHandler.Builder(this)
                 .setTrackActivitiesEnabled(true)
                .addCommaSeparatedEmailAddresses("support@joyn.de")
                .build();
    }
}
