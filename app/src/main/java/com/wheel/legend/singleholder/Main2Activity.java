package com.wheel.legend.singleholder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.wheel.legend.sligle_holder.slide.SlideHelper;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button button=findViewById(R.id.button);

        button.setOnClickListener(v -> {

            Intent intent=new Intent(this,Main3Activity.class);

            startActivity(intent);

        });

        SlideHelper.getInstance().setSlideActivity(this);
    }
}
