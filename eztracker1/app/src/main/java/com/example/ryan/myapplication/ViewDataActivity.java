package com.example.ryan.myapplication;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ViewDataActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private TextView name;
    private TextView weight;
    private TextView height;
    private TextView dob;
    private String userID;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        getDatabase();
        findAllViews();

        Button backButton = findViewById(R.id.viewBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataActivity.this, HomeActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void findAllViews() {
        name = findViewById(R.id.viewName);
        weight = findViewById(R.id.viewWeight);
        height = findViewById(R.id.viewHeight);
        dob = findViewById(R.id.viewDob);
        mListView = findViewById(R.id.listview);
    }

    private void getDatabase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(ViewDataActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

    }
}

