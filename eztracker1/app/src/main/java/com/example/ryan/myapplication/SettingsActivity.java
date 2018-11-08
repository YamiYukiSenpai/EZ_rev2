package com.example.ryan.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    Switch push_notif;
    Button settings_back;

    SharedPreferences shared_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        push_notif = (Switch)findViewById(R.id.push_Switch);
        settings_back = (Button)findViewById(R.id.settings_back);

        settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_settings();
            }
        });

        load_settings();
    }

    private void save_settings() {
        SharedPreferences.Editor settings_pref = shared_pref.edit();
        settings_pref.putBoolean("push_notif", push_notif.isChecked());
        settings_pref.commit();
    }

    private void load_settings() {
        push_notif.setChecked(shared_pref.getBoolean("push_notif", false));
    }

    @Override
    public void onBackPressed() {
        save_settings();
        super.onBackPressed();
    }
}
