package com.wheel.legend.singleholder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wheel.legend.sligle_holder.slide.SlideHelper;

public class Main5Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        SlideHelper.getInstance().setSlideActivity(this);
    }
}
