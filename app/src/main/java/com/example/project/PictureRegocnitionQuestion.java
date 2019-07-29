package com.example.project;

public class PictureRegocnitionQuestion {
    private String mImgPath;
    private String mAudioClue;
    private int mNumOfTries = 0;
    private int mScore = 0;
    private boolean mIsAudioClueUsed = false;
    private String mAnswer;

    public PictureRegocnitionQuestion(Question question){
        this.mImgPath = question.getmImageUrl();
        this.mAudioClue = question.GetmAudioRecording();
        this.mAnswer = question.GetmAnswer();

    }
    public String getmImgPath(){
        return this.mImgPath;
    }

    public String GetmAudioClue()
    {
        return this.mAudioClue;
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

    public void SetmScore(int iScore)
    {
        this.mScore = iScore;
    }

    public boolean IsAudioClueUsed()
    {
        return this.mIsAudioClueUsed;
    }

    public void AudioClueUsed()
    {
        this.mIsAudioClueUsed = true;
    }

    public String GetmAnswer()
    {
        return this.mAnswer;
    }
}