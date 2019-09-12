package com.example.project;

//This is an abstract class for the types of users (currently - student and teacher) to inherit from.
//It assembles the common members and methods to all users.
public abstract class User {
    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mCity;
    private String mId;
    private String mType;

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

    public User(String iFirstName, String iLastName, String iId,
                String iType)
    {
        this.mFirstName = iFirstName;
        this.mLastName = iLastName;
        this.mId = iId;
        this.mType = iType;
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

    public String getmType(){
        return mType;
    }

    public void setmType(String mType){
        this.mType = mType;
    }
}
