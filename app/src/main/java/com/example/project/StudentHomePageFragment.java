package com.example.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StudentHomePageFragment extends Fragment {
    private Student mStudent;
    private final String AudioRecognitionGoal = "1";
    private final String PictureRecognitionGoal = "2";

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
        Button buttonInstructions = (Button)v.findViewById(R.id.buttonGameInstructions);
        //final AlertDialog instructions = new AlertDialog(getContext());
        //TextView instructionsMsg = new TextView(getContext());
        if(getArguments() != null)
        {
            mStudent = getArguments().getParcelable("user");
        }
        displayMyInfo(mStudent, textViewHello, textViewYourScore, textViewScore ,textViewLevel);
        //setInstructionsDialog(instructions, instructionsMsg);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch(mStudent.getmGoal())
                {
                    case "1":
                        intent = new Intent(getActivity(), AudioRecognitionLevel.class);
                        intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                        intent.putExtra("id", mStudent.getmId());
                        intent.putExtra("user_type", mStudent.getmType());
                        startActivity(intent);
                        ((Activity) getActivity()).overridePendingTransition(0, 0);
                        break;
                    case "2":
                        intent = new Intent(getActivity(), PictureRecognitionLevel.class);
                        intent.putExtra("level", Integer.toString(mStudent.getmLevel()));
                        intent.putExtra("id", mStudent.getmId());
                        intent.putExtra("user_type", mStudent.getmType());
                        startActivity(intent);
                        ((Activity) getActivity()).overridePendingTransition(0, 0);
                        break;
                }
            }
        });

        buttonInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                String msg;
                switch (mStudent.getmGoal())
                {
                    case "1":
                        msg = getResources().getString(R.string.game_instructions_audio);
                        break;
                    case "2":
                        msg = getResources().getString(R.string.game_instructions_picture);
                        break;
                    default:
                        msg = getResources().getString(R.string.error_msg);
                }
                builder.setMessage(msg).setTitle(getResources().getString(R.string.game_instructions_header));
                builder.setPositiveButton("הבנתי", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return v;
    }

    private void displayMyInfo(Student iStudent, TextView iTextViewHeader, TextView iTextViewYourScore, TextView iTextViewScore, TextView iTextViewLevel) {
        iTextViewHeader.setText(String.format("שלום, %s!", iStudent.getmFirstName()));
        iTextViewYourScore.setText(String.format("הניקוד שלך"));
        iTextViewScore.setText(String.format("%d", mStudent.getmScore()));
        iTextViewLevel.setText(String.format("שלב %d מתוך 10", iStudent.getmLevel()));
    }

}