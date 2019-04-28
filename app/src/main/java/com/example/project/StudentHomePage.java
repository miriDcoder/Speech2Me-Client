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
import android.widget.Button;
import android.widget.TextView;


public class StudentHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private DrawerLayout drawer;
    public DataBase db = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container,
                    new SettingsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_settings);
        }

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView textViewHello = (TextView)findViewById(R.id.textViewHeader);
        TextView textViewScore = (TextView)findViewById(R.id.textViewScore);
        TextView textViewLevel = (TextView)findViewById(R.id.textViewLevel);
        Student currStudent = (Student)DbUtils.GetUserById(db.makeUserList(), id);
        setEditTextsPositions(textViewHello, textViewScore, textViewLevel);
        displayMyInfo(currStudent, textViewHello, textViewScore, textViewLevel);
        Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
       switch(menuItem.getItemId()){
           case R.id.nav_settings:
               getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new SettingsFragment()).commit();
               break;
           case R.id.nav_score:
               getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new StudentScoreFragment()).commit();
               break;
           case R.id.nav_play:
               getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new PlayFragment()).commit();
               break;
           case R.id.nav_home_page:
               getSupportFragmentManager().beginTransaction().replace(R.id.student_fragment_container, new StudentHomePageFragment()).commit();
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

    private void displayMyInfo(Student iStudent, TextView iTextViewHeader, TextView iTextViewScore, TextView iTextViewLevel) {
        iTextViewHeader.setText(String.format("שלום, %s!", iStudent.getmFirstName()));
        iTextViewScore.setText(String.format("ניקוד: %d", iStudent.getmScore()));
        iTextViewLevel.setText(String.format("שלב: %d", iStudent.getmLevel()));
    }

    private void setEditTextsPositions(TextView iTextViewHeader, TextView iTextViewScore, TextView iTextViewLevel){
        iTextViewHeader.setX(10);
        iTextViewHeader.setY(150);
        iTextViewScore.setX(iTextViewHeader.getX());
        iTextViewScore.setY(iTextViewHeader.getY() + iTextViewHeader.getHeight() + 100);
        iTextViewLevel.setX(iTextViewHeader.getX());
        iTextViewLevel.setY(iTextViewScore.getY() + iTextViewScore.getHeight() + 100);
    }
}
