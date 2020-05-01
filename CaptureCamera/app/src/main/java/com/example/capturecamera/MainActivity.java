package com.example.capturecamera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.capturecamera.opengl.*;

public class MainActivity extends AppCompatActivity {

    final EglBase eglBase = EglBase.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
