package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;


public class TeacherHomePage extends AppCompatActivity {

    private DrawerLayout drawer;
    public DataBase db = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView textViewHeader = (TextView)findViewById(R.id.textViewHeader);
        TextView textViewNumOfStuds = (TextView)findViewById(R.id.textViewNumStudents);
        Teacher currTeacher = (Teacher)DbUtils.GetUserById(db.makeUserList(), id);
        setEditTextsPositions(textViewHeader, textViewNumOfStuds);
        displayMyInfo(currTeacher, textViewHeader, textViewNumOfStuds);
        Button buttonStats = (Button)findViewById(R.id.buttonStatistics);
        Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
        buttonStats.setY(buttonPlay.getY() + buttonPlay.getBottom() + 230);
    }

    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void displayMyInfo(Teacher iTeacher, TextView iTextViewHeader, TextView iTextViewNumOfStuds) {
        iTextViewHeader.setText(String.format("Hello, %s!", iTeacher.getmFirstName()));
        iTextViewNumOfStuds.setText(String.format("Students enrolled to app: %s", iTeacher.getmNumOfStudents()));
    }

    private void setEditTextsPositions(TextView iTextViewHeader, TextView iTextViewNumOfStuds){
        iTextViewHeader.setX(0);
        iTextViewHeader.setY(150);
        iTextViewNumOfStuds.setX(iTextViewHeader.getX());
        iTextViewNumOfStuds.setY(iTextViewHeader.getY() + iTextViewHeader.getHeight() + 100);
    }
}
