package com.example.project;

//this class handles an audio recognition game single question
public class AudioRecognitionQuestion extends Question{

    private int mAudioPath;
    private String mImageClue;

    public AudioRecognitionQuestion(Question question){
        super(question.GetmAnswer(), question.getmImageUrl(), question.GetmAudioRecording(), question.GetmLevel(),question.GetmId());
        this.mAudioPath = question.GetmAudioRecording();
        this.mImageClue = question.getmImageUrl();
    }
    public int getmAudioPath(){
        return this.mAudioPath;
    }

    public String GetmImageClue()
    {
        return this.mImageClue;
    }

}