package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = (Button)findViewById(R.id.buttonSignup);
        //final EditText textViewRequest = (EditText) findViewById(R.id.editTextRequest);
        ImageView imageViewArrowBack = (ImageView) findViewById(R.id.imgArrowBack);
        final EditText editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        final EditText editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        final EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        final EditText editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        final Switch switchIsStudent = (Switch) findViewById(R.id.switchSignupAsStudent);
        final EditText editTextTeacherId = (EditText)findViewById(R.id.editTextTeacherId);
        final EditText editTextGoalCode = (EditText)findViewById(R.id.editTextGoalCode);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUserDetails(editTextFirstName, editTextLastName, editTextEmail,
                                    editTextPassword, switchIsStudent.isChecked(), editTextTeacherId,
                                    editTextGoalCode)){
                    System.out.println("!!!!!!!!!!! HERE 1");
                    String firstName = editTextFirstName.getText().toString();
                    String lastName = editTextLastName.getText().toString();
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();
                    String teacherId = editTextTeacherId.getText().toString();

                    User currUser;
                    String type = null;
                    String goal = null;
                    if(switchIsStudent.isChecked())
                    {
                        goal = editTextGoalCode.getText().toString();
                        teacherId = editTextTeacherId.getText().toString();
                        currUser = new Student(email, password, firstName,
                                                        lastName, teacherId, goal);
                        type = "student";
                    }
                    else
                    {
                        teacherId = null;
                        System.out.println("In teacher case");
                        currUser = new Teacher(email, password, firstName, lastName);
                        type = "teacher";
                        goal = "0";
                    }
                    InsertNewUserToDatabase(currUser, type, goal, teacherId);
                }
                else
                {
                    System.out.println("!!!!!!!!! HERE 2");
                    Context context = getApplicationContext();
                    CharSequence text = "Some details were invalid";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        switchIsStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextTeacherId.setText("");
                editTextTeacherId.setEnabled(isChecked);
            }
        });

        imageViewArrowBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(SignUp.this, LoginPage.class));
            }
        });
    }



    private boolean validateUserDetails(EditText iFirstName, EditText iLastName, EditText iEmail,
                                        EditText iPassword, boolean iIsStudent, EditText iTeacherID,
                                        EditText iGoal){
        return validateName(iFirstName) &&
                                    validateName(iLastName) &&
                                    validateMail(iEmail) &&
                                    validatePassword(iPassword) &&
                                    validateGoal(iGoal, iIsStudent);
    }

    private boolean validateGoal(EditText iGoal, boolean iIsStudent) {
        boolean isValid = false;
        if(iGoal.getText().toString() != "" && iGoal.getText().toString() != " "){
            try{
                int num = Integer.parseInt(iGoal.getText().toString());
                isValid = true;
            }
            catch (Exception ex)
            {
                isValid = false;
            }
        }

        if(!iIsStudent)
        {
            isValid = true;
        }

        return isValid;
    }

    private boolean validateName(EditText iName){
        boolean isValidName = false;

        if(iName.equals("") || iName == null || !(AppUtils.IsLetters(iName.getText().toString()))) {
            iName.setBackgroundColor(Color.RED);
            iName.setText("");
        }
        else {
            isValidName = true;
            iName.setBackgroundColor(Color.TRANSPARENT);
        }

        return isValidName;
    }

    private boolean validateMail(EditText iEmail){
        boolean isValid = false;

        if(iEmail.getText().toString() != null)
        {
            if(!Patterns.EMAIL_ADDRESS.matcher(iEmail.getText().toString()).matches()){
                iEmail.setBackgroundColor(Color.RED);
            }
            else
            {
                iEmail.setBackgroundColor(Color.TRANSPARENT);
                isValid = true;
            }
        }

        return isValid;
    }

    private boolean validatePassword(EditText iPassword){
        boolean isValid = false;

        if(iPassword.getText().toString().matches("")){
            iPassword.setBackgroundColor(Color.RED);
        }
        else if(iPassword.getText().toString().length() > 8){
            iPassword.setBackgroundColor(Color.RED);
            iPassword.setText("");
            iPassword.setHint("Up to 8 characters");
        }
        else
        {
            iPassword.setBackgroundColor(Color.TRANSPARENT);
            iPassword.setHint("Password");
            isValid = true;
        }

        return isValid;
    }

    private void InsertNewUserToDatabase(User iUser, String iType, String iGoal, String iTeacherId)
    {
        try {
            System.out.println("In insert New User, type: " + iType);
            System.out.println("In insert New User, goal: " + iGoal);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("first_name", iUser.getmFirstName());
            jsonBody.put("last_name", iUser.getmLastName());
            jsonBody.put("password", iUser.getmPassword());
            jsonBody.put("email", iUser.getmEmail());
            jsonBody.put("user_type", iType);
            jsonBody.put("teacher_id", iTeacherId);
            jsonBody.put("goal", iGoal);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="https://speech-rec-server.herokuapp.com/user_signup/";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response);
                            EditText responseSignup = (EditText)findViewById(R.id.responseSignup);
                            responseSignup.setText(response.toString());
                            Intent intent = null;
                            try {
                                System.out.println(response.getString("id"));
                                intent = new Intent(SignUp.this, LoginPage.class);
                                intent.putExtra("id", response.getString("id"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setButtons(true);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print("ERROR!");
                    messageToUser(getResources().getString(R.string.error_server));
                    setButtons(true);
                }
            });
            queue.add(jsonRequest);
            setButtons(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void messageToUser(CharSequence text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void setButtons(boolean iVal)
    {
        btnSignUp.setEnabled(iVal);
        btnSignUp.setClickable(iVal);
    }

}
