package com.example.project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class WordRecognitionGame extends AppCompatActivity {
    private WordRecognition mLevel;
    private boolean mIsRecording = false;
    private String mPathSave = "";
    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayer;
    final int REQUEST_PREMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_recognition_game);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        ImageView imgWord = (ImageView)findViewById(R.id.imageViewWord);
        final Button answer = (Button)findViewById(R.id.buttonAnswerWordRecognition);
        final Button wordClue = (Button)findViewById(R.id.buttonWordClue);
        Button audioClue = (Button)findViewById(R.id.buttonAudioClue);

        mLevel = new WordRecognition();
        mLevel.SetmImgPath("https://image.freepik.com/free-vector/little-white-house_88088-10.jpg");
        Picasso.with(this).load(mLevel.getmImgPath()).into(imgWord);
        mLevel.SetmWordClue("המשפחה גרה בו");
        mLevel.SetWord("בית");
        //System.out.println("~~~~~~~~is word clue used: " + mLevel.IsWordClueUsed());

        wordClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLevel.WordClueUsed();
                wordClue.setEnabled(false);
                wordClue.setText(mLevel.GetmWordClue());
                //System.out.println("~~~~~~~~is word clue used: " + mLevel.IsWordClueUsed());
            }
        });

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsRecording) //start recording
                {
                    answer.setText("עצור הקלטה");
                    if (checkPermissionFromDevice()) {
                        mPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "_audio_record.mp3";
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + mPathSave);
                        setupMediaRecorder();
                        try {
                            mMediaRecorder.prepare();
                            mMediaRecorder.start();
                            mIsRecording = !mIsRecording;
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        requestPermission();
                    }

                }
                else
                {
                    answer.setText("אנא המתן");
                    mMediaRecorder.stop();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            isCorrectAnswer(mMediaRecorder, answer);
                        }
                    });
                    thread.start();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
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
                    mIsRecording = !mIsRecording;
                    //TODO: send the recording to relevant things etc...
                }

            }
        });

//        audioClue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MediaPlayer ring= MediaPlayer.create(WordRecognitionGame.this,R.raw.ring);
//                ring.start();
//            }
//        });
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

    private void isCorrectAnswer(MediaRecorder iRecorder, final Button iButton)  {
        String url = "https://speech-rec-server.herokuapp.com/check_talking/";
        File file = new File(mPathSave);
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(mPathSave);
            String bytes = inputStreamToByteArray(inFile);
            System.out.println("+++++++++++++++++++++++" + bytes);
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("original_text", mLevel.GetWord());
                jsonBody.put("email", "ofer.feder@gmail.com");
                jsonBody.put("audio_file", bytes);
                final RequestQueue queue = Volley.newRequestQueue(this);
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
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

    public String inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
            System.out.println("---------------------" + baos.toString());
        }

        return baos.toString();
    }
}
