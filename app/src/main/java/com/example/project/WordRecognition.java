package com.example.project;

public class WordRecognition {
    private String mImgPath;
    private String mWordClue;
    private String mAudioClue;
    private int mNumOfTries = 0;
    private boolean mIsWordClueUsed = false;
    private boolean mIsAudioClueUsed = false;
    private String mWord;

    public String getmImgPath(){
        return mImgPath;
    }

    public void SetmImgPath(String iPath)
    {
        mImgPath = iPath;
    }

    public String GetmWordClue(){
        return mWordClue;
    }

    public void SetmWordClue(String iWordClue)
    {
        mWordClue = iWordClue;
    }

    public String GetmAudioClue()
    {
        return mAudioClue;
    }

    public void SetmAudioClue(String iAudioClue)
    {
        mAudioClue = iAudioClue;
    }

    public int GetmNumOfTries()
    {
        return mNumOfTries;
    }

    public void IncreaseNumOfTries()
    {
        mNumOfTries++;
    }

    public boolean IsWordClueUsed()
    {
        return mIsWordClueUsed;
    }

    public void WordClueUsed(){
        mIsWordClueUsed = true;
    }

    public boolean IsAudioClueUsed()
    {
        return mIsAudioClueUsed;
    }

    public void AudioClueUsed()
    {
        mIsAudioClueUsed = true;
    }

    public String GetWord(){ return mWord;}
    public void SetWord(String iWord) {mWord = iWord;}
}