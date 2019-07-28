package com.example.project;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Teacher extends User implements Parcelable {

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

    protected Teacher(Parcel in) {
        mNumOfStudents = in.readInt();
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNumOfStudents);
    }
}
