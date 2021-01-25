package com.easymp3cutter.rushiffmpeg;

import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AudioTrim extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_trim);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on

    }
}