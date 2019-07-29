package com.example.project;

public class AudioRecognitionQuestion {
    private String mImgPath;
    private int mNumOfTries = 0;
    private int mScore = 0;

    public String getmImgPath(){
        return this.mImgPath;
    }

    public void SetmImgPath(String iPath)
    {
        this.mImgPath = iPath;
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

}