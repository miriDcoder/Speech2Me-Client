package com.example.project;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;


public class HomePage extends AppCompatActivity {

    private DrawerLayout drawer;
    public DataBase db = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
              R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        Student currStudent = (Student)DbUtils.GetUserById(db.makeUserList(), id);
        displayMyInfo(currStudent);
    }

    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void displayMyInfo(Student iStudent) {
        EditText editTextMyInfo = (EditText) findViewById(R.id.editTextMyInfo);
        String myInfo = String.format("Hello, %s!{\n}Level: %s%nScore: %s%nKeep up the good work!",

                iStudent.getmFirstName(), iStudent.getmLevel(), iStudent.getmScore());
        String myInfo2= ("Hello, " +iStudent.getmFirstName() + "!Level:" + iStudent.getmLevel()+ "\nScore:" + iStudent.getmScore());


        editTextMyInfo.setText(myInfo2);
    }
}
