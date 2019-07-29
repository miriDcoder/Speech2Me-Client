package com.example.project;

import android.Manifest;
import android.content.Context;
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
import java.util.UUID;
//MISSING:
//handle returning answer from requset if answered false/ requset failed
//return currUser and score to home page
//handle get currUser from getArguments & parceable
//save question statistics in ArrayList<PictureRecognitionQuestion>
//send statistics to server (to teacher)


public class PictureRecognitionLevel extends AppCompatActivity {
    DataBase db = new DataBase();
    private PictureRegocnitionQuestion mQuestion;
    private Student mStudent;
    public QuestionsData questions = new QuestionsData();
    //NEEDS TO GET LEVEL FROM STUDENT
    private int level = 2;
    private int sizeOfLevel = 5;
    private int questionNumber = 1;
    private boolean mIsRecording = false;
    private String mPathSave = "";
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private MediaPlayer mAudioCluePlayer;
    private final int REQUEST_PREMISSION_CODE = 1000;
    private int REQUEST_ANSWER= 200;
    private Question currQuestion;
    private ImageView imgWord;
    private int[] answeredQuestions;
    private Button answer;
    private Button audioClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_recognition_level);
        if(!checkPermissionFromDevice()) {
            requestPermission();
        }
        imgWord = findViewById(R.id.imageViewWord);
        answer = findViewById(R.id.buttonAnswerWordRecognition);
        audioClue = findViewById(R.id.buttonAudioClue);
        questions.makeQuestionList();
        answeredQuestions = new int [questions.getSizeOfLevel(level)];
        initNewQuestion();

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            mIsRecording = !mIsRecording;
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
//                                    isCorrectAnswer(mMediaRecorder, answer);
                        }
                    });
                    thread.start();
                    mMediaRecorder.release();
                    mMediaRecorder = null;

                    mIsRecording = !mIsRecording;
                    //TODO: sent answer to server and get result in REQUEST_ANSWER
                    if (REQUEST_ANSWER == 200) {
                        initNewQuestion();
                    }else{
                        mQuestion.IncreasemNumOfTries();
                    }
                }
            }
        });

        audioClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioCluePlayer = MediaPlayer.create(PictureRecognitionLevel.this, R.raw.boker_tov);
                mAudioCluePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAudioCluePlayer.stop();
                        audioClue.setText("רמז");
                    }
                });
                mAudioCluePlayer.start();
                audioClue.setText("משמיע רמז...");
            }
        });
    }

    private void initNewQuestion(){
        do
        {
            currQuestion = questions.getRandomQuestion(level);
        } while(answeredQuestions[currQuestion.GetmId()] == 1);
        answeredQuestions[currQuestion.GetmId()] = 1;
        mQuestion = new PictureRegocnitionQuestion(currQuestion);
        Picasso.with(PictureRecognitionLevel.this).load(mQuestion.getmImgPath()).into(imgWord);
        answer.setText("ענה");
        questionNumber++;

        //finished level
        if (questionNumber == sizeOfLevel) {
            Toast toast = Toast.makeText(PictureRecognitionLevel.this, String.format("כל הכבוד! סיימת את שלב %d!", level), Toast.LENGTH_LONG);
            for (int i= 0; i< questions.getSizeOfLevel(level);i++){
                answeredQuestions[i] = 0;
            }
            moveToHomePage("0002");
        }
    }

    private void moveToHomePage(String id){
        User currUser = DbUtils.GetUserById(db.makeUserList(), id);
//        if (savedInstanceState==null) {
            if (currUser.getmType()==User.eType.STUDENT){
//                bundle.putParcelable("user",(Student)currUser);
                StudentHomePageFragment studentPage = new StudentHomePageFragment();
//                studentPage.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, studentPage).commit();
            }
            else if (currUser.getmType()==User.eType.TEACHER){
//                bundle.putParcelable("user", (Teacher)currUser);
                TeacherHomePageFragment teacherPage = new TeacherHomePageFragment();
//                teacherPage.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, teacherPage).commit();
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

    private void isCorrectAnswer(MediaRecorder iRecorder, final Button iButton)  {
        String url = "https://speech-rec-server.herokuapp.com/check_talking/";
        File file = new File(mPathSave);
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(mPathSave);
            byte[] bytes = inputStreamToByteArray(inFile);
            bytesToAudio(bytes);
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("original_text", mQuestion.GetmAnswer());
                jsonBody.put("email", "ofer.feder@gmail.com");
                jsonBody.put("audio_file", bytes);
                final RequestQueue queue = Volley.newRequestQueue(this);
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
                System.out.println("+++++++++++++++++++++++" + bytes);

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

    private void bytesToAudio(byte[] bytes) {
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }
        File file = new File(root, "ToAudio");
        try {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX" + file.getAbsolutePath());
            FileWriter fw = new FileWriter(file);
            fw.write(bytes.toString());
            fw.close();
            mMediaPlayer = new MediaPlayer();
                    try
                    {
                        System.out.println(">>>>>>>> ABOUT TO PLAY");
                        mMediaPlayer.setDataSource(file.getAbsolutePath());
                        mMediaPlayer.prepare();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        System.out.println("~~~~~~~~~~~~~~~~~~~MESSAGE:" + e.getMessage());
                    }

                    mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

        public byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
//            System.out.println("---------------------" + baos.toString());
            }

            byte[] result = baos.toByteArray();

            writeToFile(result.toString(), PictureRecognitionLevel.this);
//        System.out.println("---------------------" + baos.toString());

        return result;//baos.toString("UTF-8");
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
