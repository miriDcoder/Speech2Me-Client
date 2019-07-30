package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StudentHomePageFragment extends Fragment {
    private Student mStudent;

    public StudentHomePageFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_home_page, container, false);
        TextView textViewHello = (TextView)v.findViewById(R.id.textViewHeader);
        TextView textViewScore = (TextView)v.findViewById(R.id.textViewScore);
        TextView textViewLevel = (TextView)v.findViewById(R.id.textViewLevel);
        if(getArguments() != null)
        {
            mStudent = getArguments().getParcelable("user");
        }
        setEditTextsPositions(textViewHello, textViewScore, textViewLevel);
        displayMyInfo(mStudent, textViewHello, textViewScore, textViewLevel);
        Button buttonPlayWord = (Button)v.findViewById(R.id.buttonPlayWord);
        Button buttonPlayRecord = (Button)v.findViewById(R.id.buttonPlayRecord);
        setPlayButtons(buttonPlayWord, buttonPlayRecord);
        buttonPlayWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PictureRecognitionLevel.class);
                intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                intent.putExtra("type", mStudent.getmType());
                intent.putExtra("id", mStudent.getmId());
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        buttonPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AudioRecognitionLevel.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

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

    private void setPlayButtons(Button iWord, Button iVoice)
    {
        iVoice.setY(iWord.getY() + 400);
    }
}