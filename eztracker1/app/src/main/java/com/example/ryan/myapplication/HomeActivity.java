package com.example.ryan.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button settingsBtn = findViewById(R.id.homeSettings);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HomeActivity.this, settingsBtn);

                popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getApplicationContext(),
                                item.getTitle(), Toast.LENGTH_SHORT).show();
                        switch(item.getItemId()){
                            case R.id.menuQuit:
                                finishAndRemoveTask();
                                break;
                                //return true;
                            case R.id.menuSettings:
                                Intent settings = new Intent(HomeActivity.this, SettingsActivity.class);
                                startActivity(settings);
                                break;
                                //return true;
                        }
                        return true;
                        //return HomeActivity.super.onOptionsItemSelected(item);
                    }

                });
                popup.show();
            }

        });

    }
}