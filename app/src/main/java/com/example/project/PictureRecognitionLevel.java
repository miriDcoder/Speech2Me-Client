package com.example.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

public class PictureRecognitionLevel extends GameLevel{
    private ImageView imgWord;
    private MediaPlayer mAudioCluePlayer;
    private ArrayList<PictureRegocnitionQuestion> questionStatistics = new ArrayList<PictureRegocnitionQuestion>();


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
        questions.makeQuestionList();
        answeredQuestions = new int [questions.getSizeOfLevel(mLevel)];
        sizeOfLevel = questions.getSizeOfLevel(mLevel);
        getNextQuestion(imgWord, false);

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textClue.setEnabled(false);
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
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    System.out.println("@@@@@@@@@@@@@@@@@@@@released");
                    mMediaRecorder = null;
                    System.out.println("@@@@@@@@@@@@@@@@@@@@media recorder is null");


                    //TODO: sent answer to server and get result in REQUEST_ANSWER
                    if (REQUEST_ANSWER == 200) {    //Answer is correct, next question
//                        setBirdAnswerVisibility(imageGoodJob, textGoodJob);
                        setBirdAnswerVisibility(imageTryAgain, textTryAgain, imgWord);
                        questionStatistics.add((PictureRegocnitionQuestion) mQuestion);
                        questionNumber++;
                        succeededQuestions++;
                        mQuestion.IncreasemScore();
                        getNextQuestion(imgWord, false);
                    } else {    //Answer is incorrect, try again
                        setBirdAnswerVisibility(imageTryAgain, textTryAgain, imgWord);
                        mQuestion.IncreasemNumOfTries();
                    }
                }
                textClue.setEnabled(true);
                mIsRecording = !mIsRecording;

            }
        });

        textClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //answer.setEnabled(false);
                mAudioCluePlayer = MediaPlayer.create(PictureRecognitionLevel.this, currQuestion.GetmAudioRecording());
                mAudioCluePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAudioCluePlayer.stop();
                        textClue.setText(R.string.clue);
                    }
                });
                mAudioCluePlayer.start();
                textClue.setText("משמיע רמז...");
                mQuestion.SetClueAsUsed();
                //answer.setEnabled(true);
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

    private void isCorrectAnswer(MediaRecorder iRecorder, final Button iButton)  {
        String url = "https://speech-rec-server.herokuapp.com/check_talking/";
        File file = new File(mPathSave);
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(mPathSave);
            byte[] bytes = fileToBytes();//inputStreamToByteArray(inFile);
            String stringBytes = new String(bytes);
            System.out.println("!!!!!!OFER!!!!!");
            System.out.println(stringBytes);
            System.out.println("!!!!!!DONE!!!!!");
            writeToFile(stringBytes, PictureRecognitionLevel.this);
//            try {
//                JSONObject jsonBody = new JSONObject();
//                jsonBody.put("answer", mQuestion.GetmAnswer());
//                jsonBody.put("email", "ofer.feder@gmail.com");
//                jsonBody.put("audio_file", stringBytes);
//                final RequestQueue queue = Volley.newRequestQueue(this);
//                RequestFuture<JSONObject> future = RequestFuture.newFuture();
//                //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
//                System.out.println("+++++++++++++++++++++++" + stringBytes);
//
//                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                System.out.print("****************************" + response);
//                                iButton.setText(response.toString());
//                            }
//                        },  new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.print("ERROR!");
//                    }
//                });
//                queue.add(jsonRequest);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
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
