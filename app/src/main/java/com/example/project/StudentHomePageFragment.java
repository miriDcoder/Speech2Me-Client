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
import android.widget.ImageView;
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
        TextView textViewYourScore = (TextView)v.findViewById(R.id.textViewYourScore);
        TextView textViewScore = (TextView)v.findViewById(R.id.textViewScore);
        TextView textViewLevel = (TextView)v.findViewById(R.id.textViewLevel);
        ImageView imageViewBirdWelcome = (ImageView)v.findViewById(R.id.imageViewBirdWelcome);
        ImageView imageViewScoreBackground = (ImageView)v.findViewById(R.id.imageViewScoreBackground);
        Button buttonPlay = (Button)v.findViewById(R.id.buttonPlay);
        Button buttonPlayWord = (Button)v.findViewById(R.id.buttonPlayWord);
        Button buttonPlayRecord = (Button)v.findViewById(R.id.buttonPlayRecord);

        if(getArguments() != null)
        {
            mStudent = getArguments().getParcelable("user");
        }
//        setPositions(textViewHello, textViewYourScore, textViewScore, textViewLevel, imageViewBirdWelcome, imageViewScoreBackground,
//                buttonPlayWord, buttonPlayRecord);
        displayMyInfo(mStudent, textViewHello, textViewYourScore, textViewScore ,textViewLevel);

//        setPlayButtons(buttonPlayWord, buttonPlayRecord);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch(mStudent.getmGameType())
                {
                    case 1:
                        intent = new Intent(getActivity(), AudioRecognitionLevel.class);
                        intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                        intent.putExtra("id", mStudent.getmId());
                        startActivity(intent);
                        ((Activity) getActivity()).overridePendingTransition(0, 0);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), PictureRecognitionLevel.class);
                        intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                        intent.putExtra("id", mStudent.getmId());
                        startActivity(intent);
                        ((Activity) getActivity()).overridePendingTransition(0, 0);
                        break;
                }
//                intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
//                intent.putExtra("id", mStudent.getmId());
//                startActivity(intent);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

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

    private void displayMyInfo(Student iStudent, TextView iTextViewHeader, TextView iTextViewYourScore, TextView iTextViewScore, TextView iTextViewLevel) {
        iTextViewHeader.setText(String.format("שלום, %s!", iStudent.getmFirstName()));
        iTextViewYourScore.setText(String.format("הניקוד שלך"));
        //iTextViewScore.setText(String.format("%d", iStudent.getmScore()));
        iTextViewScore.setText(String.format("%d", mStudent.getmScore()));
        iTextViewLevel.setText(String.format("שלב %d מתוך 10", iStudent.getmLevel()));
    }

    private void setPositions(TextView iTextViewHeader,TextView iTextViewYourScore, TextView iTextViewScore, TextView iTextViewLevel,ImageView iImageViewBirdWelcome,
                                       ImageView iImageViewScoreBackground, Button iButtonPlayWord, Button iButtonPlayRecord){
        iTextViewHeader.setX(10);
        iTextViewHeader.setY(50);
        iTextViewYourScore.setX(10);
        iTextViewYourScore.setY(340);
        iTextViewScore.setX(10);
        iTextViewScore.setY(420);
        iTextViewLevel.setX(10);
        iTextViewLevel.setY(600);
        iImageViewBirdWelcome.setX(630);
        iImageViewBirdWelcome.setY(350);
//        iImageViewScoreBackground.setX(10);
        iImageViewScoreBackground.setY(250);
        iButtonPlayWord.setX(40);
        iButtonPlayWord.setY(250);
        iButtonPlayRecord.setX(40);
//        iButtonPlayRecord.setY();
    }

    private void setPlayButtons(Button iWord, Button iVoice)
    {
        iVoice.setY(iWord.getY() + 400);
    }
}