package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class StudentHomePageFragment extends Fragment {
    private Student mStudent;

    public StudentHomePageFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_home_page, container, false);


        TextView textViewHello = (TextView)v.findViewById(R.id.textViewHeader);
        TextView textViewScore = (TextView)v.findViewById(R.id.textViewScore);
        TextView textViewLevel = (TextView)v.findViewById(R.id.textViewLevel);
        ImageView imageViewBirdWelcome = (ImageView)v.findViewById(R.id.imageViewBirdWelcome);
        ImageView imageViewScoreBackground = (ImageView)v.findViewById(R.id.imageViewScoreBackground);
        Button buttonPlayWord = (Button)v.findViewById(R.id.buttonPlayWord);
        Button buttonPlayRecord = (Button)v.findViewById(R.id.buttonPlayRecord);

        if(getArguments() != null)
        {
            mStudent = getArguments().getParcelable("user");
        }
        setPositions(textViewHello, textViewScore, textViewLevel, imageViewBirdWelcome, imageViewScoreBackground,
                buttonPlayWord, buttonPlayRecord);
        displayMyInfo(mStudent, textViewHello, textViewScore, textViewLevel);

        setPlayButtons(buttonPlayWord, buttonPlayRecord);
        buttonPlayWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PictureRecognitionLevel.class);
                intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                intent.putExtra("id", mStudent.getmId());
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        buttonPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AudioRecognitionLevel.class);
                intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                intent.putExtra("id", mStudent.getmId());
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

    private void setPositions(TextView iTextViewHeader, TextView iTextViewScore, TextView iTextViewLevel,ImageView iImageViewBirdWelcome,
                                       ImageView iImageViewScoreBackground, Button iButtonPlayWord, Button iButtonPlayRecord){
        iTextViewHeader.setX(10);
        iTextViewHeader.setY(350);
//        iTextViewScore.setX(10);
//        iTextViewScore.setY(430);
//        iTextViewLevel.setX(10);
//        iTextViewLevel.setY(510);
//        iImageViewBirdWelcome.setX(630);
//        iImageViewBirdWelcome.setY(400);
//        iImageViewScoreBackground.setX(40);
//        iImageViewScoreBackground.setY(340);
//        iButtonPlayWord.setX(40);
//        iButtonPlayWord.setY(250);
//        iButtonPlayRecord.setX(40);
//        iButtonPlayRecord.setY();


    }

    private void setPlayButtons(Button iWord, Button iVoice)
    {
        iVoice.setY(iWord.getY() + 400);
    }
}