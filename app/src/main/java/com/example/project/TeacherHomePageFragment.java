package com.example.project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TeacherHomePageFragment extends Fragment {
    private Teacher mTeacher;
    public DataBase db = new DataBase();
    private Intent selectedIntent = null;
    public TeacherHomePageFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_home_page, container, false);
        TextView textViewHello = (TextView)v.findViewById(R.id.textViewHeader);
        TextView textViewAmountOfStudents= (TextView)v.findViewById(R.id.textViewAmountOfStudents);
        TextView textViewWatchStatistics = (TextView)v.findViewById(R.id.textViewWatchStatistics);
        TextView textViewYourAmountOfStudents = (TextView)v.findViewById(R.id.textViewYourAmountOfStudents);
        ImageView imageViewGameBackground = (ImageView)v.findViewById(R.id.imageViewGameBackground);
        ImageView imageViewProgressBackground = (ImageView)v.findViewById(R.id.imageViewProgressBackground);
        TextView textViewTeacherCode = (TextView)v.findViewById(R.id.textViewTeacherCodeData);
        TextView textViewCodeList = (TextView)v.findViewById(R.id.textViewCodeList);
        RadioGroup radioGame = (RadioGroup)v.findViewById(R.id.radioGame);
        TextView buttonPlay = (TextView) v.findViewById(R.id.buttonPlay);
        TextView textViewIdCode = (TextView)v.findViewById(R.id.textViewTeacherCodeInfo);
        final Dialog teacherCodeDialog = new Dialog(getContext());
        TextView teacherCodeMsg = new TextView(getContext());
        final Dialog codesDialog = new Dialog(getContext());
        TextView instructionsMsg = new TextView(getContext());
        final Spinner spinnerLevel = (Spinner)v.findViewById(R.id.spinner);
        String[] items = new String[]{"שלב", "1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinnerLevel.setAdapter(adapter);
        if(getArguments() != null)
        {
            mTeacher = getArguments().getParcelable("user");
        }
//        setPositions(textViewHello, textViewYourAmountOfStudents, textViewAmountOfStudents, textViewWatchStatistics,
//                imageViewProgressBackground, radioGame, spinnerLevel, buttonPlay);
        displayMyInfo(mTeacher, textViewHello, textViewAmountOfStudents, textViewTeacherCode);
        setInstructionsDialog(codesDialog, instructionsMsg);
        setTeacherCodeDialog(teacherCodeDialog, teacherCodeMsg);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String level = String.valueOf(spinnerLevel.getSelectedItem());
                if (level == "שלב" || selectedIntent == null) {
                    if (selectedIntent == null) {
                        String chooseGame = "אנא בחר/י משחק";
                        Toast toast = Toast.makeText(getContext(), chooseGame, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if (level == "שלב") {
                        String chooseLevel = "אנא בחר/י שלב";
                        Toast toast = Toast.makeText(getContext(), chooseLevel, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    selectedIntent.putExtra("level", level);
                    selectedIntent.putExtra("id", mTeacher.getmId());
                    selectedIntent.putExtra("user_type", mTeacher.getmType());
                    startActivity(selectedIntent);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);
                }
            }
        });

        textViewWatchStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mTeacher);
                ViewStudentsDataFragment viewStudentsData = new ViewStudentsDataFragment();
                viewStudentsData.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.student_fragment_container, viewStudentsData).commit();
            }
        });

        radioGame.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch(checkedId) {
                    case R.id.radioPictureGame:
                        selectedIntent = new Intent(getActivity(), PictureRecognitionLevel.class);
                        break;
                    case R.id.radioAudioGame:
                        selectedIntent = new Intent(getActivity(), AudioRecognitionLevel.class);
                        break;
                }
            }
        });

        textViewCodeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codesDialog.show();
            }
        });

        textViewIdCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherCodeDialog.show();
            }
        });
        return v;
    }

    private void setTeacherCodeDialog(Dialog iTeacherCodeDialog, TextView iTeacherCodeMsg) {
        String msg = " ";
        iTeacherCodeDialog.setTitle(getResources().getString(R.string.game_instructions_header));
        iTeacherCodeMsg.setGravity(Gravity.RIGHT);
        msg = String.format("%s", getResources().getString(R.string.teacher_code_info));
        iTeacherCodeMsg.setText(msg);
        iTeacherCodeDialog.setContentView(iTeacherCodeMsg);
    }


    private void displayMyInfo(Teacher iTeacher, TextView iTextViewHeader, TextView iTextViewAmountOfStudents,
                               TextView iTextViewTeacherCode) {
        iTextViewHeader.setText(String.format("שלום, %s!", iTeacher.getmFirstName()));
        iTextViewAmountOfStudents.setText(String.format("%d", iTeacher.getmNumOfStudents()));
        iTextViewTeacherCode.setText(iTeacher.getmId());
    }

    public void onRadioButtonClicked(View v){
        // Check which radio button was clicked
        boolean checked = ((RadioButton)v).isChecked();
        switch(v.getId()) {
            case R.id.radioPictureGame:
                if (checked)
                    selectedIntent = new Intent(getActivity(), PictureRecognitionLevel.class);
                    break;
            case R.id.radioAudioGame:
                if (checked)
                    selectedIntent = new Intent(getActivity(), AudioRecognitionLevel.class);
                    break;
        }
    }

    private void setPositions(TextView iTextViewHeader, TextView iTextViewYourAmountOfStudents, TextView iTextViewAmountOfStudents, TextView iTextViewWatchStatistics,
                              ImageView iImageViewProgressBackground, RadioGroup iRadioGame, Spinner iSpinnerLevel, TextView iTextViewPlay){
        iTextViewHeader.setX(10);
        iTextViewHeader.setY(50);
        iTextViewYourAmountOfStudents.setX(10);
        iTextViewYourAmountOfStudents.setY(400);
        iTextViewAmountOfStudents.setX(10);
        iTextViewAmountOfStudents.setY(420);
        iTextViewWatchStatistics.setX(-200);
        iTextViewWatchStatistics.setY(600);
        iImageViewProgressBackground.setY(250);
        iRadioGame.setX(-40);
        iRadioGame.setY(1000);
        iSpinnerLevel.setX(-200);
        iSpinnerLevel.setY(500);
//        iButtonPlay.setX(40);
//        iButtonPlay.setY(250);
    }

    private void setInstructionsDialog(Dialog iInstructions, TextView iInstructionsMsg) {
        String msg = " ";
        iInstructions.setTitle(getResources().getString(R.string.game_instructions_header));
        iInstructionsMsg.setGravity(Gravity.RIGHT);
        msg = String.format("%s\n%s\n%s", getResources().getString(R.string.teacher_instructions),
                getResources().getString(R.string.goal_audio_with_code),
                getResources().getString(R.string.goal_picture_with_code));
        iInstructionsMsg.setText(msg);
        iInstructions.setContentView(iInstructionsMsg);
    }

}