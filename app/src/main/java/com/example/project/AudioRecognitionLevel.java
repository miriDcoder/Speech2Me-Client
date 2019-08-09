package com.example.project;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
//MISSING:
//handle get currUser ?
//send statistics to server (to teacher)
//audio clues: add audios to library and change with switch case/url
//handle returning answer from requset if answered false/ requset failed

public class AudioRecognitionLevel extends GameLevel {
    private ImageView imageClue;
    private ImageView play;
    private ImageView pause;
    private MediaPlayer mMediaPlayerListen;
    private ArrayList<AudioRecognitionQuestion> questionStatistics = new ArrayList<AudioRecognitionQuestion>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recognition_level);

        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        imageClue = findViewById(R.id.imageViewClue);
        answer = findViewById(R.id.buttonAnswerRecordingRecognition);
        play = findViewById(R.id.imageViewPlay);
        pause = findViewById(R.id.imageViewPause);
        imageTryAgain = findViewById(R.id.imageViewBirdTryAgain);
        textTryAgain= findViewById(R.id.textViewTryAgain);
        imageGoodJob = findViewById(R.id.imageViewBirdGoodJob);
        textGoodJob =  findViewById(R.id.textViewGoodJob);
        textClue = findViewById(R.id.buttonClue);
        homePage = findViewById(R.id.buttonHomePage);
        goToNextQuestion = findViewById(R.id.buttonNextQuestion);
        textPressToContinue = findViewById(R.id.textViewPressToContinue);
        Intent intent = getIntent();

        mLevel = Integer.parseInt(intent.getStringExtra("level"));
        mId = intent.getStringExtra("id");
        questions.makeQuestionList();

        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        getNextQuestion(imageClue);

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play.setEnabled(false);
                play.isClickable();
                System.out.println("========== /is listen enabled(should be no):" + play.isClickable());
                    if (!mIsRecording) //start recording
                    {
                        answer.setText("עצור הקלטה");
                        if (checkPermissionFromDevice()) {
                            mPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    UUID.randomUUID().toString() + "_audio_record.mp3";
                            setupMediaRecorder();
                            try {
                                mMediaRecorder.prepare();
                                mMediaRecorder.start();
                                mIsRecording = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            requestPermission();
                        }
                    } else {
                        answer.setText("אנא המתן");
                        mMediaRecorder.stop();
                        mIsRecording = false;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
//                         isCorrectAnswer(mMediaRecorder, answer);
                            }
                        });
                        thread.start();
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                        //TODO: sent answer to server and get result in REQUEST_ANSWER
                        if (REQUEST_ANSWER == 200) {
                            setBirdAnswerVisibility(imageGoodJob, textGoodJob, play);
//                            setBirdAnswerVisibility(imageTryAgain, textTryAgain);
                            questionStatistics.add((AudioRecognitionQuestion)mQuestion);
                            questionNumber++;
                            mQuestion.IncreasemScore();
                            getNextQuestion(imageClue);
                        } else {
                            setBirdAnswerVisibility(imageTryAgain, textTryAgain, play);
                            mQuestion.IncreasemNumOfTries();
                        }
                    }
                //play.setEnabled(true);
                play.setClickable(true);
                System.out.println("========== /is play enabled(should be yes):" + play.isClickable());
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //answer.setEnabled(false);
                answer.setClickable(false);
                System.out.println("========== /is answer enabled(should be no):" + answer.isClickable());
                int audioPath = ((AudioRecognitionQuestion) mQuestion).getmAudioPath();
                mMediaPlayerListen = MediaPlayer.create(AudioRecognitionLevel.this, audioPath);
                    mMediaPlayerListen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayerListen.stop();
                            pause.setVisibility(View.INVISIBLE);
                            play.setVisibility(View.VISIBLE);
                            int score = mQuestion.GetmScore();
                            mQuestion.SetmScore(score++);
                        }
                    });
                    mMediaPlayerListen.start();
                    play.setVisibility(View.INVISIBLE);
                    pause.setVisibility(View.VISIBLE);
                //answer.setEnabled(true);
                    answer.setClickable(true);
                System.out.println("========== /is answer enabled(should be yes):" + answer.isClickable());

            }
        });

        textClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClue.setVisibility(View.VISIBLE);
                textClue.setVisibility(View.INVISIBLE);
                mQuestion.SetClueAsUsed();
            }
        });

        imageGoodJob.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                setNextLevelVisibility(imageGoodJob, textGoodJob, play);
            }
        });

        imageTryAgain.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                setNextLevelVisibility(imageTryAgain, textTryAgain, play);
            }
        });

        homePage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(AudioRecognitionLevel.this);
                builder.setMessage("האם לצאת מהמשחק?");

                // add the buttons
                builder.setPositiveButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        moveToHomePage(mId);
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        goToNextQuestion.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(AudioRecognitionLevel.this);
                builder.setMessage("האם לעבור לשאלה הבאה?");

                // add the buttons
                builder.setPositiveButton("לנסות שוב", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getNextQuestion(imageClue);
                        textClue.setVisibility(View.VISIBLE);
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

}