package com.example.project;

import android.net.Uri;

public class Question {
    private final String mAnswer;
    private final String mImageUrl;
    private final int mAudioRecording;
    private final int mLevel;
    private final int mId;

    public Question(String iAnswer, String iImageUrl, int iAudioRecording, int iLevel, int iId){
        this.mAnswer = iAnswer;
        this.mImageUrl = iImageUrl;
        this.mAudioRecording = iAudioRecording;
        this.mLevel = iLevel;
        this.mId = iId;
    }

    public String getmImageUrl(){
        return this.mImageUrl;
    }

    public String GetmAnswer(){
        return this.mAnswer;
    }

    public int GetmAudioRecording()
    {
        return this.mAudioRecording;
    }

    public int GetmLevel()
    {
        return this.mLevel;
    }

    public int GetmId()
    {
        return this.mId;
    }

    //    public void SetmAudioRecording (String iAudioRecording)
//    {
//        this.mAudioRecording = iAudioRecording;
//    }
    //    public void SetmAnswer(String iAnswer)
//    {
//        this.mAnswer = iAnswer;
//    }
    //    public void SetmImageUrl(String iPath)
//    {
//        this.mImageUrl = iPath;
//    }
}
