package com.example.ryan.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    Switch push_notif;
    Button settings_back;

    SharedPreferences shared_pref;

    public static final String pref_name = "prefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        shared_pref = getApplicationContext().getSharedPreferences(pref_name, 0);

        push_notif = findViewById(R.id.push_Switch);
        settings_back = findViewById(R.id.settings_back);

        settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
        //Intent back_home = new Intent(SettingsActivity.this, HomeActivity.class);
        save_settings();
        super.onBackPressed();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
