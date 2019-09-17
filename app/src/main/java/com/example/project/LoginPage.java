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

import java.io.UnsupportedEncodingException;

//This is the login page for the app
public class LoginPage extends AppCompatActivity {
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    TextView buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //set XML elements
        buttonLogin = findViewById(R.id.loginBtn);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        buttonSignup = findViewById(R.id.signUpBtn);
        buttonSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editTextEmail = findViewById(R.id.emailEditText);
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                buttonLogin.setEnabled(false);
                buttonLogin.setClickable(false);
                buttonLogin.setText(getString(R.string.logging_in));
                getUserFromDatabase(editTextEmail.getText().toString(), editTextPassword.getText().toString());
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
                            try {
                                if(response.has("id") && response.has("user_type")){
                                    moveToHomePage(response.getString("id"), response.getString("user_type"));
                                }
                            } catch (JSONException e) {
                                buttonLogin.setText(getString(R.string.login));
                                setButtons(true);
                                messageToUser(getResources().getString(R.string.error_server));
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    buttonLogin.setText(getString(R.string.login));
                    setButtons(true);
                    parseVolleyError(error);
                }
            });
            queue.add(jsonRequest);
        } catch (Exception e) {
            buttonLogin.setText(getString(R.string.login));
            setButtons(true);
            messageToUser(getResources().getString(R.string.error_server));
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
        buttonLogin.setEnabled(iVal);
        buttonLogin.setClickable(iVal);
        buttonSignup.setEnabled(iVal);
        buttonSignup.setClickable(iVal);
    }

    //Disabling the back key
    public void onBackPressed(){
    }

    private void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("error");
            translateErrorToMessageForClient(message);
        } catch (JSONException e) {
            messageToUser(getResources().getString(R.string.error_server));
        } catch (UnsupportedEncodingException exceptionError) {
            messageToUser(getResources().getString(R.string.error_server));
        } catch(Exception ex){
            messageToUser(getResources().getString(R.string.error_server));
        }
    }

    private void translateErrorToMessageForClient(String iErrorMsg)
    {
        String message = "";
        if(iErrorMsg.toLowerCase().equals(getResources().getString(R.string.server_error_login)))
        {
            message = getString(R.string.error_login_mail_pass);
        }
        else
        {
            message = getString(R.string.error_server);
        }

        messageToUser(message);
    }
}