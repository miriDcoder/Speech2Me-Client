package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

//This is the Audio recognition game
public class PictureRecognitionLevel extends GameLevel{
    private ImageView imgWord;
    private MediaPlayer mAudioCluePlayer;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private ImageView play;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_recognition_level);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        imgWord = findViewById(R.id.imageViewWord);
        answer = findViewById(R.id.buttonAnswerWordRecognition);
        textClue = findViewById(R.id.buttonClue);
        textTryAgain= findViewById(R.id.textViewTryAgain);
        imageTryAgain = findViewById(R.id.imageViewBirdTryAgain);
        imageGoodJob = findViewById(R.id.imageViewBirdGoodJob);
        textGoodJob =  findViewById(R.id.textViewGoodJob);
        homePage = findViewById(R.id.buttonHomePage);
        goToNextQuestion = findViewById(R.id.buttonNextQuestion);
        textPressToContinue = findViewById(R.id.textViewPressToContinue);
        Intent intent = getIntent();
        mLevel = Integer.parseInt(intent.getStringExtra("level"));
        mId= intent.getStringExtra("id");
        mUserType = intent.getStringExtra("user_type");
        questions.makeQuestionList();
        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        sizeOfLevel = questions.getSizeOfLevel(mLevel);
        getNextQuestion(imgWord, false);
        mAudioCluePlayer = MediaPlayer.create(PictureRecognitionLevel.this, currQuestion.GetmAudioRecording());
        mIsAudioResourcesFree = true;
        play = findViewById(R.id.imagePlay);
        //If the user is recording - need to setup the recorder
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAudioCluePlayer.isPlaying()) {
                    if (!mIsRecording) //start recording
                    {
                        answer.setText("עצור הקלטה");
                        if (checkPermissionFromDevice()) {
                            mPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    UUID.randomUUID().toString() + "audio_record.mp3";
                            try {
                                setupMediaRecorder();
                                mMediaRecorder.prepare();
                                mMediaRecorder.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            requestPermission();
                        }
                    } else {
                        answer.setText("אנא המתן");
                        mMediaRecorder.stop();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                isCorrectAnswer(mMediaRecorder, answer, imgWord, play, false);
                                File recording = new File(mPathSave);
                                boolean isDeleted = recording.delete();
                                if (!isDeleted) {
                                    System.out.println("Couldn't delete file");
                                }
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
        //The clue is an audio so need to setup the audio player
        textClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                System.out.println("+++++++++++++++++++++++++++" + mIsAudioResourcesFree);
                if (!mIsRecording) {
                    mAudioCluePlayer = MediaPlayer.create(PictureRecognitionLevel.this, currQuestion.GetmAudioRecording());
                    mAudioCluePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mAudioCluePlayer.stop();
                            mIsAudioResourcesFree = true;
                            textClue.setText(R.string.clue);
                        }
                    });
                    mAudioCluePlayer.start();
                    textClue.setText("משמיע רמז...");
                    mQuestion.SetClueAsUsed();
                }
            }
        });

        imageGoodJob.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                setNextLevelVisibility(imageGoodJob, textGoodJob, imgWord);
            }
        });

        imageTryAgain.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                setNextLevelVisibility(imageTryAgain, textTryAgain, imgWord);
            }
        });

        textGoodJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextLevelVisibility(imageGoodJob, textGoodJob, imgWord);
            }
        });

        textTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextLevelVisibility(imageTryAgain, textTryAgain, imgWord);
            }
        });

        homePage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(PictureRecognitionLevel.this);
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

        goToNextQuestion.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(PictureRecognitionLevel.this);
                builder.setMessage("האם לעבור לשאלה הבאה?");

                // add the buttons
                builder.setPositiveButton("לנסות שוב", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        questionNumber++;
                        getNextQuestion(imgWord, false);
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