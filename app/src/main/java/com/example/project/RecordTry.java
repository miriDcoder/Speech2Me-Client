package com.example.project;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RecordTry extends AppCompatActivity implements View.OnClickListener {
    MediaRecorder recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_try);

        Button buttonRecord = (Button)findViewById(R.id.buttonRecord);
        Button buttonStopRecord = (Button)findViewById(R.id.buttonStopRecord);

        buttonRecord.setOnClickListener(this);
        buttonStopRecord.setOnClickListener(this);
    }

        @Override
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);

        } else {
            switch (v.getId()) {
                case R.id.buttonRecord:
                    startRecording();
                    break;
                case R.id.buttonStopRecord:
                    stopRecording();
                    break;
            }
        }
    }

    private void startRecording()
    {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        else
        {

        }
    }

    private void stopRecording()
    {

    }
}
