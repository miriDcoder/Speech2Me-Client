package com.example.project;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class RecordTry extends AppCompatActivity {
    MediaRecorder recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_try);

        Button buttonRecord = (Button)findViewById(R.id.buttonRecord);
        Button buttonStopRecord = (Button)findViewById(R.id.buttonStopRecord);
        recorder = new MediaRecorder();

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile("C:/Users/miril/Desktop");
                try {
                    recorder.prepare();
                    recorder.start();   // Recording is now started
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                recorder.release(); // Now the object cannot be reused
            }
        });
    }
}
