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

    // private FirebaseDatabase database;
    // private DatabaseReference dbRef;
    private FirebaseAuth firebaseAuth;
    // private FirebaseUser user;

    //  private String userID;
    // private TextView name;

   // private FirebaseDatabase database;
   // private DatabaseReference dbRef;
    private FirebaseAuth firebaseAuth;
   // private FirebaseUser user;

  //  private String userID;
   // private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
       /* database = FirebaseDatabase.getInstance();
        dbRef =  database.getReference();
        name = findViewById(R.id.welcomeHome);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation ds = dataSnapshot.getValue(UserInformation.class);
                showData(dataSnapshot);
                name.setText("Welcome "+ds.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                showData(dataSnapshot);

                name.setText("Welcome "+ds.getName());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/

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
                            case R.id.menuView:
                                Intent intentView = new Intent(HomeActivity.this, ViewDataActivity.class);
                                finish();
                                startActivity(intentView);
                                return true;
                            case R.id.menuSettings:
                                Toast.makeText(HomeActivity.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                Intent intent_settings = new Intent(HomeActivity.this, SettingsActivity.class);
                                startActivity(intent_settings);
                                return true;
                            case R.id.menuQuit:
                                finishAndRemoveTask();
                                return true;
                            case R.id.menuSignout:
                                firebaseAuth.signOut();
                                Toast.makeText(HomeActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                                return true;
                            case R.id.menuUpdate:
                                Intent intentUpdate = new Intent(HomeActivity.this, UpdateActivity.class);
                                finish();
                                startActivity(intentUpdate);
                                return true;
                        }
                        return HomeActivity.super.onOptionsItemSelected(item);
                    }

                });
                popup.show();
            }

        });

    }

/*
    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot a : dataSnapshot.getChildren()){
            UserInformation uInfo = new UserInformation();
            //userID = user.getUid();
            uInfo.setName(a.getValue(UserInformation.class).getName());
            uInfo.setWeight(a.getValue(UserInformation.class).getWeight());
            uInfo.setHeight(a.getValue(UserInformation.class).getHeight());
            uInfo.setDob(a.getValue(UserInformation.class).getDob());

            List<UserInformation> arraylist = new ArrayList<UserInformation>();

            List<UserInformation> arraylist = new ArrayList<UserInformation>();
            arraylist.add(uInfo);
        }
    }*/
}