package com.example.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
    private MediaPlayer mMediaPlayerListen = null;
    private boolean mIsAudioResourcesFree;
    private ArrayList<AudioRecognitionQuestion> questionStatistics = new ArrayList<AudioRecognitionQuestion>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recognition_level);

        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        imageClue = findViewById(R.id.imageViewClue);
        answer = (Button)findViewById(R.id.buttonAnswerRecordingRecognition);
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
        sizeOfLevel = questions.getSizeOfLevel(mLevel);
        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        getNextQuestion(imageClue, true);

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayerListen == null)
                {
                    messageToUser("השמע את ההקלטה טרם מתן תשובה");
                }
                else if(!mMediaPlayerListen.isPlaying())
                {
                    System.out.println("...........RECORDING. MEDIA PLAYER: " + mMediaPlayerListen.isPlaying());
                    mIsAudioResourcesFree = false;
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
                            imageClue.setVisibility(View.INVISIBLE);
                            questionStatistics.add((AudioRecognitionQuestion)mQuestion);
                            questionNumber++;
                            succeededQuestions++;
                            mQuestion.IncreasemScore();
                            getNextQuestion(imageClue, true);
                        } else {
                            setBirdAnswerVisibility(imageTryAgain, textTryAgain, play);
                            mQuestion.IncreasemNumOfTries();
                        }
                    }
                    mIsAudioResourcesFree = true;
                    System.out.println("...........DONE RECORDING. AUDIO RES: " + mIsAudioResourcesFree);
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsRecording)
                {
                    mIsAudioResourcesFree = false;
                    System.out.println("...........PLAYING. AUDIO RES: " + mIsAudioResourcesFree);
                    int audioPath = ((AudioRecognitionQuestion) mQuestion).getmAudioPath();
                    mMediaPlayerListen = MediaPlayer.create(AudioRecognitionLevel.this, audioPath);
                    mMediaPlayerListen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayerListen.stop();
                            pause.setVisibility(View.INVISIBLE);
                            play.setVisibility(View.VISIBLE);
                            int score = ((Question)mQuestion).GetmScore();
                            mQuestion.IncreasemScore();
                        }
                    });
                    mMediaPlayerListen.start();
                    play.setVisibility(View.INVISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    mIsAudioResourcesFree = true;
                    System.out.println("...........DONE PLAYING. AUDIO RES: " + mIsAudioResourcesFree);
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
                        questionNumber++;
                        getNextQuestion(imageClue, true);
                        textClue.setVisibility(View.VISIBLE);
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    private void messageToUser(CharSequence text)
    {
        Context context = getBaseContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}