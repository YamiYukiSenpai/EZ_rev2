package com.example.ryan.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String userID;

    private TextView name;
    private TextView mon;
    private TextView tues;
    private TextView weds;
    private TextView thur;
    private TextView fri;
    private TextView sat;
    private TextView sun;
    private TextView goal;
    private TextView current;
    private TextView currentPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getDatabase();
        findViews();

        DatabaseReference namer = FirebaseDatabase.getInstance().getReference(userID);
        DatabaseReference getSteps = namer.child("steps");

        namer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String realName = dataSnapshot.child("name").getValue(String.class);
                name.setText("Welcome Home, " + realName);

                int realGoal = dataSnapshot.child("goalSteps").getValue(Integer.class);
                goal.setText("Goal: " + realGoal);

                int  realSteps = dataSnapshot.child("realSteps").getValue(Integer.class);
                current.setText("Current Steps: " + realSteps);

                int percentSteps = (int)(((double) realSteps / (double) realGoal) * 100.0);
                currentPercent.setText("Percent Complete: " + percentSteps + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getSteps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int realMonday = dataSnapshot.child("monday").getValue(Integer.class);
                int realTuesday = dataSnapshot.child("tuesday").getValue(Integer.class);
                int realWednesday = dataSnapshot.child("wednesday").getValue(Integer.class);
                int realThursday = dataSnapshot.child("thursday").getValue(Integer.class);
                int realFriday = dataSnapshot.child("friday").getValue(Integer.class);
                int realSaturday = dataSnapshot.child("saturday").getValue(Integer.class);
                int realSunday = dataSnapshot.child("sunday").getValue(Integer.class);

                mon.setText("Monday: " + realMonday);
                tues.setText("Tuesday: " + realTuesday);
                weds.setText("Wednesday: " + realWednesday);
                thur.setText("Thursday: " + realThursday);
                fri.setText("Friday: " + realFriday);
                sat.setText("Saturday: " + realSaturday);
                sun.setText("Sunday: " + realSunday);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Popup Menu
        final Button settingsBtn = findViewById(R.id.homeSettings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HomeActivity.this, settingsBtn);

                popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.menuSettings:
                                Intent intent_settings = new Intent(HomeActivity.this, SettingsActivity.class);
                                startActivity(intent_settings);
                                return true;
                            case R.id.menuQuit:
                                finishAndRemoveTask();
                                return true;
                            case R.id.menuSignout:
                                mAuth.signOut();
                                Toast.makeText(HomeActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                                return true;
                        }
                        return HomeActivity.super.onOptionsItemSelected(item);
                    }

                });
                popup.show();
            }

        });

    }

    private void findViews(){
        name = findViewById(R.id.welcomeHome);
        mon = findViewById(R.id.home_monday);
        tues = findViewById(R.id.home_Tuesday);
        weds = findViewById(R.id.home_Wednesday);
        thur = findViewById(R.id.home_thursday);
        fri = findViewById(R.id.home_friday);
        sat = findViewById(R.id.home_saturday);
        sun = findViewById(R.id.home_sunday);

        goal = findViewById(R.id.goalSteps);
        current = findViewById(R.id.realSteps);
        currentPercent = findViewById(R.id.percentSteps);
    }


    private void getDatabase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

    }
}