package com.example.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

public class PictureRecognitionLevel extends GameLevel{
    private ImageView imgWord;
    private MediaPlayer mAudioCluePlayer;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
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
        sizeOfLevel = 3;// questions.getSizeOfLevel(mLevel);
        getNextQuestion(imgWord, false);
        mAudioCluePlayer = MediaPlayer.create(PictureRecognitionLevel.this, currQuestion.GetmAudioRecording());
        mIsAudioResourcesFree = true;

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
                                isCorrectAnswer(mMediaRecorder, answer, imgWord, false);
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

//    private void isCorrectAnswer(MediaRecorder iRecorder, final Button iButton)  {
//        String url = "https://speech-rec-server.herokuapp.com/check_talking/";
//        File file = new File(mPathSave);
//        InputStream inFile = null;
//        try {
//            inFile = new FileInputStream(mPathSave);
//            byte[] bytes = fileToBytes();//inputStreamToByteArray(inFile);
//            //new String(bytes, "UTF-8");
//            String stringBytes = null;
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//                stringBytes = Base64.getMimeEncoder().encodeToString(bytes);
//
//            } else {
//                System.out.println("low API");
//            }
//            System.out.println("!!!!!!OFER!!!!!");
//            System.out.println(stringBytes);
//            System.out.println("!!!!!!DONE!!!!!");
//            writeToFile(stringBytes, PictureRecognitionLevel.this);
//
//            try {
//                JSONObject jsonBody = new JSONObject();
//                jsonBody.put("original_text", mQuestion.GetmAnswer());
//                jsonBody.put("id", mId);
//                jsonBody.put("audio_file", stringBytes);
//                final RequestQueue queue = Volley.newRequestQueue(this);
//                RequestFuture<JSONObject> future = RequestFuture.newFuture();
//                //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
//                System.out.println("+++++++++++++++++++++++" + jsonBody);
//
//                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                System.out.println("****************************" + response);
//                                //iButton.setText(response.toString());
//                                try {
//                                    if(response.getString("answer").toLowerCase() == "true")
//                                    {
//                                        System.out.println("$$$$$$$$$$$$$$ is correct");
//                                        setBirdAnswerVisibility(imageGoodJob, textGoodJob, imgWord);
//                                        questionStatistics.add((PictureRegocnitionQuestion) mQuestion);
//                                        questionNumber++;
//                                        succeededQuestions++;
//                                        mQuestion.IncreasemScore();
//                                        getNextQuestion(imgWord, false);
//                                    }
//                                    else
//                                    {
//                                        System.out.println("$$$$$$$$$$$$$$ is not correct");
//                                        setBirdAnswerVisibility(imageTryAgain, textTryAgain, imgWord);
//                                        mQuestion.IncreasemNumOfTries();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        },  new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println("ERROR!" + error.getMessage());
//                    }
//                });
//                queue.add(jsonRequest);
//                System.out.println("###############SENT");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private byte[] fileToBytes() {
//        byte[] bytes = null;
//        File audioFile = new File(mPathSave);
//        try {
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//
//                bytes = Files.readAllBytes(audioFile.toPath());
//
//            } else {
//            }
//            ///This part is for debug - checks if the convertion to bytes
//            ///was ok by converting the bytes to file
//            //String str = new String(bytes);
////            System.out.println("*****************************" + str);
////            writeToFile(str, PictureRecognitionLevel.this);
////            File root
////            = new File(Environment.getExternalStorageDirectory(), "Decodes");
////            if (!root.exists()) {
////                root.mkdirs();
////            }
////            File gpxfile = new File(root, "audio_decode.mp3");
////            try (FileOutputStream fos = new FileOutputStream(gpxfile.getAbsolutePath())) {
////                fos.write(bytes);
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return bytes;
//    }

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
