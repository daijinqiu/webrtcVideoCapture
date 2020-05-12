package com.example.capturecamera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "MainActivity";

    private Button camera1Btn;
    private Button camera2Btn;
    private Button screenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera1Btn = findViewById(R.id.camera1_btn);
        camera2Btn = findViewById(R.id.camera2_btn);
        screenBtn = findViewById(R.id.screen_btn);

        camera1Btn.setOnClickListener(this);
        camera2Btn.setOnClickListener(this);
        screenBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera1_btn:
                Intent camera1Intent = new Intent(this, Camera1Activity.class);
                startActivity(camera1Intent);
                break;
            case R.id.camera2_btn:
                Intent camera2Intent = new Intent(this, Camera2Activity.class);
                startActivity(camera2Intent);
                break;
            case R.id.screen_btn:
                Intent screenIntent = new Intent(this, ScreenActivity.class);
                startActivity(screenIntent);
                break;
        }
    }
}
