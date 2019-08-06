package com.example.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
//MISSING:
//handle get currUser ?
//send statistics to server (to teacher)
//audio clues: add audios to library and change with switch case/url
//handle returning answer from requset if answered false/ requset failed

public class PictureRecognitionLevel extends AppCompatActivity {
//    DataBase db = new DataBase();
    private PictureRegocnitionQuestion mQuestion;
//    private Student mUser;
    public QuestionsData questions = new QuestionsData();
    //NEEDS TO GET LEVEL AND USER TYPE FROM CURRSENT USER
    private int mLevel;
    private String mId;
    ArrayList<PictureRegocnitionQuestion> questionStatistics = new ArrayList<PictureRegocnitionQuestion>();
    private int sizeOfLevel = 5;
    private int questionNumber = 0;
    private boolean mIsRecording = false;
    private String mPathSave = "";
    private MediaRecorder mMediaRecorder = null;
    private MediaPlayer mAudioCluePlayer;
    private final int REQUEST_PREMISSION_CODE = 1000;
    private int REQUEST_ANSWER= 200;
    private Question currQuestion;
    private ImageView imgWord;
    private int[] answeredQuestions;
    private Button answer;
    private TextView buttonClue;
    private ImageView imageTryAgain;
    private TextView textTryAgain;
    private ImageView imageGoodJob;
    private TextView textGoodJob;
    private boolean nextQuestion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_recognition_level);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        imgWord = findViewById(R.id.imageViewWord);
        answer = findViewById(R.id.buttonAnswerWordRecognition);
        buttonClue = findViewById(R.id.buttonClue);
        textTryAgain= findViewById(R.id.textViewTryAgain);
        imageTryAgain = findViewById(R.id.imageViewBirdTryAgain);
        imageGoodJob = findViewById(R.id.imageViewBirdGoodJob);
        textGoodJob =  findViewById(R.id.textViewGoodJob);
        buttonClue = findViewById(R.id.buttonClue);
        Intent intent = getIntent();
        mLevel = Integer.parseInt(intent.getStringExtra("level"));
        mId= intent.getStringExtra("id");
        questions.makeQuestionList();
        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        getNextQuestion();

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClue.setEnabled(false);
                if (!mIsRecording) //start recording
                {
                    answer.setText("עצור הקלטה");
                    if (checkPermissionFromDevice()) {
                        mPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "_audio_record.mp3";
                        System.out.println("+++++++++++++++++++++++++++" + mPathSave);
                        try {
                            setupMediaRecorder();
                            mMediaRecorder.prepare();
                            System.out.println("+++++++++++++++++++++++++++here");
                            mMediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        requestPermission();
                    }
                } else {
                    System.out.println("+++++++++++++++++++++++++++answered");
                    answer.setText("אנא המתן");
                    mMediaRecorder.stop();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                                    isCorrectAnswer(mMediaRecorder, answer);
                            File recording = new File(mPathSave);
                            boolean isDeleted = recording.delete();
                            if(!isDeleted){

                            }
                        }
                    });
                    thread.start();
                    mMediaRecorder.release();
                    System.out.println("@@@@@@@@@@@@@@@@@@@@released");
                    mMediaRecorder = null;
                    System.out.println("@@@@@@@@@@@@@@@@@@@@media recorder is null");


                    //TODO: sent answer to server and get result in REQUEST_ANSWER
                    if (REQUEST_ANSWER == 200) {
                        setBirdAnswerVisibility(imageGoodJob, textGoodJob);
//                        setBirdAnswerVisibility(imageTryAgain, textTryAgain);

                        getNextQuestion();
                    } else {
                        setBirdAnswerVisibility(imageTryAgain, textTryAgain);
                        mQuestion.IncreasemNumOfTries();
                    }
                }
                buttonClue.setEnabled(true);
                mIsRecording = !mIsRecording;

            }
        });

        buttonClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //answer.setEnabled(false);
                mAudioCluePlayer = MediaPlayer.create(PictureRecognitionLevel.this, currQuestion.GetmAudioRecording());
                mAudioCluePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAudioCluePlayer.stop();
                        buttonClue.setText(R.string.clue);
                    }
                });
                mAudioCluePlayer.start();
                buttonClue.setText("משמיע רמז...");
                mQuestion.SetAudioClueAsUsed();
                //answer.setEnabled(true);
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
        System.out.println("+++++++++++++++++++++++++++question: "+ questionNumber);

        if (questionNumber < sizeOfLevel) {
            do {
                currQuestion = questions.getRandomQuestion(mLevel);
            } while (answeredQuestions[currQuestion.GetmId()] == 1);
            answeredQuestions[currQuestion.GetmId()] = 1;
            mQuestion = new PictureRegocnitionQuestion(currQuestion);
            Picasso.with(PictureRecognitionLevel.this).load(mQuestion.getmImgPath()).into(imgWord);
            answer.setText("ענה");
            System.out.println("+++++++++++++++++++++++++++got question");
        }
        //finished level
        else {
            String text = String.format("כל הכבוד! סיימת את שלב %d!", mLevel);
            System.out.println("+++++++++++++++++++++++++++trying to finish game");
            //SEND questionsStatistics TO SERVER AND DELETE IT FROM MEMORY
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(PictureRecognitionLevel.this, text, duration);
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
            System.out.println("+++++++++++++++++++++++++++moving to home");
            moveToHomePage(mId);
        }
    }

    private void moveToHomePage(String iCurrUserId)
    {
        Intent intent = new Intent(PictureRecognitionLevel.this, HomePage.class);
        intent.putExtra("id", iCurrUserId);
        startActivity(intent);
    }

    private void setBirdAnswerVisibility(ImageView iImage, TextView iText){
        iImage.setVisibility(View.VISIBLE);
        iText.setVisibility(View.VISIBLE);
        answer.setVisibility(View.INVISIBLE);
        buttonClue.setVisibility(View.INVISIBLE);
        imgWord.setVisibility(View.INVISIBLE);
        nextQuestion = true;
    }

    private void setNextLevelVisibility(ImageView iImage, TextView iText) {
        if (nextQuestion) {
            iImage.setVisibility(View.INVISIBLE);
            iText.setVisibility(View.INVISIBLE);
            answer.setVisibility(View.VISIBLE);
            buttonClue.setVisibility(View.VISIBLE);
            imgWord.setVisibility(View.VISIBLE);
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
        //mMediaRecorder.setAudioSamplingRate(8000);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(mPathSave);
    }

    private void isCorrectAnswer(MediaRecorder iRecorder, final Button iButton)  {
        String url = "https://speech-rec-server.herokuapp.com/check_talking/";
        File file = new File(mPathSave);
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(mPathSave);
            byte[] bytes = fileToBytes();//inputStreamToByteArray(inFile);
            String stringBytes = new String(bytes);
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("answer", mQuestion.GetmAnswer());
                jsonBody.put("email", "ofer.feder@gmail.com");
                jsonBody.put("audio_file", stringBytes);
                final RequestQueue queue = Volley.newRequestQueue(this);
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
                System.out.println("+++++++++++++++++++++++" + stringBytes);

                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.print("****************************" + response);
                                iButton.setText(response.toString());
                            }
                        },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("ERROR!");
                    }
                });
                queue.add(jsonRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] fileToBytes() {
        byte[] bytes = null;
        File audioFile = new File(mPathSave);
        try {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                bytes = Files.readAllBytes(audioFile.toPath());

            } else {
            }
            ///This part is for debug - checks if the convertion to bytes
            ///was ok by converting the bytes to file
            //String str = new String(bytes);
//            System.out.println("*****************************" + str);
//            writeToFile(str, PictureRecognitionLevel.this);
//            File root
//            = new File(Environment.getExternalStorageDirectory(), "Decodes");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            File gpxfile = new File(root, "audio_decode.mp3");
//            try (FileOutputStream fos = new FileOutputStream(gpxfile.getAbsolutePath())) {
//                fos.write(bytes);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    private byte[] getBytes(File iAudioFile) throws IOException, FileNotFoundException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(iAudioFile);
        int read;

        while((read = fis.read(buffer)) != -1)
        {
            os.write(buffer, 0, read);
        }

        fis.close();
        os.close();

        return os.toByteArray();
    }

     private void writeToFile(String data, Context context) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "for_ofer.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
            System.out.println("((((((((((((((((((((((" + gpxfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("------------>EXCEPTION");
            e.printStackTrace();
        }
    }
}


//***********FOR DEBUG**************
//                    mMediaPlayer = new MediaPlayer();
//                    try
//                    {
//                        mMediaPlayer.setDataSource(mPathSave);
//                        mMediaPlayer.prepare();
//                    }
//                    catch (IOException e)
//                    {
//                        e.printStackTrace();
//                        System.out.println("~~~~~~~~~~~~~~~~~~~MESSAGE:" + e.getMessage());
//                    }
//
//                    mMediaPlayer.start();
//***********END OF DEBUG***********
