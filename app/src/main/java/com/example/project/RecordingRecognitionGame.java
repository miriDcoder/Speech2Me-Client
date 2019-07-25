package com.example.project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class RecordingRecognitionGame extends AppCompatActivity {

    private RecordingRecognition mLevel;
    private boolean mIsRecording = false;
    private String mPathSave = "";
    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayerAnswer;
    MediaPlayer mMediaPlayerListen;

    final int REQUEST_PREMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_recognition_game);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        ImageView imgWord = (ImageView)findViewById(R.id.imageViewWordRecording);
        final Button answer = (Button)findViewById(R.id.buttonAnswerRecordingRecognition);
        final Button listen= (Button)findViewById(R.id.buttonListenToRecordingRecognition);

        mLevel = new RecordingRecognition();
        mLevel.SetmImgPath("https://cdn.pixabay.com/photo/2018/05/24/21/36/summer-3427732_1280.png");
        Picasso.with(this).load(mLevel.getmImgPath()).into(imgWord);
        System.out.println("AFTER PICASSO");
        //mMediaPlayerListen = new MediaPlayer();
        //mMediaPlayerListen.setAudioStreamType(AudioManager.STREAM_MUSIC);
        System.out.println("+++++++++++before try");
//            mMediaPlayerListen =  mMediaPlayerListen.create(RecordingRecognitionGame.this, R.raw.bait_yarok);
//            mMediaPlayerListen.prepare();


        //Listen to the question button

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayerListen = MediaPlayer.create(RecordingRecognitionGame.this,R.raw.boker_tov);
                mMediaPlayerListen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mMediaPlayerListen.stop();
                        listen.setText("הקשב להקלטה");
                        int score = mLevel.GetmScore();
                        mLevel.SetmScore(score++);
                        try
                        {
                            Thread.sleep(600);

                            //UPDATE STUDENT'S SCORE AND

                        }
                        catch(InterruptedException ex)
                        {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                mMediaPlayerListen.start();
                listen.setText("משמיע...");
            }
        });

        //Answer the question button
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
                    mMediaPlayerAnswer = new MediaPlayer();
                    try
                    {
                        mMediaPlayerAnswer.setDataSource(mPathSave);
                        mMediaPlayerAnswer.prepare();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        System.out.println("~~~~~~~~~~~~~~~~~~~MESSAGE:" + e.getMessage());
                    }
                    mMediaPlayerAnswer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayerAnswer.stop();
                            answer.setText("מצוין!");
                            int score = mLevel.GetmScore();
                            mLevel.SetmScore(score++);

                            try
                            {
                                Thread.sleep(600);
                                //UPDATE STUDENT'S SCORE

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.student_fragment_container, new StudentHomePageFragment()).commit();
                                transaction.addToBackStack(null);
//                                transaction.commit();
                            }
                            catch(InterruptedException ex)
                            {
                                Thread.currentThread().interrupt();
                            }
                        }
                    });

                    mMediaPlayerAnswer.start();
                    //***********END OF DEBUG***********
                    mIsRecording = !mIsRecording;
                    //

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
