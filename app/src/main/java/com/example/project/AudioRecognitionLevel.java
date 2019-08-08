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

public class AudioRecognitionLevel extends AppCompatActivity {
    DataBase db = new DataBase();
    private Question mQuestion;
    public QuestionsData questions = new QuestionsData();
    private ArrayList<AudioRecognitionQuestion> questionStatistics = new ArrayList<AudioRecognitionQuestion>();
    private int sizeOfLevel = 6;
    private int questionNumber = 0;
    private int REQUEST_ANSWER = 200;
    private int mLevel;
    private final int REQUEST_PREMISSION_CODE = 1000;
    private int[] answeredQuestions;
    private String mId;
    private String mPathSave = "";
    private boolean mIsRecording = false;
    private boolean nextQuestion = false;
    private Question currQuestion;
    private ImageView imageClue;
    private Button answer;
    private Button homePage;
    private Button goToNextQuestion;
    private ImageView play;
    private ImageView pause;
    private ImageView imageTryAgain;
    private ImageView imageGoodJob;
    private TextView textClue;
    private TextView textTryAgain;
    private TextView textGoodJob;
    private TextView textPressToContinue;
    private MediaPlayer mMediaPlayerListen;
    private MediaRecorder mMediaRecorder;

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
        mId= intent.getStringExtra("id");
        questions.makeQuestionList();

        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        getNextQuestion();

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
                            setBirdAnswerVisibility(imageGoodJob, textGoodJob);
//                            setBirdAnswerVisibility(imageTryAgain, textTryAgain);
                            mQuestion.IncreasemScore();
                            getNextQuestion();
                        } else {
                            setBirdAnswerVisibility(imageTryAgain, textTryAgain);
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
                mMediaPlayerListen = MediaPlayer.create(AudioRecognitionLevel.this, currQuestion.GetmAudioRecording());
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
                setNextLevelVisibility(imageGoodJob, textGoodJob);
            }
        });

        imageTryAgain.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                setNextLevelVisibility(imageTryAgain, textTryAgain);
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
                        getNextQuestion();
                        textClue.setVisibility(View.VISIBLE);
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    private void getNextQuestion(){
        questionStatistics.add((AudioRecognitionQuestion)mQuestion);
        questionNumber++;
        //continue to next question
        imageClue.setVisibility(View.INVISIBLE);
        if (questionNumber < sizeOfLevel) {
            do {
                currQuestion = questions.getRandomQuestion(mLevel);
            } while (answeredQuestions[currQuestion.GetmId()] == 1);
            answeredQuestions[currQuestion.GetmId()] = 1;
            mQuestion = new AudioRecognitionQuestion(currQuestion);
            System.out.println("^^^^^^^^^^^^"+((AudioRecognitionQuestion) mQuestion).GetmImageClue());
            String imageCluePath = ((AudioRecognitionQuestion) mQuestion).GetmImageClue();
            Picasso.with(AudioRecognitionLevel.this).load(imageCluePath).into(imageClue);
            answer.setText("ענה");
        }
        //finished level
        else {
            String text = String.format("כל הכבוד! סיימת את שלב %d!", mLevel);
            //SEND questionsStatistics TO SERVER AND DELETE IT FROM MEMORY
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(AudioRecognitionLevel.this, text, duration);
            toast.show();
            for (int i= 0; i< questions.getSizeOfLevel(mLevel);i++){
                answeredQuestions[i] = 0;
            }
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            moveToHomePage(mId);
        }
    }

    private void moveToHomePage(String iCurrUserId)
    {
        Intent intent = new Intent(AudioRecognitionLevel.this, HomePage.class);
        intent.putExtra("id", iCurrUserId);
        intent.putExtra("newScore", mQuestion.GetmScore());
        startActivity(intent);
    }

    private void setBirdAnswerVisibility(ImageView iImage, TextView iText){
        iImage.setVisibility(View.VISIBLE);
        iText.setVisibility(View.VISIBLE);
        answer.setVisibility(View.INVISIBLE);
        textClue.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        textPressToContinue.setVisibility(View.VISIBLE);
        nextQuestion = true;
    }

    private void setNextLevelVisibility(ImageView iImage, TextView iText) {
        if (nextQuestion) {
            iImage.setVisibility(View.INVISIBLE);
            iText.setVisibility(View.INVISIBLE);
            answer.setVisibility(View.VISIBLE);
            textClue.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            textPressToContinue.setVisibility(View.INVISIBLE);
            nextQuestion = false;
        }
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