package com.example.project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Game extends AppCompatActivity {

    Button btnRecord, btnStopRecord, btnPlay, btnStop;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    final int REQUEST_PREMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnRecord = (Button)findViewById(R.id.btnStartRecord);
        btnStop = (Button)findViewById(R.id.btnStop);
        btnStopRecord = (Button)findViewById(R.id.btnStopRecord);
            btnRecord.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (checkPermissionFromDevice()) {
                        pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "_audio_record.mp3";
                        setupMediaRecorder();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            btnStopRecord.setEnabled(true); //enable stop record
                            btnPlay.setEnabled(false); //shouldn't be able to play while recording too
                            btnRecord.setEnabled(false); //yours to see if should be able to restart record while recording, but probably not if it crashes
                            btnStop.setEnabled(false); //probably should be disabled too on record
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(Game.this, "Recording...", Toast.LENGTH_SHORT).show(); //CHECK IF ITS OK
                    } else {
                        requestPermission();
                    }
                }
            });

            btnStopRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    btnStopRecord.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                    Toast.makeText(Game.this, "Stop Recoding done", Toast.LENGTH_SHORT).show(); //CHECK IF ITS OK
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Recording done");
                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println("++++++++++++++++++++++++++++++++In Play");
                    btnStopRecord.setEnabled(false);
                    btnRecord.setEnabled(false);
                    btnStop.setEnabled(true);
                    mediaPlayer = new MediaPlayer();
                    try
                    {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    Toast.makeText(Game.this, "Playing...", Toast.LENGTH_SHORT).show(); //CHECK IF ITS OK
                }
            });

            btnStop.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    btnStopRecord.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                    if(mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });
        }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$SETTING RECORD");
        System.out.println("pathSave");
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PREMISSION_CODE);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PREMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
    //changessssssssssssssssssssssssss//

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}

