package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class LoginPage extends AppCompatActivity {
    EditText email, password;
    DataBase db = new DataBase();
    JSONObject mResponse = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Regular.ttf");
        email.setTypeface(custom_font);
        password.setTypeface(custom_font);
        loginBtn.setTypeface(custom_font);


        Button signUpBtn = (Button)findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                email = (EditText) findViewById(R.id.emailEditText);
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });

//        Button studentTestingBtn = (Button)findViewById(R.id.studentTestingBtn);
//        studentTestingBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                User currUser = DbUtils.GetUserByMail(db.makeUserList(), "roni@gmail.com");
//                moveToHomePage(currUser);
//            }
//        });

//        Button teacherTestingBtn = (Button)findViewById(R.id.teacherTestingBtn);
//        teacherTestingBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                User currUser = DbUtils.GetUserByMail(db.makeUserList(), "dana@gmail.com");
//                moveToHomePage(currUser);
//            }
//        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getUserFromDatabase(email.getText().toString(), password.getText().toString());
//                if(userValidation == eUserValidation.validUser)
//                {
//                    loginBtn.setText("מתחבר");
//                    loginBtn.setEnabled(false);
//                    User currUser = DbUtils
//                            .GetUserByMail(db.makeUserList(), email.getText().toString());
//                    moveToHomePage(currUser);
//                }
//                else if(userValidation == eUserValidation.wrongPassword)
//                {
//                    password.getText().clear();
//                    messageToUser("סיסמה שגויה!");
//                }
//                else
//                {
//                    //give the user an option - either re-enter the details for login
//                    //or move to sign up page.
//                    password.getText().clear();
//                    email.getText().clear();
//                    messageToUser("הפרטים שהזנת שגויים! אנא הכנס פרטים תקינים, או הירשם");
//                }
            }
        });
    }

    private boolean waitForResponse(){
        for(int wait=0; wait<10;wait++){
            try{
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e){
                e.printStackTrace();
                break;
            }
            if(mResponse!=null){
                return true;
            }
        }
        return false;
    }

    private void getUserFromDatabase(String iEmail, String iPassword) {
        eUserValidation validation = eUserValidation.invalidUser;
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("password", iPassword);
            jsonBody.put("email", iEmail);
            final RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://speech-rec-server.herokuapp.com/user_login/";
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            //final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                      new Response.Listener<JSONObject>() {
                          @Override
                          public void onResponse(JSONObject response) {
                              System.out.print(response);
                              EditText responseLogin = (EditText) findViewById(R.id.responseLogin);
                              responseLogin.setText(response.toString());
                              //mResponse = response;
                              try {
                                  moveToHomePage(response.getString("id"), response.getString("user_type"));
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }

                          }
                      },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print("ERROR!");
                }
              });
            //JsonObjectRequest request = new JsonObjectRequest()
//            try{
//                JSONObject response = future.get(10,TimeUnit.SECONDS);
//            }
//            catch (InterruptedException e){
//
//            }
//            catch (ExecutionException e){
//
//            }

            queue.add(jsonRequest);
            //waitForResponse();
            if(mResponse != null)
            {
                validation = eUserValidation.invalidUser;
            }
            else
            {
                validation = eUserValidation.validUser;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToHomePage(String iCurrUserId, String iUserType)
    {
        Intent intent;
        if (iUserType.toLowerCase().equals("student")){
            intent = new Intent(LoginPage.this, StudentHomePage.class);
            intent.putExtra("id", iCurrUserId);
            startActivity(intent);
        }
        else if (iUserType.toLowerCase().equals("teacher")){
            intent = new Intent(LoginPage.this, TeacherHomePage.class);
            intent.putExtra("id", iCurrUserId);
            startActivity(intent);
        }
    }

    private void messageToUser(CharSequence text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}

//TODO: case of error from request