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

import java.io.UnsupportedEncodingException;

//This is the Signup page, for the user to signup to the app
public class SignUp extends AppCompatActivity {
    Button buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        buttonSignUp = findViewById(R.id.buttonSignup);
        ImageView imageViewArrowBack = findViewById(R.id.imgArrowBack);
        final EditText editTextFirstName = findViewById(R.id.editTextFirstName);
        final EditText editTextLastName = findViewById(R.id.editTextLastName);
        final EditText editTextEmail = findViewById(R.id.editTextEmail);
        final EditText editTextPassword = findViewById(R.id.editTextPassword);
        final EditText editTextTeacherId = findViewById(R.id.editTextTeacherId);
        final EditText editTextGoalCode = findViewById(R.id.editTextGoalCode);
        final Switch switchIsStudent = findViewById(R.id.switchSignupAsStudent);

        //After the user clicks the signup button, we validate some of the info that the user entered, and if
        //we found them valid - we send a request to the server.
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSignUp.setEnabled(false);
                buttonSignUp.setText(getString(R.string.msg_please_wait));
                if(validateUserDetails(editTextFirstName, editTextLastName, editTextEmail,
                                    editTextPassword, switchIsStudent.isChecked(), editTextTeacherId,
                                    editTextGoalCode)){
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
                        currUser = new Teacher(email, password, firstName, lastName);
                        type = "teacher";
                        goal = "0";
                    }
                    InsertNewUserToDatabase(currUser, type, goal, teacherId);
                }
                else
                {
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
                editTextGoalCode.setText("");
                editTextGoalCode.setEnabled(isChecked);
            }
        });

        imageViewArrowBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(SignUp.this, LoginPage.class));
            }
        });
    }

    //Validating the details - names only in hebrew, valid email address (in a right email format),
    //making sure that in case that it's a student - that a teacher id and goal code were entered
    private boolean validateUserDetails(EditText iFirstName, EditText iLastName, EditText iEmail,
                                        EditText iPassword, boolean iIsStudent, EditText iTeacherID,
                                        EditText iGoal){
//        return validateName(iFirstName) &&
//                                    validateName(iLastName) &&
//                                    validateMail(iEmail) &&
//                                    validatePassword(iPassword) &&
//                                    validateGoal(iGoal, iIsStudent);
        return validateMail(iEmail) &&
                validatePassword(iPassword) &&
                validateGoal(iGoal, iIsStudent);
    }


    //validate that a student entered a code goal and that it's a number
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

    //Validate name
//    private boolean validateName(EditText iName){
//        boolean isValidName = false;
//
//        if(iName.equals("") || iName == null || !(AppUtils.IsLetters(iName.getText().toString()))) {
//            iName.setBackgroundColor(Color.RED);
//            iName.setText("");
//        }
//        else {
//            isValidName = true;
//            iName.setBackgroundColor(Color.TRANSPARENT);
//        }
//
//        return isValidName;
//    }

    //Validate mail
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

    //Validate password (up to 8 chars)
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

    //Creating a request to insert a new user to the server
    //If the request has the right details - new user is being created and redirected to the login page
    //Else, the user has to insert correct details in accordance to the error that was returned
    private void InsertNewUserToDatabase(User iUser, String iType, String iGoal, String iTeacherId)
    {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("first_name", iUser.getmFirstName());
            jsonBody.put("last_name", iUser.getmLastName());
            jsonBody.put("password", iUser.getmPassword());
            jsonBody.put("email", iUser.getmEmail());
            jsonBody.put("user_type", iType);
            if(iType.toLowerCase() == "student"){
                jsonBody.put("teacher_id", iTeacherId);
                jsonBody.put("goal", iGoal);
            }
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="https://speech-rec-server.herokuapp.com/user_signup/";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent intent = null;
                            try {
                                if(response.has("id"))
                                {
                                    intent = new Intent(SignUp.this, LoginPage.class);
                                    intent.putExtra("id", response.getString("id"));
                                    intent.putExtra("isAfterSignUp", "true");
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                messageToUser(getString(R.string.error_server));
                                e.printStackTrace();
                                setButtons(true);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    buttonSignUp.setText(getString(R.string.signup));
                    buttonSignUp.setEnabled(true);
                    parseVolleyError(error);
                }
            });
            queue.add(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
            messageToUser(getString(R.string.error_server));
        }
    }
    //Showing a message to the user
    private void messageToUser(CharSequence text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Enables or disables the buttons
    private void setButtons(boolean iVal)
    {
        buttonSignUp.setEnabled(iVal);
        buttonSignUp.setClickable(iVal);
    }

    private void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("error");
            translateErrorToMessageForClient(message);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException exceptionError) {
        }
    }

    private void translateErrorToMessageForClient(String iErrorMsg)
    {
        String message = "";
        switch(iErrorMsg.toLowerCase())
        {
            case "email already exists":
                message = getString(R.string.error_email_exists);
                break;
            case "no user type":
                message = getString(R.string.error_user_type);
                break;
            case "no teacher id":
                message = getString(R.string.error_teacher_id);
                break;
            default:
                message = getString(R.string.error_server);
                break;
        }

        messageToUser(message);
    }
}
