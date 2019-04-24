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
    ArrayList<User> tempUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        final Button loginBtn = (Button) findViewById(R.id.LoginBtn);
        tempUsers = makeUserList();

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText email = (EditText) findViewById(R.id.emailEditText);
                EditText password = (EditText) findViewById(R.id.passwordEditText);

                eUserValidation userValidation = isValidDetails(email.getText().toString(), password.getText().toString());
                if(userValidation == eUserValidation.validUser)
                {
                    loginBtn.setText("Logging in");
                    loginBtn.setEnabled(false);
                    startActivity(new Intent(LoginPage.this, HomePage.class));

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
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });
    }

    private ArrayList<User> makeUserList() {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new Teacher("shula@gmail.com", "aA2020", "Shula", "Katz", "Rehovot", "1001"));
        users.add(new Teacher("dana@gmail.com", "danaA2020", "Dana", "Levi", "Tel Aviv", "1002"));

        users.add(new Student("dan@gmail.com", "1234", "Dan", "Strik", "Rehovot", "0001", 1, "12", 0, "1001"));
        users.add(new Student("ofer@gmail.com", "1234", "Ofer", "Feder", "Rehovot", "0002", 1, "12", 0, "1001"));
        users.add(new Student("miri@gmail.com", "1234", "Miri", "Levi", "Rehovot", "0003", 1, "11", 0, "1001"));
        users.add(new Student("daniel@gmail.com", "1234", "Daniel", "Levinson", "Tel Aviv", "0004", 1, "14", 0, "1002"));
        users.add(new Student("roni@gmail.com", "1234", "Roni", "Shaham", "Tel Aviv", "0005", 1, "15", 0, "1002"));
        users.add(new Student("gal@gmail.com", "1234", "Gal", "Cohen", "Tel Aviv", "0006", 1, "22", 0, "1002"));
        ((Teacher) users.get(0)).getmStudents().add((Student) users.get(2));
        ((Teacher) users.get(0)).getmStudents().add((Student) users.get(3));
        ((Teacher) users.get(0)).getmStudents().add((Student) users.get(4));
        ((Teacher) users.get(1)).getmStudents().add((Student) users.get(5));
        ((Teacher) users.get(1)).getmStudents().add((Student) users.get(6));
        ((Teacher) users.get(1)).getmStudents().add((Student) users.get(7));
        ((Teacher) users.get(0)).setmNumOfStudents(3);
        ((Teacher) users.get(1)).setmNumOfStudents(3);
        return users;
    }

    private eUserValidation isValidDetails(String iEmail, String iPassword) {
        eUserValidation res = eUserValidation.invalidUser;
        for (User user : tempUsers) {
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

}
