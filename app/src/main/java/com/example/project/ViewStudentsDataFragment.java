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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//This is the page where the teacher can see the data about his students progress
public class ViewStudentsDataFragment extends Fragment {
    private Teacher mTeacher;
    private HashMap<String, MiniStudent> mStudentNamesToIds = null;
    private List<String> mStudentNames = null;
    private Spinner spinnerLevel;
    private TextView textViewCurrLevel;
    private TextView textViewCurrLevelHeader;
    private TextView textViewGoal;
    private TextView textViewGoalHeader;
    private Spinner spinnerChooseStudent;
    private TableLayout tableData;
    private MiniStudent chosenStudent;
    private Button buttonExport;
    private TextView textViewNoData;

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
        spinnerChooseStudent = (Spinner)v.findViewById(R.id.spinnerChooseStudent);
        tableData = (TableLayout)v.findViewById(R.id.tableData);
        textViewCurrLevel = (TextView)v.findViewById(R.id.textViewCurrLevelData);
        textViewGoal = (TextView)v.findViewById(R.id.textViewGoalData);
        textViewCurrLevelHeader = (TextView)v.findViewById(R.id.textViewCurrLevelHeader);
        textViewGoalHeader = (TextView)v.findViewById(R.id.textViewGoalHeader);
        buttonExport = (Button)v.findViewById(R.id.buttonExportToMail);
        textViewNoData = (TextView)v.findViewById(R.id.textViewNoDataToShow);

        if(getArguments() != null)
        {
            mTeacher = getArguments().getParcelable("user");
        }
        getStudents();

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportChosenStudentDetailsToTeachersMail();
            }
        });
        return v;
    }

    //This method creates an array list of student names for the drop down list
    private List<String> setStudents()
    {
        String id = " ";
        String studentName = " ";
        JSONObject jsonObject = null;
        ArrayList<String> studentNames = new ArrayList<String>();
        List<String> ids = new ArrayList<String>(mStudentNamesToIds.keySet());
        System.out.println("SIZE OF ids: " + mStudentNamesToIds.keySet().size());
        studentNames.add("בחר תלמיד");
        for(int i=0; i<mStudentNamesToIds.size(); i++)
        {
            id = ids.get(i);
            System.out.println("############## ID: " + mStudentNamesToIds.get(id));
            studentName = mStudentNamesToIds.get(id).Name;
            System.out.println("######### goal: " + mStudentNamesToIds.get(id).Goal);
            studentNames.add(studentName);
        }

        return studentNames;
    }

    //Sending request to the server to get the students of the current teacher
    private void getStudents()
    {
        String url = "https://speech-rec-server.herokuapp.com/get_students_of_teacher/";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", mTeacher.getmId());
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray students = response.getJSONArray("students");
                                setIdToStudents(students);
                                mStudentNames = setStudents();
                                setSpinners();
                            } catch (JSONException e) {
                                messageToUser(getString(R.string.server_error_get_students));
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageToUser(getString(R.string.server_error_get_students));
                }
            });
            queue.add(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
            messageToUser(getString(R.string.server_error_get_students));
        }
    }

    //Matching the students id's to names, to use the relevant student id when needed
    private void setIdToStudents(JSONArray iStudents)
    {
        String id = " ";
        String studentName = " ";
        JSONObject jsonObject = null;
        MiniStudent student = null;
        mStudentNamesToIds = new HashMap<>();
        for(int i=0; i<mTeacher.getmNumOfStudents(); i++)
        {
            try {
                student = new MiniStudent();
                jsonObject = iStudents.getJSONObject(i);
                id = jsonObject.getString("user_id");
                studentName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                student.Name = studentName;
                student.Id = id;
                student.Goal = jsonObject.getString("goal");
                student.CurrLevel = jsonObject.getString("curr_level");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            mStudentNamesToIds.put(studentName, student);
        }

        System.out.println("NUM OF STUDENTS IN HASH MAP: " + mStudentNamesToIds.size());
    }


    //Setting the drop down list after we have all the students
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
                    chosenStudent = mStudentNamesToIds.get(spinnerChooseStudent.getSelectedItem());
                    System.out.println("CHOSEN STUDENT LEVEL: " + chosenStudent.CurrLevel);
                    System.out.println("CHOSEN STUDENT GOAL: " + chosenStudent.Goal);
                    tableData.removeAllViews();
                    textViewCurrLevel.setText(chosenStudent.CurrLevel);
                    textViewGoal.setText(getGoal());
                    textViewGoal.setVisibility(View.VISIBLE);
                    textViewCurrLevel.setVisibility(View.VISIBLE);
                    textViewCurrLevelHeader.setVisibility(View.VISIBLE);
                    textViewGoalHeader.setVisibility(View.VISIBLE);
                    getStudentDetails();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //After the teacher chose a student, we send a request to the server to get all the data we have
    //about the chosen student's progress
    private void getStudentDetails()
    {
        String url = "https://speech-rec-server.herokuapp.com/get_answers_for_student/";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", chosenStudent.Id);
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray answers = response.getJSONArray("answers");
                                setTable(answers);
                            } catch (JSONException e) {
                                messageToUser(getString(R.string.server_error_get_students));
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageToUser(getString(R.string.server_error_get_students));
                }
            });
            queue.add(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
            messageToUser(getString(R.string.server_error_get_students));
        }
    }

    //Setting the table in which the student data is presented
    private void setTable(JSONArray iAnswers)
    {
        Context currContext = getContext();
        String isClueUsed;
        String isCompleted;
        CheckBox checkBox = null;
        TableRow tableRow = null;
        TextView textView = null;
        JSONObject jsonObject = null;
        if(iAnswers.length() == 0)
        {
            textViewNoData.setVisibility(View.VISIBLE);
            buttonExport.setVisibility(View.INVISIBLE);
        }
        else {
            textViewNoData.setVisibility(View.INVISIBLE);
            buttonExport.setVisibility(View.VISIBLE);
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            setTableHeaders(params);
            for (int i = 0; i < iAnswers.length(); i++) {
                try {
                    System.out.println("IN LOOP, INDEX: " + i);
                    jsonObject = iAnswers.getJSONObject(i);
                    tableRow = new TableRow(currContext);
                    textView = new TextView(currContext);
                    textView.setText(jsonObject.getString("word"));
                    tableRow.addView(textView, params);
                    textView = new TextView(currContext);
                    textView.setText(jsonObject.getString("level"));
                    tableRow.addView(textView, params);
                    textView = new TextView(currContext);
                    textView.setText(jsonObject.getString("numOfTries"));
                    tableRow.addView(textView, params);
                    checkBox = new CheckBox(currContext);
                    isClueUsed = jsonObject.getString("isAudioClueUsed");
                    if (isClueUsed.toLowerCase().equals("true")) {
                        checkBox.setButtonDrawable(R.drawable.ic_check);
                    } else {
                        checkBox.setButtonDrawable(R.drawable.ic_clear);
                    }
                    checkBox.setClickable(false);
                    tableRow.addView(checkBox, params);
                    checkBox = new CheckBox(currContext);
                    isCompleted = jsonObject.getString("answer");
                    if (isCompleted.toLowerCase().equals("true")) {
                        checkBox.setButtonDrawable(R.drawable.ic_check);
                    } else {
                        checkBox.setButtonDrawable(R.drawable.ic_clear);
                    }
                    checkBox.setClickable(false);
                    tableRow.addView(checkBox, params);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        tableRow.setBackground(getResources().getDrawable(R.drawable.shape_cell));
                        tableRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        tableRow.setTextDirection(View.LAYOUT_DIRECTION_RTL);
                        tableRow.setMinimumWidth(0);
                    }
                    tableData.addView(tableRow);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Setting up the headers to the table
    private void setTableHeaders(TableRow.LayoutParams iTableParams)
    {
        TableRow tableRow = new TableRow(getContext());
        TextView textView;
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

    //Get goal name in accordance to the goal id
    private String getGoal()
    {
        String goalStr = " ";
        switch (chosenStudent.Goal)
        {
            case "1":
                goalStr = getString(R.string.goal_audio_without_code);
                break;
            case "2":
                goalStr = getString(R.string.goal_picture_without_code);
                break;
        }

        return goalStr;
    }

    //If the teacher chose to, the app sends a request to the server to send the presented data as a .csv file
    //to the teachers mail.
    //Currently, since we used random email addresses to signup to the app, the server sends those files
    //to Miri's email.
    private void exportChosenStudentDetailsToTeachersMail()
    {
        String url = "https://speech-rec-server.herokuapp.com/send_email_statistics/";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("student_id", chosenStudent.Id);
            jsonBody.put("teacher_id", mTeacher.getmId());
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("body") && response.getString("body").toLowerCase().contains("email sent")) {
                                    messageToUser("המייל נשלח");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                messageToUser(getString(R.string.error_server_try_later));

                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageToUser(getString(R.string.error_server_try_later));
                }
            });
            queue.add(jsonRequest);

        } catch (Exception e) {
            messageToUser(getString(R.string.error_server_try_later));
            e.printStackTrace();
        }
    }

    //Displaying message to the user
    private void messageToUser(CharSequence text)
    {
        Context context = getActivity();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}