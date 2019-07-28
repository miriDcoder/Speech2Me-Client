package com.example.project;

public abstract class User {
    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mCity;
    private String mId;
    private eType mType;

    enum eType{TEACHER, STUDENT}

    public User()
    {

    }

    public User(String iEmail, String iPassword, String iFirstName,
                String iLastName, String iCity, String iId)
    {
        this.mEmail = iEmail;
        this.mPassword = iPassword;
        this.mFirstName = iFirstName;
        this.mLastName = iLastName;
        this.mCity = iCity;
        this.mId = iId;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public eType getmType(){
        return mType;
    }

    public void setmType(eType mType){
        this.mType=mType;
    }
}
