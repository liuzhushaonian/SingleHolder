package com.wheel.legend.singleholder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button=findViewById(R.id.button);

        button.setOnClickListener(v -> {

            Intent intent=new Intent(this,Main2Activity.class);

            startActivity(intent);

        });



    }
}
