package com.example.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;


public class MyStudentProfile extends AppCompatActivity {

    private Student mStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_student_profile);
    }

    public MyStudentProfile(Student iStudent){
        this.mStudent = iStudent;
    }

    private void displayMyInfo(){
        final EditText editTextMyInfo = (EditText) findViewById(R.id.editTextMyInfo);
        String myInfo = String.format("Hello, %d! /nLevel: %d/nScore: %d/nKeep up the good work!",
                mStudent.getmFirstName(), mStudent.getmLevel(), mStudent.getmScore());

       // string myInfo= "Hello, " + mStudent.getmFirstName();
        editTextMyInfo.setText(myInfo);
    }
}
