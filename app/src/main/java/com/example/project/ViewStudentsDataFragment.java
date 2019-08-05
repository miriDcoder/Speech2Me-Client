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
        if(getArguments() != null)
        {
            mTeacher = getArguments().getParcelable("user");
        }
        List<String> studentNames = setStudents();
        final Spinner spinnerChooseStudent = (Spinner)v.findViewById(R.id.spinnerChooseStudent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, studentNames);
        spinnerChooseStudent.setAdapter(adapter);

        spinnerChooseStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                studentNames.add(studentName);
            }
        }

        return studentNames;
    }
}
