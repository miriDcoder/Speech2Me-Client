package com.example.project;

public class WordRecognition {
    private String mImgPath;
    private String mWordClue;
    private String mAudioClue;
    private int mNumOfTries = 0;
    private int mScore = 0;
    private boolean mIsWordClueUsed = false;
    private boolean mIsAudioClueUsed = false;
    private String mWord;

    public String getmImgPath(){
        return this.mImgPath;
    }

    public void SetmImgPath(String iPath)
    {
        this.mImgPath = iPath;
    }

    public String GetmWordClue(){
        return this.mWordClue;
    }

    public void SetmWordClue(String iWordClue)
    {
        this.mWordClue = iWordClue;
    }

    public String GetmAudioClue()
    {
        return this.mAudioClue;
    }

    public void SetmAudioClue(String iAudioClue)
    {
        this.mAudioClue = iAudioClue;
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

    public boolean IsWordClueUsed()
    {
        return this.mIsWordClueUsed;
    }

    public void WordClueUsed(){
        this.mIsWordClueUsed = true;
    }

    public boolean IsAudioClueUsed()
    {
        return this.mIsAudioClueUsed;
    }

    public void AudioClueUsed()
    {
        this.mIsAudioClueUsed = true;
    }

    public void SetmWord(String iWord)
    {
        this.mWord = iWord;
    }

    public String GetmWord()
    {
        return this.mWord;
    }
}