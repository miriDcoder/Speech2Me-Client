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
//
//public class AudioRecognitionQuestion {
//
//    private String mAudioPath;
//    private String mImageClue;
//    private int mNumOfTries = 0;
//    private int mScore = 0;
//    private boolean mIsAudioClueUsed = false;
//    private String mAnswer;
//
//    public AudioRecognitionQuestion(Question question){
//        this.mAudioPath = question.GetmAudioRecording();
//        this.mImageClue = question.getmImageUrl();
//        this.mAnswer = question.GetmAnswer();
//    }
//    public String getmAudioPath(){
//        return this.mAudioPath;
//    }
//
//    public String GetmImageClue()
//    {
//        return this.mImageClue;
//    }
//
//    public int GetmNumOfTries()
//    {
//        return this.mNumOfTries;
//    }
//
//    public void IncreasemNumOfTries()
//    {
//        this.mNumOfTries++;
//    }
//
//    public int GetmScore()
//    {
//        return this.mScore;
//    }
//
//    public void SetmScore(int iScore)
//    {
//        this.mScore = iScore;
//    }
//
//    public boolean IsImageClueUsed()
//    {
//        return this.mIsAudioClueUsed;
//    }
//
//    public void SetImageClueAsUsed()
//    {
//        this.mIsAudioClueUsed = true;
//    }
//
//    public String GetmAnswer()
//    {
//        return this.mAnswer;
//    }
//}