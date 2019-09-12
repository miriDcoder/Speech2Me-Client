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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//This is the home page activity, that setting up the home page for both student and teacher, and navigate to the relevant display,
//according to the user type
public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public User currUser;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private String id;
    private String userType;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setting up the menu:
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //get the relevant user info
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        userType = intent.getStringExtra("user_type");
        getUserFromDatabase(id, navigationView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_home_page:
                getUserFromDatabase(id, navigationView);
                break;
            case R.id.nav_account:
                getSupportActionBar().setTitle("פרטי חשבון");
                AccountFragment accountFragment = new AccountFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", currUser.getmId());
                bundle.putString("user_type", currUser.getmType());
                accountFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, accountFragment).commit();
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
    //navigates to the relevant home page fragment, according to the user type
    public void moveToHomePage(){
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
            navigationView.setCheckedItem(R.id.nav_home_page);
            getUserFromDatabase(id, navigationView);
        }
    }

    //Sends request to the server to get the user details
    private void getUserFromDatabase(final String id, final NavigationView iNav) {
        switch (userType)
        {
            case "student":
                url = "https://speech-rec-server.herokuapp.com/get_student/";
                break;
            case "teacher":
                url = "https://speech-rec-server.herokuapp.com/get_teacher/";
                break;
        }
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", id);
            final RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String firstName = response.getString("first_name");
                                String lastName = response.getString("last_name");
                                String userType = response.getString("user_type");
                                String idDb = response.getString("id");
                                switch (userType)
                                {
                                    case "student":
                                        String level = response.getString("level");
                                        String goal = response.getString("goal");
                                        String score = response.getString("score");

                                        currUser = new Student(firstName, lastName, idDb,
                                                userType, level, goal,
                                                score);
                                        break;
                                    case "teacher":
                                        String numOfStudents = response.getString("number_of_students");
                                        currUser = new Teacher(firstName, lastName, idDb,
                                                numOfStudents, true);
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
                    Intent intent = new Intent(HomePage.this, LoginPage.class);
                    intent.putExtra("isServerFailed", "true");
                    startActivity(intent);
                }
            });
            queue.add(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}