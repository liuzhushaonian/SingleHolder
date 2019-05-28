package com.wheel.legend.singleholder;

import android.app.Application;

import com.wheel.legend.sligle_holder.slide.SlideHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SlideHelper.setApplication(this);
    }
}
