package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//This is the login page for the app
public class LoginPage extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;
    TextView signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        signupBtn = (TextView) findViewById(R.id.signUpBtn);
        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                email = (EditText) findViewById(R.id.emailEditText);
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginBtn.setEnabled(false);
                loginBtn.setClickable(false);
                loginBtn.setText(getString(R.string.logging_in));
                getUserFromDatabase(email.getText().toString(), password.getText().toString());
            }
        });
        //Checking if we we're transferred to this page from signup or from a server failure,
        //and showing a corresponding message to the user
        Intent intent = getIntent();
        if(intent.hasExtra("isAfterSignUp")){
            String isAfterSignup = intent.getStringExtra("isAfterSignUp");
            if(isAfterSignup.equals("true")){
                messageToUser(getString(R.string.msg_after_signup));
            }
        }
        else if(intent.hasExtra("isServerFailed")){
            String isServerFaild = intent.getStringExtra("isServerFailed");
            if(isServerFaild.equals("true"))
            {
                messageToUser(getString(R.string.error_server_try_later));
            }
        }
    }

    //Sending a request to the server to log in, and check if the values entered are a match
    private void getUserFromDatabase(String iEmail, String iPassword) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("password", iPassword);
            jsonBody.put("email", iEmail.toLowerCase());
            final RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://speech-rec-server.herokuapp.com/user_login/";
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response);
                            if(response.has("error"))
                            {
                                try {
                                    if(response.getString("error").toLowerCase().equals(getResources().getString(R.string.server_error_login)))
                                    {
                                        messageToUser("האימייל או הסיסמה שהוזנו אינם נכונים. אנא נסו שוב");
                                    }
                                    loginBtn.setText(getString(R.string.login));
                                    setButtons(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                try {
                                    moveToHomePage(response.getString("id"), response.getString("user_type"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print("ERROR!");
                    setButtons(true);
                    messageToUser(getResources().getString(R.string.error_server));
                }
            });
            queue.add(jsonRequest);
            setButtons(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //In case that the details the user entered are a match - moving to Home Page
    private void moveToHomePage(String iCurrUserId, String iUserType)
    {
        Intent intent = new Intent(LoginPage.this, HomePage.class);
        intent.putExtra("id", iCurrUserId);
        intent.putExtra("user_type", iUserType);
        startActivity(intent);
    }

    //Presenting a message to the user
    private void messageToUser(CharSequence text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Enabling or disabling the buttons
    private void setButtons(boolean iVal)
    {
        loginBtn.setEnabled(iVal);
        loginBtn.setClickable(iVal);
        signupBtn.setEnabled(iVal);
        signupBtn.setClickable(iVal);
    }

    //Disabling the back key
    public void onBackPressed(){
    }
}