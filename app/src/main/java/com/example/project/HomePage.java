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
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Bundle bundle = new Bundle();
        currUser = DbUtils.GetUserById(db.makeUserList(), id);
        if (savedInstanceState==null) {
            if (currUser.getmType()==User.eType.STUDENT){
                bundle.putParcelable("user",(Student)currUser);
                StudentHomePageFragment studentPage = new StudentHomePageFragment();
                studentPage.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, studentPage).commit();
            }
            else if (currUser.getmType()==User.eType.TEACHER){
                bundle.putParcelable("user", (Teacher)currUser);
                TeacherHomePageFragment teacherPage = new TeacherHomePageFragment();
                teacherPage.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, teacherPage).commit();
            }
            navigationView.setCheckedItem(R.id.nav_home_page);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_home_page:
               if (currUser.getmType()==User.eType.STUDENT){
                   getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new StudentHomePageFragment()).commit();
               }
               if (currUser.getmType()==User.eType.TEACHER){
                   getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new TeacherHomePageFragment()).commit();
               }
                break;
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new AccountFragment()).commit();
                break;
            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new AboutFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_logout:
                startActivity(new Intent(HomePage.this, LoginPage.class));
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}