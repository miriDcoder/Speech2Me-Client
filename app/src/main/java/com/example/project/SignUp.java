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

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button btnSignUp = (Button)findViewById(R.id.buttonSignup);
        ImageView imageViewArrowBack = (ImageView) findViewById(R.id.imgArrowBack);
        final EditText editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        final EditText editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        final EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        final EditText editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        final Switch switchIsStudent = (Switch) findViewById(R.id.switchSignupAsStudent);
        final EditText editTextTeacherId = (EditText)findViewById(R.id.editTextTeacherId);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUserDetails(editTextFirstName, editTextLastName, editTextEmail,
                                    editTextPassword, switchIsStudent.isChecked(), editTextTeacherId)){
                    String firstName = editTextFirstName.getText().toString();
                    String lastName = editTextLastName.getText().toString();
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();
                    String teacherId = editTextTeacherId.getText().toString();

                    User currUser;
                    Intent intent;
                    if(switchIsStudent.isChecked())
                    {
                        //TODO: insert to students db
                        //TODO: insert to teacher to students db
                        //id = what we got from db. then insert to constructor
                        currUser = new Student(email, password, firstName,
                                                        lastName, teacherId);

                        intent = new Intent(SignUp.this, StudentHomePage.class);
                        intent.putExtra("id", currUser.getmId());
                        startActivity(intent);
                    }
                    else
                    {
                        //TODO: insert to teacher db
                        //id = what we got from db. then insert to constructor
                        currUser = new Teacher(email, password, firstName, lastName);
                        intent = new Intent(SignUp.this, TeacherHomePage.class);
                        intent.putExtra("id", currUser.getmId());
                        startActivity(intent);
                    }
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
                                        EditText iPassword, boolean iIsStudent, EditText iTeacherID){
        return validateName(iFirstName) &&
                                    validateName(iLastName) &&
                                    validateMail(iEmail) &&
                                    validatePassword(iPassword) &&
                                    validateTeacherId(iIsStudent, iTeacherID);
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

    private boolean validateTeacherId(boolean iIsStudent, EditText iTeacherId){
        boolean isValid = true;
        String teacherId = iTeacherId.getText().toString();

        if(iIsStudent)
        {
            if(teacherId.matches("") || !isTeacherIdExists(teacherId))
            {
                iTeacherId.setBackgroundColor(Color.RED);
                isValid = false;
            }
            else
            {
                iTeacherId.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        return isValid;
    }

    private boolean isTeacherIdExists(String iTeacherId){
        //TODO: the real check
        return true;
    }

    //TODO: for every page, see how to make a return button
    //TODO: app design and colors
}
