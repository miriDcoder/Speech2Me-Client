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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class WordRecognitionGame extends AppCompatActivity {
    private WordRecognition mLevel;
    private boolean mIsRecording = false;
    private String mPathSave = "";
    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayer;
    final int REQUEST_PREMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_recognition_game);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        ImageView imgWord = (ImageView)findViewById(R.id.imageViewWord);
        final Button answer = (Button)findViewById(R.id.buttonAnswerWordRecognition);
        final Button wordClue = (Button)findViewById(R.id.buttonWordClue);
        Button audioClue = (Button)findViewById(R.id.buttonAudioClue);

        mLevel = new WordRecognition();
        mLevel.SetmImgPath("https://image.freepik.com/free-vector/little-white-house_88088-10.jpg");
        Picasso.with(this).load(mLevel.getmImgPath()).into(imgWord);
        mLevel.SetmWordClue("המשפחה גרה בו");
        //System.out.println("~~~~~~~~is word clue used: " + mLevel.IsWordClueUsed());

        wordClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLevel.WordClueUsed();
                wordClue.setEnabled(false);
                wordClue.setText(mLevel.GetmWordClue());
                //System.out.println("~~~~~~~~is word clue used: " + mLevel.IsWordClueUsed());
            }
        });

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsRecording) //start recording
                {
                    answer.setText("עצור הקלטה");
                    if (checkPermissionFromDevice()) {
                        mPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "_audio_record.mp3";
                        setupMediaRecorder();
                        try {
                            mMediaRecorder.prepare();
                            mMediaRecorder.start();
                            mIsRecording = !mIsRecording;
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        requestPermission();
                    }

                }
                else //stop recording
                {
                    answer.setText("אנא המתן");
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                    //***********FOR DEBUG**************
                    mMediaPlayer = new MediaPlayer();
                    try
                    {
                        mMediaPlayer.setDataSource(mPathSave);
                        mMediaPlayer.prepare();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        System.out.println("~~~~~~~~~~~~~~~~~~~MESSAGE:" + e.getMessage());
                    }

                    mMediaPlayer.start();
                    //***********END OF DEBUG***********
                    mIsRecording = !mIsRecording;
                    //TODO: send the recording to relevant things etc...
                }

            }
        });
        //TODO: load the game from the database
    }

    private boolean checkPermissionFromDevice()
    {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PREMISSION_CODE);
    }

    private void setupMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(mPathSave);
    }
}
