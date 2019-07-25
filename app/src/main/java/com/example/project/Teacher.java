package com.example.project;

import java.io.Serializable;
import java.util.ArrayList;

public class Teacher extends User {

    private ArrayList<Student> mStudents;
    private int mNumOfStudents;
    private eType mType = eType.TEACHER;

    public Teacher(String iEmail, String iPassword, String iFirstName,
                   String iLastName, String iCity, String iId)
    {
        super(iEmail, iPassword, iFirstName, iLastName, iCity, iId);
        this.mStudents = new ArrayList<Student>();
        this.mNumOfStudents = 0;
        setmType(eType.TEACHER);
    }

    public Teacher(String iEmail, String iPassword, String iFirstName,
                   String iLastName)
    {
        super(iEmail, iPassword, iFirstName, iLastName, "", "");
        this.mStudents = new ArrayList<Student>();
        this.mNumOfStudents = 0;
    }

    public int getmNumOfStudents() {
        return mNumOfStudents;
    }

    public void setmNumOfStudents(int mNumOfStudents) {
        this.mNumOfStudents = mNumOfStudents;
    }

    public ArrayList<Student> getmStudents() {
        return mStudents;
    }

    public void setmStudents(ArrayList<Student> mStudents) {
        this.mStudents = mStudents;
    }
}
