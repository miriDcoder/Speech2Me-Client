package com.example.project;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;

public abstract class GameLevel extends AppCompatActivity {
    public QuestionsData mQuestions = new QuestionsData();
    private ArrayList<Question> mQuestionStatistics = new ArrayList<Question>();
    protected Question mQuestion;
    protected Question mCurrQuestion;
    protected int mSizeOfLevel;
    protected int mQuestionNumber = 0;
    protected int mLevel;
    protected int mSucceededQuestions = 0;
    protected final int REQUEST_PREMISSION_CODE = 1000;
    protected int[] mAnsweredQuestions;
    protected String mId;
    protected String mPathSave = "";
    protected String mUserType;
    protected boolean mIsRecording = false;
    protected boolean mIsAudioResourcesFree;
    protected boolean mNextQuestion = false;
    protected MediaRecorder mMediaRecorder;
    protected Button buttonAnswer;
    protected Button buttonHomePage;
    protected Button buttonGoToNextQuestion;
    protected ImageView imageTryAgain;
    protected ImageView imageGoodJob;
    protected TextView textClue;
    protected TextView textTryAgain;
    protected TextView textGoodJob;
    protected TextView textPressToContinue;
    protected TextView textQuestionNumber;

    protected void moveToHomePage(String iCurrUserId, String iUserType)
    {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("id", iCurrUserId);
        intent.putExtra("user_type", iUserType);
        startActivity(intent);
    }

    protected void setBirdAnswerVisibility(ImageView iImageBird, TextView iTextBird, ImageView iImagePlay, ImageView iPlay){

        //set screen with answer from the server
        iImageBird.setVisibility(View.VISIBLE);
        iTextBird.setVisibility(View.VISIBLE);
        textPressToContinue.setVisibility(View.VISIBLE);

        //make game elements invisible
        iImagePlay.setVisibility(View.INVISIBLE);
        iPlay.setVisibility(View.INVISIBLE);
        textQuestionNumber.setVisibility(View.INVISIBLE);
        buttonAnswer.setVisibility(View.INVISIBLE);
        textClue.setVisibility(View.INVISIBLE);
        buttonGoToNextQuestion.setVisibility(View.INVISIBLE);
        mNextQuestion = true;
    }

    protected void setNextLevelVisibility(ImageView iImage, TextView iText, ImageView iImagePlay) {
        if (mNextQuestion) {

            //make answer from the server invisible
            iImage.setVisibility(View.INVISIBLE);
            iText.setVisibility(View.INVISIBLE);
            textPressToContinue.setVisibility(View.INVISIBLE);

            //set screen with next question
            iImagePlay.setVisibility(View.VISIBLE);
            textQuestionNumber.setVisibility(View.VISIBLE);
            buttonAnswer.setVisibility(View.VISIBLE);
            textClue.setVisibility(View.VISIBLE);
            buttonGoToNextQuestion.setVisibility(View.VISIBLE);
            mNextQuestion = false;
        }
        buttonAnswer.setText(R.string.answer);
    }

    protected boolean checkPermissionFromDevice()
    {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PREMISSION_CODE);
    }

    protected void setupMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOutputFile(mPathSave);
    }

    protected void getNextQuestion(ImageView iImage, boolean isAudio){

        //continue to next question on the level
        String imagePath;
        if (mQuestionNumber < mSizeOfLevel) {
            do {
                mCurrQuestion = mQuestions.getRandomQuestion(mLevel);
            } while (mAnsweredQuestions[mCurrQuestion.GetmId()] == 1);
            mAnsweredQuestions[mCurrQuestion.GetmId()] = 1;
            if (isAudio){
                mQuestion = new AudioRecognitionQuestion(mCurrQuestion);
                imagePath = ((AudioRecognitionQuestion) mQuestion).GetmImageClue();
                buttonAnswer.setText(R.string.answer_audio);
            }
            else{
                mQuestion = new PictureRegocnitionQuestion(mCurrQuestion);
                imagePath = ((PictureRegocnitionQuestion) mQuestion).getmImgPath();
                buttonAnswer.setText(R.string.answer_picture);
            }
            Picasso.with(this).load(imagePath).into(iImage);
            buttonAnswer.setText(R.string.answer);
            textQuestionNumber.setText(String.format("שאלה %d מתוך %d", mQuestionNumber +1, mSizeOfLevel));
        }
        //finished level
        else {
            String textToUser = null;
//            updateScore();
            switch(mUserType){
                case("teacher"):
                    textToUser = String.format("התנסות בשלב %d של המשחק הסתיימה", mLevel);
                    break;
                case("student"):
                    //if player didn't succeed all question -
                    // update score, stay on the same level and go to home page.
                    updateScore();
                    textToUser = String.format("ניסיון יפה! אבל לא עברת את שלב %d, נסה שוב...", mLevel);

                    //if player succeeded all question -
                    // update level, send mQuestions statistics to server (to teacher) ang go to home page.
                    if (mSucceededQuestions == mSizeOfLevel) {
                        textToUser = String.format("כל הכבוד! סיימת את שלב %d!", mLevel);
                        ArrayList<JSONObject> answers = getAnswers();
                        String url = "https://speech-rec-server.herokuapp.com/finish_level/";
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("user_id", mId);
                            jsonBody.put("level", mLevel);
                            jsonBody.put("answers", answers);
                            final RequestQueue queue = Volley.newRequestQueue(this);
                            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            updateLevel();
                                            moveToHomePage(mId, mUserType);
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //if there was an error with updating the progress data -
                                    //we're showing the user a dialog that explaing the situation and
                                    //redirecting to the user home page, to do the level again
                                    showErrorDialog();
                                    moveToHomePage(mId, mUserType);
                                }
                            });
                            queue.add(jsonRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorDialog();
                            moveToHomePage(mId, mUserType);
                        }
                    }
                    break;
            }
            messageToUser(textToUser);

            //initialize used mQuestions
            for (int i = 0; i< mQuestions.getSizeOfLevel(mLevel); i++){
                mAnsweredQuestions[i] = 0;
            }
            try
            {
                Thread.sleep(1000);
            }catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            moveToHomePage(mId, mUserType);
        }
    }

    private ArrayList<JSONObject> getAnswers(){
        JSONObject answer = null;
        ArrayList<JSONObject> answers = new ArrayList<JSONObject>();
        for(Question question : mQuestionStatistics){
            try {
                answer = new JSONObject();
                answer.put("isAudioClueUsed", question.IsClueUsed());
                answer.put("numOfTries", question.GetmNumOfTries());
                answer.put("word", question.GetmAnswer());
                answer.put("answer", question.GetmSucceeded());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            answers.add(answer);
        }
        return answers;
    }

    protected void messageToUser(CharSequence text)
    {
        Context context = getBaseContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    protected void isCorrectAnswer(MediaRecorder iRecorder, final Button iButton, final ImageView iImgWord, final ImageView iPlay, final boolean isAudio)  {
        String url = "https://speech-rec-server.herokuapp.com/check_talking/";
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(mPathSave);
            byte[] bytes = fileToBytes();
            String stringBytes = null;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                stringBytes = Base64.getMimeEncoder().encodeToString(bytes);

            } else {
                stringBytes = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            }
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("original_text", mQuestion.GetmAnswer());
                jsonBody.put("id", mId);
                jsonBody.put("audio_file", stringBytes);
                final RequestQueue queue = Volley.newRequestQueue(this);

                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getString("answer").toLowerCase() == "true")
                                    {
                                        setBirdAnswerVisibility(imageGoodJob, textGoodJob, iImgWord, iPlay);
                                        mQuestionNumber++;
                                        mSucceededQuestions++;
                                        mQuestion.SetmSucceeded();
                                        if (isAudio){
                                            mQuestionStatistics.add((AudioRecognitionQuestion)mQuestion);
                                        }
                                        else{
                                            mQuestionStatistics.add((PictureRegocnitionQuestion)mQuestion);
                                        }
                                        getNextQuestion(iImgWord, isAudio);
                                    }
                                    else
                                    {
                                        setBirdAnswerVisibility(imageTryAgain, textTryAgain, iImgWord, iPlay);
                                        mQuestion.IncreasemNumOfTries();
                                    }
                                } catch (JSONException e) {
                                    messageToUser(getString(R.string.error_server));
                                    e.printStackTrace();
                                }
                            }
                        },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int statusCode = error.networkResponse.statusCode;
                        String errorMsgToClient = "";
                        if(statusCode>= 400 && statusCode <= 499)
                        {
                            errorMsgToClient = getString(R.string.error_bad_recording);
                        }
                        else if(statusCode >= 500 && statusCode <= 599)
                        {
                            errorMsgToClient = getString(R.string.error_server);
                        }
                        else{
                            errorMsgToClient = getString(R.string.error_server);
                        }

                        messageToUser(errorMsgToClient);
                        iButton.setText("רוצה לענות!");
                    }
                });
                queue.add(jsonRequest);

            } catch (Exception e) {
                messageToUser(getString(R.string.error_server));
                e.printStackTrace();
            }
        } catch (IOException e) {
            messageToUser(getString(R.string.error_server));
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
                bytes = fileToBytesLowApk();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    private void updateScore(){
        String url = "https://speech-rec-server.herokuapp.com/update_score/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", mId);
            jsonBody.put("add_to_score", mSucceededQuestions);
            final RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("body").toLowerCase().contains("updated score to user")){
                                }
                            } catch (JSONException e) {
                                messageToUser(getString(R.string.server_error_update_score));
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageToUser(getString(R.string.server_error_update_score));
                }
            });
            queue.add(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
            messageToUser(getString(R.string.server_error_update_score));
        }
    }

    private void updateLevel(){
        String url = "https://speech-rec-server.herokuapp.com/update_level/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", mId);
            final RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageToUser(getString(R.string.server_error_update_level));
                }
            });
            queue.add(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
            messageToUser(getString(R.string.server_error_update_level));
        }
    }

    private byte[] fileToBytesLowApk()
    {
        File file = new File(mPathSave);
        int size = (int) file.length();
        byte[] bytes = null;
        try {
            bytes = new byte[size];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bytes = null;
        } catch (IOException e) {
            e.printStackTrace();
            bytes = null;
        }

        return bytes;
    }

    private void showErrorDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameLevel.this);

        builder.setMessage(getString(R.string.server_error_finish_level));
        // add the buttons
        builder.setPositiveButton("הבנתי", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
