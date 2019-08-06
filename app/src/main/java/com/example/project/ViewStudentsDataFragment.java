package com.example.project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewStudentsDataFragment extends Fragment {
    private Teacher mTeacher;
    private HashMap<String, String> mStudentIdsToNames = null;
    public ViewStudentsDataFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_students_data, container, false);
        final ScrollView scrollViewDetails = (ScrollView)v.findViewById(R.id.scrollViewDetails);
        scrollViewDetails.setVisibility(View.INVISIBLE);
        final RelativeLayout relativeLayoutDetails = (RelativeLayout)v.findViewById(R.id.relativeLayoutDetails);
        final Spinner spinnerLevel = (Spinner)v.findViewById(R.id.spinnerChooseLevel);
        final Spinner spinnerWord = (Spinner)v.findViewById(R.id.spinnerChooseWord);
        relativeLayoutDetails.setVisibility(View.INVISIBLE);
        setAllStudentDetailsVisibility(View.INVISIBLE, relativeLayoutDetails);

        if(getArguments() != null)
        {
            mTeacher = getArguments().getParcelable("user");
        }
        List<String> studentNames = setStudents();
        final Spinner spinnerChooseStudent = (Spinner)v.findViewById(R.id.spinnerChooseStudent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, studentNames);
        spinnerChooseStudent.setAdapter(adapter);
        spinnerChooseStudent.setSelection(0);

        spinnerChooseStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("############# IN ON ITEM SELECTED");
                System.out.println("#############" + spinnerChooseStudent.getSelectedItem());
                if(spinnerChooseStudent.getSelectedItemPosition() != 0)
                {
                    scrollViewDetails.setVisibility(View.VISIBLE);
                    relativeLayoutDetails.setVisibility(View.VISIBLE);
                    setAllStudentDetailsVisibility(View.VISIBLE, relativeLayoutDetails);
                    setSpinnerLevel(spinnerLevel);
                    setSpinnerWord(spinnerWord);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerWord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private List<String> setStudents()
    {
        String id = " ";
        String studentName = " ";
        mStudentIdsToNames = new HashMap<>();
        List<String> studentNames = new ArrayList();
        ///TODO: get real student;
        for(int i=0; i<=mTeacher.getmNumOfStudents(); i++)
        {
            if(i == 0)
            {
                studentNames.add("בחר תלמיד");
            }
            else
            {
                id = String.valueOf(i);
                studentName = "Student" + id;
                mStudentIdsToNames.put(id, "Student" + id);
                studentNames.add("שי לוי");
            }
        }

        return studentNames;
    }

    private void setAllStudentDetailsVisibility(int iVisibility, RelativeLayout oRelativeLayout)
    {
        int detailsInfoCound = oRelativeLayout.getChildCount();
        for(int i=0; i<detailsInfoCound; i++)
        {
            oRelativeLayout.getChildAt(i).setVisibility(iVisibility);
        }
    }

    private void setSpinnerLevel(Spinner iSpinnerLevel)
    {
        String[] levels = {"1", "2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, levels);
        iSpinnerLevel.setAdapter(adapter);
        iSpinnerLevel.setSelection(0);
    }

    private void setSpinnerWord(Spinner iSpinnerWord)
    {
        String[] levels = {"אבטיח", "בית"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, levels);
        iSpinnerWord.setAdapter(adapter);
        iSpinnerWord.setSelection(0);
    }
}
