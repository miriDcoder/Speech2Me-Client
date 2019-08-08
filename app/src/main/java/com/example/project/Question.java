package com.example.project;

import android.net.Uri;

public class Question {
    private final String mAnswer;
    private final String mImageUrl;
    private final int mAudioRecording;
    private final int mLevel;
    private final int mId;
    private boolean mIsClueUsed = false;
    private int mNumOfTries = 0;
    private int mScore = 0;

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

    public int GetmNumOfTries()
    {
        return this.mNumOfTries;
    }

    public void IncreasemNumOfTries()
    {
        this.mNumOfTries++;
    }

    public void SetmScore(int iScore)
    {
        this.mScore = iScore;
    }

    public void IncreasemScore()
    {
        this.mScore ++;
    }

    public int GetmScore()
    {
        return this.mScore;
    }
    public boolean IsClueUsed()
    {
        return this.mIsClueUsed;
    }

    public void SetClueAsUsed()
    {
        this.mIsClueUsed = true;
    }

}
