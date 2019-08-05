package com.example.project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;


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
        setStudents();
        //ArrayAdapter<HashMap> adapter = getAdapter();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, (String[]) mStudentIdsToNames.values().toArray());

        return v;
    }

    private void setStudents()
    {
        mStudentIdsToNames = new HashMap<>();
        ///TODO: get real student;
        mStudentIdsToNames.put("1", "משה כהן");
        mStudentIdsToNames.put("2", "ליאת אחירון");
        mStudentIdsToNames.put("3", "מיה קולוצ'י");
        mStudentIdsToNames.put("4", "ג'סטין טימברלייק");
    }
}
