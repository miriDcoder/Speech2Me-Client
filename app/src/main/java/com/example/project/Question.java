package com.example.project;

//This class is the parent of AudioRecognitionQuestion and PictureRecognitionQuestion,
//it assembles all the methods and members that are common in both.
//This way, in the future if we choose to incorporate another type of game - it will be easier.
public class Question {
    private final String mAnswer;
    private final String mImageUrl;
    private final int mAudioRecording;
    private final int mLevel;
    private final int mId;
    private boolean mIsClueUsed = false;
    private int mNumOfTries = 1;
    private int mScore = 0;
    private boolean mSucceeded = false;

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

    public int GetmScore()
    {
        return this.mScore;
    }

    public boolean IsClueUsed()
    {
        return this.mIsClueUsed;
    }

    public boolean GetmSucceeded()
    {
        return this.mSucceeded;
    }

    public void SetmSucceeded()
    {
        this.mSucceeded = true;
    }

    public void SetClueAsUsed()
    {
        this.mIsClueUsed = true;
    }

}
