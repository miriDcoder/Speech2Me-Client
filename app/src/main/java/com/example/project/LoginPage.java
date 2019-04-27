package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginPage extends AppCompatActivity {
    EditText email, password;
    DataBase db = new DataBase();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        final Button loginBtn = (Button) findViewById(R.id.LoginBtn);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                eUserValidation userValidation = isValidDetails(email.getText().toString(), password.getText().toString());
                if(userValidation == eUserValidation.validUser)
                {
                    loginBtn.setText("Logging in");
                    loginBtn.setEnabled(false);
                    User currUser = DbUtils.GetUserByMail(db.makeUserList(), email.getText().toString());
                    moveToHomePage(currUser);
                }
                else if(userValidation == eUserValidation.wrongPassword)
                {
                    password.getText().clear();
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid password!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else
                {
                    //give the user an option - either re-enter the details for login
                    //or move to sign up page.
                    password.getText().clear();
                    email.getText().clear();
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid details! Please insert valid details or press sign up";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        Button signUpBtn = (Button)findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                email = (EditText) findViewById(R.id.emailEditText);
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });
    }

     private eUserValidation isValidDetails(String iEmail, String iPassword) {
        eUserValidation res = eUserValidation.invalidUser;
        for (User user : db.makeUserList()) {
            if (iEmail.equals(user.getmEmail()) && iPassword.equals(user.getmPassword())) {
                res = eUserValidation.validUser;
                break;
            } else if (iEmail.equals(user.getmEmail()) && !iPassword.equals(user.getmPassword())) {
                res = eUserValidation.wrongPassword;
                break;
            }
        }
        return res;
    }

    private void moveToHomePage(User iCurrUser)
    {
        Intent intent;
        if (iCurrUser.getmType()==User.eType.STUDENT){
            intent = new Intent(LoginPage.this, StudentHomePage.class);
            intent.putExtra("id", iCurrUser.getmId());
            startActivity(intent);
        }
        else if (iCurrUser.getmType()==User.eType.TEACHER){
            intent = new Intent(LoginPage.this, TeacherHomePage.class);
            intent.putExtra("id", iCurrUser.getmId());
            startActivity(intent);
        }
    }
}
