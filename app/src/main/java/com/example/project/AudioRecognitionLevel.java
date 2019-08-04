package com.example.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
//MISSING:
//handle get currUser ?
//send statistics to server (to teacher)
//audio clues: add audios to library and change with switch case/url
//handle returning answer from requset if answered false/ requset failed

public class AudioRecognitionLevel extends AppCompatActivity {
    //    DataBase db = new DataBase();
    private AudioRecognitionQuestion mQuestion;
    //    private Student mUser;
    public QuestionsData questions = new QuestionsData();
    //NEEDS TO GET LEVEL AND USER TYPE FROM CURRSENT USER
    private int mLevel;
    private String mId;
    ArrayList<AudioRecognitionQuestion> questionStatistics = new ArrayList<AudioRecognitionQuestion>();
    private int sizeOfLevel = 6;
    private int questionNumber = 0;
    private boolean mIsRecording = false;
    private String mPathSave = "";
    private MediaPlayer mMediaPlayer;
    private final int REQUEST_PREMISSION_CODE = 1000;
    private int REQUEST_ANSWER = 200;
    private Question currQuestion;
    private ImageView imageClue;
    private int[] answeredQuestions;
    private Button answer;
    private ImageView listen;
    private TextView buttonClue;
    private ImageView imageTryAgain;
    private TextView textTryAgain;
    private ImageView imageGoodJob;
    private TextView textGoodJob;
    private ImageView playCircle;
    private boolean nextQuestion = false;

    //
    private MediaPlayer mMediaPlayerAnswer;
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
        listen = findViewById(R.id.imageViewTrianglePlay);
        imageTryAgain = findViewById(R.id.imageViewBirdTryAgain);
        textTryAgain= findViewById(R.id.textViewTryAgain);
        imageGoodJob = findViewById(R.id.imageViewBirdGoodJob);
        textGoodJob =  findViewById(R.id.textViewGoodJob);
        buttonClue = findViewById(R.id.buttonClue);
        playCircle = findViewById(R.id.imageViewRectanglePlay);

        Intent intent = getIntent();

        mLevel = Integer.parseInt(intent.getStringExtra("level"));
        mId= intent.getStringExtra("id");
        questions.makeQuestionList();

        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        getNextQuestion();

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listen.setEnabled(false);
                listen.isClickable();
                System.out.println("========== /is listen enabled(should be no):" + listen.isClickable());
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
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                        //TODO: sent answer to server and get result in REQUEST_ANSWER
                        if (REQUEST_ANSWER == 200) {
                            setBirdAnswerVisibility(imageGoodJob, textGoodJob);
                            getNextQuestion();
                        } else {
                            setBirdAnswerVisibility(imageTryAgain, textTryAgain);
                            mQuestion.IncreasemNumOfTries();
                        }
                    }
                //listen.setEnabled(true);
                listen.setClickable(true);
                System.out.println("========== /is listen enabled(should be yes):" + listen.isClickable());
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //answer.setEnabled(false);
                answer.setClickable(false);
                System.out.println("========== /is answer enabled(should be no):" + answer.isClickable());
                mMediaPlayerListen = MediaPlayer.create(AudioRecognitionLevel.this, R.raw.boker_tov);
                    mMediaPlayerListen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayerListen.stop();
                            listen.setVisibility(View.VISIBLE);
                            int score = mQuestion.GetmScore();
                            mQuestion.SetmScore(score++);
                        }
                    });
                    mMediaPlayerListen.start();
                    listen.setVisibility(View.INVISIBLE);
            //answer.setEnabled(true);
                    answer.setClickable(true);
                System.out.println("========== /is answer enabled(should be yes):" + answer.isClickable());

            }
        });

        buttonClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClue.setVisibility(View.VISIBLE);
                mQuestion.SetImageClueAsUsed();
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
    }

    private void getNextQuestion(){
        questionStatistics.add(mQuestion);
        questionNumber++;
        //continue to next question
        imageClue.setVisibility(View.INVISIBLE);
        if (questionNumber < sizeOfLevel) {
            do {
                currQuestion = questions.getRandomQuestion(mLevel);
            } while (answeredQuestions[currQuestion.GetmId()] == 1);
            answeredQuestions[currQuestion.GetmId()] = 1;
            mQuestion = new AudioRecognitionQuestion(currQuestion);
            Picasso.with(AudioRecognitionLevel.this).load(mQuestion.GetmImageClue()).into(imageClue);
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
        startActivity(intent);
    }

    private void setBirdAnswerVisibility(ImageView iImage, TextView iText){
        iImage.setVisibility(View.VISIBLE);
        iText.setVisibility(View.VISIBLE);
        answer.setVisibility(View.INVISIBLE);
        buttonClue.setVisibility(View.INVISIBLE);
        listen.setVisibility(View.INVISIBLE);
        playCircle.setVisibility(View.INVISIBLE);

        nextQuestion = true;
    }

    private void setNextLevelVisibility(ImageView iImage, TextView iText) {
        if (nextQuestion) {
            iImage.setVisibility(View.INVISIBLE);
            iText.setVisibility(View.INVISIBLE);
            answer.setVisibility(View.VISIBLE);
            buttonClue.setVisibility(View.VISIBLE);
            listen.setVisibility(View.VISIBLE);
            playCircle.setVisibility(View.VISIBLE);
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