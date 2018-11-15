package com.example.ryan.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbRef;
    private EditText editTextName;
    private EditText editTextHeight;
    private EditText editTextWeight;
    private EditText editTextDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            Intent intent = new Intent (UpdateActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        dbRef = FirebaseDatabase.getInstance().getReference();
        editTextName = findViewById(R.id.updateName);
        editTextWeight = findViewById(R.id.updateWeight);
        editTextHeight = findViewById(R.id.updateHeight);
        editTextDob = findViewById(R.id.updateDob);

        //read data here
        Button updateSave = findViewById(R.id.updateSave);
        updateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewUser();
                Toast.makeText(UpdateActivity.this, "Information Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateActivity.this, SettingsActivity.class);
                finish();
                startActivity(intent);
            }

            private void writeNewUser() {
                String name = editTextName.getText().toString().trim();
                String height = editTextHeight.getText().toString().trim();
                String weight = editTextWeight.getText().toString().trim();
                String dob = editTextDob.getText().toString().trim();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                UserInformation userInfo = new UserInformation(name, height, weight, dob);
                dbRef.child(user.getUid()).setValue(userInfo);

            }
        });

        Button updateBack = findViewById(R.id.updateBackButton);
        updateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, SettingsActivity.class);
                finish();
                startActivity(intent);

            }
        });
    }
}
