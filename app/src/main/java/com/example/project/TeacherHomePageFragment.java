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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class TeacherHomePageFragment extends Fragment {
    private Teacher mTeacher;
    public DataBase db = new DataBase();

    public TeacherHomePageFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_home_page, container, false);
        TextView textViewHello = (TextView)v.findViewById(R.id.textViewHeader);
        final Spinner levelDropDown = (Spinner)v.findViewById(R.id.spinner1);
        String[] items = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        levelDropDown.setAdapter(adapter);
        if(getArguments() != null)
        {
            mTeacher = getArguments().getParcelable("user");
        }
        setEditTextsPositions(textViewHello);
        displayMyInfo(mTeacher, textViewHello);
        Button buttonStatistics = (Button)v.findViewById(R.id.buttonStatistics);
        Button buttonPlayWord = (Button)v.findViewById(R.id.buttonPlayWord);
        Button buttonPlayRecord = (Button)v.findViewById(R.id.buttonPlayRecord);
        setPlayButtons(buttonPlayWord, buttonPlayRecord);

        buttonStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonPlayWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String level = String.valueOf(levelDropDown.getSelectedItem());
                Intent intent = new Intent(getActivity(), PictureRecognitionLevel.class);
                intent.putExtra("level", level);
                intent.putExtra("id", mTeacher.getmId());
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        buttonPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String level = String.valueOf(levelDropDown.getSelectedItem());
                Intent intent = new Intent(getActivity(), AudioRecognitionLevel.class);
                intent.putExtra("level", level);
                intent.putExtra("id", mTeacher.getmId());
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        return v;
    }


    private void displayMyInfo(Teacher iTeacher, TextView iTextViewHeader) {
        iTextViewHeader.setText(String.format("שלום, %s!", iTeacher.getmFirstName()));
    }

    private void setEditTextsPositions(TextView iTextViewHeader){
        iTextViewHeader.setX(10);
        iTextViewHeader.setY(150);
//        iTextViewScore.setX(iTextViewHeader.getX());
//        iTextViewScore.setY(iTextViewHeader.getY() + iTextViewHeader.getHeight() + 100);
//        iTextViewLevel.setX(iTextViewHeader.getX());
//        iTextViewLevel.setY(iTextViewScore.getY() + iTextViewScore.getHeight() + 100);
    }

    private void setPlayButtons(Button iWord, Button iVoice)
    {
        iVoice.setY(iWord.getY() + 400);
    }
}