package com.example.project;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: rearrange set spinners functions
public class ViewStudentsDataFragment extends Fragment {
    private Teacher mTeacher;
    private HashMap<String, MiniStudent> mStudentIdsToNames = null;
    //private HashMap<String, String> mStudentIdsToNames = null;
    private List<String> mStudentNames = null;
    private Spinner spinnerLevel;
//    private ScrollView scrollViewDetails;
//    private RelativeLayout relativeLayoutDetails;
//    private Spinner spinnerLevel;
//    private Spinner spinnerWord;
    private Spinner spinnerChooseStudent;
    private TableLayout tableData;
//    private TextView goalInfo;
//    private TextView currLevel;

    public ViewStudentsDataFragment() {
    }


    public class MiniStudent{
        public String Id;
        public String Name;
        public String Goal;
        public String CurrLevel;

        public MiniStudent(){

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_students_data, container, false);
        //scrollViewDetails = (ScrollView)v.findViewById(R.id.scrollViewDetails);
        //scrollViewDetails.setVisibility(View.INVISIBLE);
        //relativeLayoutDetails = (RelativeLayout)v.findViewById(R.id.relativeLayoutDetails);
        //spinnerLevel = (Spinner)v.findViewById(R.id.spinnerChooseLevel);
        //spinnerWord = (Spinner)v.findViewById(R.id.spinnerChooseWord);
        spinnerChooseStudent = (Spinner)v.findViewById(R.id.spinnerChooseStudent);
        tableData = (TableLayout)v.findViewById(R.id.tableData);
        //relativeLayoutDetails.setVisibility(View.INVISIBLE);
        //setAllStudentDetailsVisibility(View.INVISIBLE, relativeLayoutDetails);
        //goalInfo = (TextView)v.findViewById(R.id.textViewStudentGoalInfo);
        //currLevel = (TextView)v.findViewById(R.id.textViewStudentCurrLevelInfo);

        if(getArguments() != null)
        {
            mTeacher = getArguments().getParcelable("user");
        }
        //getStudents(scrollViewDetails, relativeLayoutDetails, spinnerLevel, spinnerWord);
        getStudents();

        return v;
    }

    private List<String> setStudents()
    {
        boolean isNeedToAddChooseStudent = true;
        String id = " ";
        String studentName = " ";
        JSONObject jsonObject = null;
        ArrayList<String> studentNames = new ArrayList<String>();
        List<String> ids = new ArrayList<String>(mStudentIdsToNames.keySet());
        System.out.println("SIZE OF ids: " + mStudentIdsToNames.keySet().size());
        studentNames.add("בחר תלמיד");
        for(int i=0; i<mStudentIdsToNames.size(); i++)
        {
            id = ids.get(i);
            System.out.println("############## ID: " + mStudentIdsToNames.get(id));
            studentName = mStudentIdsToNames.get(id).Name;
            System.out.println("######### goal: " + mStudentIdsToNames.get(id).Goal);
            studentNames.add(studentName);
        }

        return studentNames;
    }

    private void setAllStudentDetailsVisibility(int iVisibility, RelativeLayout oRelativeLayout)
    {
        int detailsInfoCount = oRelativeLayout.getChildCount();
        for(int i=0; i<detailsInfoCount; i++)
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

//    private void getStudents(final ScrollView iScrollViewDetails, final RelativeLayout iRelativeLayoutDetails, final Spinner iSpinnerLevel,
//                             final Spinner iSpinnerWord)
    private void getStudents()
    {
        String url = "https://speech-rec-server.herokuapp.com/get_students_of_teacher/";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", mTeacher.getmId());
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            System.out.println("+++++++++++++++++++++++" + jsonBody);

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("****************************" + response);
                            try {
                                JSONArray students = response.getJSONArray("students");
                                setIdToStudents(students);
                                mStudentNames = setStudents();
                                setSpinners();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERROR!" + error.getMessage());
                }
            });
            queue.add(jsonRequest);
            System.out.println("###############SENT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLevelWords(String iLevel, String iStudentId) {
        String url = "";//TODO
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("level", iLevel);
            jsonBody.put("student_id", iStudentId);
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
            System.out.println("+++++++++++++++++++++++" + jsonBody);

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("****************************" + response);
                            //DO SOMETHING
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERROR!" + error.getMessage());
                }
            });
            queue.add(jsonRequest);
            System.out.println("###############SENT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIdToStudents(JSONArray iStudents)
    {
        String id = " ";
        String studentName = " ";
        JSONObject jsonObject = null;
        MiniStudent student = null;
        mStudentIdsToNames = new HashMap<>();
        for(int i=0; i<mTeacher.getmNumOfStudents(); i++)
        {
            try {
                student = new MiniStudent();
                jsonObject = iStudents.getJSONObject(i);
                id = jsonObject.getString("user_id");
                System.out.println("ID FROM JSON: " + id);
                studentName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                System.out.println("@@@@@@@@@@@@@@@ STUDENT NAME: " + studentName);
                student.Name = studentName;
                student.Id = id;
                student.Goal = "1";//jsonObject.getString("goal");
                student.CurrLevel = "1";//jsonObject.getString("curr_level");
                System.out.println("NAME FROM JSON: " + studentName);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            mStudentIdsToNames.put(studentName, student);
        }

        System.out.println("NUM OF STUDENTS IN HASH MAP: " + mStudentIdsToNames.size());
    }

    private void setSpinners()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mStudentNames);
        spinnerChooseStudent.setAdapter(adapter);
        spinnerChooseStudent.setSelection(0);

        spinnerChooseStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("############# IN ON ITEM SELECTED");
                System.out.println("#############" + spinnerChooseStudent.getSelectedItem());
                if(spinnerChooseStudent.getSelectedItemPosition() != 0)
                {
                    System.out.println("############# IN CONDITION");
                    setTableTemp();
                    //getStudentDetails(spinnerChooseStudent.getSelectedItem().toString());
                    //goalInfo.setText(mStudentIdsToNames.get(spinnerChooseStudent.getSelectedItem()).Goal);
                    //currLevel.setText(mStudentIdsToNames.get(spinnerChooseStudent.getSelectedItem()).CurrLevel);
                    //scrollViewDetails.setVisibility(View.VISIBLE);
                    //relativeLayoutDetails.setVisibility(View.VISIBLE);
                    //setAllStudentDetailsVisibility(View.VISIBLE, relativeLayoutDetails);
                    //setSpinnerLevel(spinnerLevel);
                    //setSpinnerWord(spinnerWord);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        spinnerWord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
    private void getStudentDetails(String iSelectedStudentName)
    {
        String userId = mStudentIdsToNames.get(iSelectedStudentName).Id;
        String url = "https://speech-rec-server.herokuapp.com/get_answers_for_student/";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            System.out.println("+++++++++++++++++++++++" + jsonBody);

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("****************************" + response);
                            try {
                                JSONArray answers = response.getJSONArray("answers");
                                setTable(answers);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERROR!" + error.getMessage());
                }
            });
            queue.add(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTable(JSONArray iAnswers)
    {
        Context currContext = getContext();
        String isClueUsed;
        String isCompleted;
        CheckBox checkBox = null;
        TableRow tableRow = null;
        TextView textView = null;
        JSONObject jsonObject = null;
        for(int i=0; i<iAnswers.length(); i++)
        {
            try {
                jsonObject = iAnswers.getJSONObject(i);
                tableRow = new TableRow(currContext);
                textView = new TextView(currContext);
                textView.setText(jsonObject.getString("word"));
                tableRow.addView(textView, 1);
                textView = new TextView(currContext);
                textView.setText(jsonObject.getString("level"));
                tableRow.addView(textView, 2);
                textView = new TextView(currContext);
                textView.setText(jsonObject.getString("numOfTries"));
                tableRow.addView(textView, 3);
                checkBox = new CheckBox(currContext);
                isClueUsed = jsonObject.getString("isAudioClueUsed");
                if(isClueUsed.toLowerCase().equals("true"))
                {
                    checkBox.setChecked(true);
                }
                else{
                    checkBox.setChecked(false);
                }
                checkBox.setEnabled(false);
                tableRow.addView(checkBox, 4);

                checkBox = new CheckBox(currContext);
                isClueUsed = jsonObject.getString("answer");
                if(isClueUsed.toLowerCase().equals("true"))
                {
                    checkBox.setChecked(true);
                }
                else{
                    checkBox.setChecked(false);
                }
                checkBox.setEnabled(false);
                tableRow.addView(checkBox, 5);
//                textView = new TextView(currContext);
//                textView.setText(jsonObject.getString("answer")+rightToLeft);
//                tableRow.addView(textView, 5);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    textView.setTextDirection(View.TEXT_DIRECTION_RTL);
//                    textView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//                }
                tableData.addView(tableRow);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setTableTemp(){
        Context currContext = getContext();
        String isClueUsed;
        String isCompleted;
        CheckBox checkBox = null;
        TableRow tableRow = null;
        TextView textView = null;
        JSONObject jsonObject = null;
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        setTableHeaders(params);
        for(int i=0; i<10; i++) {
            tableRow = new TableRow(currContext);
            textView = new TextView(currContext);
            textView.setText("בית");
            System.out.println("~~~~~~~~~~ " + textView.getText());
            tableRow.addView(textView, params);
            textView = new TextView(currContext);
            textView.setText("2");
            System.out.println("~~~~~~~~~~ " + textView.getText());
            tableRow.addView(textView, params);
            textView = new TextView(currContext);
            textView.setText("3");
            System.out.println("~~~~~~~~~~ " + textView.getText());
            tableRow.addView(textView, params);
            checkBox = new CheckBox(currContext);
            isClueUsed = "true";
            if (isClueUsed.toLowerCase().equals("true")) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setEnabled(false);
            tableRow.addView(checkBox, params);

            checkBox = new CheckBox(currContext);
            isCompleted = "false";
            if (isCompleted.toLowerCase().equals("true")) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setEnabled(false);
            tableRow.addView(checkBox, params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tableRow.setBackground(getResources().getDrawable(R.drawable.shape_cell));
                tableRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                tableRow.setTextDirection(View.LAYOUT_DIRECTION_RTL);
                tableRow.setMinimumWidth(0);
            }
//                textView = new TextView(currContext);
//                textView.setText(jsonObject.getString("answer")+rightToLeft);
//                tableRow.addView(textView, 5);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    textView.setTextDirection(View.TEXT_DIRECTION_RTL);
//                    textView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//                }
            tableData.addView(tableRow);
        }
    }

    private void setTableHeaders(TableRow.LayoutParams iTableParams)
    {
        TableRow tableRow = new TableRow(getContext());
        TextView textView = new TextView(getContext());
        List<String> headers = new ArrayList<String>();

        headers.add(getResources().getString(R.string.header_word));
        headers.add(getResources().getString(R.string.header_level));
        headers.add(getResources().getString(R.string.header_num_of_tries));
        headers.add(getResources().getString(R.string.header_used_clue));
        headers.add(getResources().getString(R.string.header_is_completed));

        for(String header : headers)
        {
            textView = new TextView(getContext());
            textView.setText(header);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
                textView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            tableRow.addView(textView, iTableParams);
        }

        tableData.addView(tableRow);
    }
}