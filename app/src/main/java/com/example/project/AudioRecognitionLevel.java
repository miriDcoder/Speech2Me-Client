package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//This is the Audio recognition game
public class AudioRecognitionLevel extends GameLevel {
    private ImageView imageClue;
    private ImageView imagePlay;
    private ImageView imagePause;
    private MediaPlayer mMediaPlayerListen = null;

    //private boolean isAtLeaseOncePlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recognition_level);

        if(!checkPermissionFromDevice()) {
            requestPermission();
        }

        //set XML elements
        imageClue = findViewById(R.id.imageViewClue);
        imagePlay = findViewById(R.id.imageViewPlay);
        imagePause = findViewById(R.id.imageViewPause);
        imageTryAgain = findViewById(R.id.imageViewBirdTryAgain);
        imageGoodJob = findViewById(R.id.imageViewBirdGoodJob);
        textTryAgain= findViewById(R.id.textViewTryAgain);
        textGoodJob =  findViewById(R.id.textViewGoodJob);
        textClue = findViewById(R.id.textViewClue);
        textPressToContinue = findViewById(R.id.textViewPressToContinue);
        textQuestionNumber = findViewById(R.id.textViewQuestionNumber);
        buttonAnswer = findViewById(R.id.buttonAnswerRecordingRecognition);
        buttonGoToNextQuestion = findViewById(R.id.buttonNextQuestion);
        buttonHomePage = findViewById(R.id.buttonHomePage);

        //set class members
        Intent intent = getIntent();
        mLevel = Integer.parseInt(intent.getStringExtra("level"));
        mId = intent.getStringExtra("id");
        mUserType = intent.getStringExtra("user_type");
        mQuestions.makeQuestionList();
        mSizeOfLevel = mQuestions.getSizeOfLevel(mLevel);
        mAnsweredQuestions = new int [mQuestions.getSizeOfLevel(mLevel)];
        getNextQuestion(imageClue, true);
        textQuestionNumber.setText(String.format("שאלה %d מתוך %d", mQuestionNumber +1, mSizeOfLevel));
        //if the user is recording - make the other button disabled and setup the recorder
        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayerListen == null)
                {
                    messageToUser("השמע את ההקלטה טרם מתן תשובה");
                }
                else if(!mMediaPlayerListen.isPlaying())
                {
                    if (!mIsRecording) //start recording
                    {
                        buttonAnswer.setText("עצור הקלטה");
                        if (checkPermissionFromDevice()) {
                            mPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    UUID.randomUUID().toString() + "_audio_record.mp3";
                            setupMediaRecorder();
                            try {
                                mMediaRecorder.prepare();
                                mMediaRecorder.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            requestPermission();
                        }
                    } else {
                        buttonAnswer.setText("אנא המתן");
                        mMediaRecorder.stop();

                        //new thread for checking the answer
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                isCorrectAnswer(mMediaRecorder, buttonAnswer, imageClue, imagePlay, true);
                                File recording = new File(mPathSave);
                                boolean isDeleted = recording.delete();
                            }
                        });
                        thread.start();
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                    }
                    mIsRecording = !mIsRecording;
                }
            }
        });

        //if the user is listening to the audio - need to make record button disabled and setup the player
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsRecording)
                {
                    int audioPath = ((AudioRecognitionQuestion) mQuestion).getmAudioPath();
                    mMediaPlayerListen = MediaPlayer.create(AudioRecognitionLevel.this, audioPath);
                    mMediaPlayerListen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayerListen.stop();
                            imagePause.setVisibility(View.INVISIBLE);
                            imagePlay.setVisibility(View.VISIBLE);
                        }
                    });
                    mMediaPlayerListen.start();
                    imagePlay.setVisibility(View.INVISIBLE);
                    imagePause.setVisibility(View.VISIBLE);
                }
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
                setNextLevelVisibility(imageGoodJob, textGoodJob, imagePlay);
            }
        });

        imageTryAgain.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                setNextLevelVisibility(imageTryAgain, textTryAgain, imagePlay);
            }
        });

        buttonHomePage.setOnClickListener(new View.OnClickListener(){
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
                        moveToHomePage(mId, mUserType);
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        buttonGoToNextQuestion.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(AudioRecognitionLevel.this);
                builder.setMessage("האם לעבור לשאלה הבאה?");

                builder.setPositiveButton("לנסות שוב", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mQuestionNumber++;
                        textClue.setVisibility(View.VISIBLE);
                        imageClue.setVisibility(View.INVISIBLE);
                        getNextQuestion(imageClue, true);
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }
}