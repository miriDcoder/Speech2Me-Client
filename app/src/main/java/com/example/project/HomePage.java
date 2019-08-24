package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public User currUser;
    private DrawerLayout drawer;
    public DataBase db = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        String url = null;
        toggle.syncState();
        Intent intent = getIntent();
        System.out.println("~~~~~~~~~~~~IN HOME PAGE");
        String id = intent.getStringExtra("id");
        String userType = intent.getStringExtra("user_type");
        switch (userType)
        {
            case "student":
                url = "https://speech-rec-server.herokuapp.com/get_student/";
                getUserFromDatabase(id, url, navigationView);
                break;
            case "teacher":
                System.out.println("~~~~~~~~~~~~IN TEACHER BEFORE REQUEST");
                url = "https://speech-rec-server.herokuapp.com/get_teacher/";
                getUserFromDatabase(id, url, navigationView);
                break;
        }
//        String newScore = intent.getStringExtra("newScore");
        //currUser = DbUtils.GetUserById(db.makeUserList(), id);

        //when going to home page from game
//        if (newScore != null){
//            int score = Integer.parseInt(newScore);
//            Student student = (Student)currUser;
//            student.setmScore(score);
//        }
//        if (savedInstanceState==null) {
//            moveToHomePage();
//            navigationView.setCheckedItem(R.id.nav_home_page);
//        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_home_page:
                moveToHomePage();
                break;
            case R.id.nav_account:
                getSupportActionBar().setTitle("פרופיל");
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new AccountFragment()).commit();
                break;
            case R.id.nav_info:
                getSupportActionBar().setTitle("אודות");
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new AboutFragment()).commit();
                break;
            case R.id.nav_logout:
                getSupportActionBar().setTitle("התנתק/י");
                startActivity(new Intent(HomePage.this, LoginPage.class));
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void moveToHomePage(){
        System.out.println("IN HOME PAGE");
        Bundle bundle = new Bundle();
        getSupportActionBar().setTitle("בית");
        switch (currUser.getmType())
        {
            case "student":
            bundle.putParcelable("user",(Student)currUser);
            StudentHomePageFragment studentPage = new StudentHomePageFragment();
            studentPage.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, studentPage).commit();
            break;
            case "teacher":
                bundle.putParcelable("user", (Teacher)currUser);
                TeacherHomePageFragment teacherPage = new TeacherHomePageFragment();
                teacherPage.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, teacherPage).commit();
                break;
        }
    }

    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            moveToHomePage();
        }
    }

    private void getUserFromDatabase(final String id, final String iUrl, final NavigationView iNav) {
        eUserValidation validation = eUserValidation.invalidUser;
        try {
            //TODO: encript password?
            System.out.println("MAKING JSON REQUEST");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", id);
            final RequestQueue queue = Volley.newRequestQueue(this);
            String url = iUrl;
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            System.out.print(response);
//                            EditText responseLogin = (EditText) findViewById(R.id.responseLogin);
//                            responseLogin.setText(response.toString());
                            //mResponse = response;
                            try {
                                System.out.println("IN ON RESPONSE");
                                String firstName = response.getString("first_name");
                                String lastName = response.getString("last_name");
                                String userType = response.getString("user_type");
                                String idDb = response.getString("id");
                                System.out.println("ID : " + idDb);
                                System.out.println("USER TYPE : " + userType);

                                switch (userType)
                                {
                                    case "student":
                                        String level = response.getString("level");
                                        String goal = response.getString("goal");
                                        String score = response.getString("score");

                                        currUser = new Student(firstName, lastName, idDb,
                                                userType, level, goal,
                                                score);
                                        System.out.println("~~~~~~ Created user: " + currUser.getmId());
                                        break;
                                    case "teacher":
                                        System.out.println("~~~~~~~BEFORE TEACHER");
                                        String numOfStudents = response.getString("number_of_students");
                                        currUser = new Teacher(firstName, lastName, idDb,
                                                numOfStudents, true);
                                        System.out.println("~~~~~~ Created teacher user: " + currUser.getmId());
                                        break;

                                }

                                moveToHomePage();
                                iNav.setCheckedItem(R.id.nav_home_page);

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
            queue.add(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}