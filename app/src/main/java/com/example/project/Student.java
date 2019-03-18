package com.example.project;

public class Student extends User {
    private int mLevel;
    private String mAge;
    private int mScore;
    private String mTeacherId;


    public Student(String iEmail, String iPassword, String iFirstName,
                   String iLastName, String iCity, String iId,
                   int iLevel, String iAge, int iScore,
                   String iTeacherId){
        super(iEmail, iPassword, iFirstName, iLastName, iCity, iId);
        this.mLevel = iLevel;
        this.mAge = iAge;
        this.mScore = iScore;
        this.mTeacherId = iTeacherId;
    }

    public int getmLevel() {
        return mLevel;
    }

    public void setmLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public String getmAge() {
        return mAge;
    }

    public void setmAge(String mAge) {
        this.mAge = mAge;
    }

    public int getmScore() {
        return mScore;
    }

    public void setmScore(int mScore) {
        this.mScore = mScore;
    }

    public String getmTeacherId() {
        return mTeacherId;
    }

    public void setmTeacherId(String mTeacherId) {
        this.mTeacherId = mTeacherId;
    }
}
