package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StudentHomePageFragment extends Fragment {

    public DataBase db = new DataBase();

    public StudentHomePageFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_home_page, container, false);
        TextView textViewHello = (TextView)v.findViewById(R.id.textViewHeader);
        TextView textViewScore = (TextView)v.findViewById(R.id.textViewScore);
        TextView textViewLevel = (TextView)v.findViewById(R.id.textViewLevel);
 //       String this_id = this.getArguments().getString("id");
        Student currStudent = (Student)DbUtils.GetUserById(db.makeUserList(), "0005");
        setEditTextsPositions(textViewHello, textViewScore, textViewLevel);
        displayMyInfo(currStudent, textViewHello, textViewScore, textViewLevel);
        Button buttonPlay = (Button)v.findViewById(R.id.buttonPlay);
//        buttonPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return v;
    }

    private void displayMyInfo(Student iStudent, TextView iTextViewHeader, TextView iTextViewScore, TextView iTextViewLevel) {
        iTextViewHeader.setText(String.format("שלום, %s!", iStudent.getmFirstName()));
        iTextViewScore.setText(String.format("ניקוד: %d", iStudent.getmScore()));
        iTextViewLevel.setText(String.format("שלב: %d", iStudent.getmLevel()));
    }

    private void setEditTextsPositions(TextView iTextViewHeader, TextView iTextViewScore, TextView iTextViewLevel){
        iTextViewHeader.setX(10);
        iTextViewHeader.setY(150);
        iTextViewScore.setX(iTextViewHeader.getX());
        iTextViewScore.setY(iTextViewHeader.getY() + iTextViewHeader.getHeight() + 100);
        iTextViewLevel.setX(iTextViewHeader.getX());
        iTextViewLevel.setY(iTextViewScore.getY() + iTextViewScore.getHeight() + 100);
    }
}
