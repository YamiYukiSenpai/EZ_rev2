package com.example.ryan.myapplication;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    private BarChart barChart;
    private PieChart goal_chart;

    private TextView name;
    private TextView goal;
    private TextView current;
    private TextView currentPercent;
    private Button goalButton;
    private Button nav_btn;

    int goal_steps;
    int total_steps;

    NavigationView nav_view;
    private DrawerLayout menu_drawer;
    MenuItem about_us, update_info, view_info, signout;
    Switch push_notif;

    Toolbar toolbar;

    SharedPreferences shared_pref;
    public static final String pref_name = "prefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.homeBar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        shared_pref = getApplicationContext().getSharedPreferences(pref_name, 0);
        about_us = findViewById(R.id.nav_about_us);
        update_info = findViewById(R.id.nav_update_info);
        view_info = findViewById(R.id.nav_view_info);
        signout = findViewById(R.id.nav_signout);
        push_notif = findViewById(R.id.drawer_push_switch);

        nav_btn = findViewById(R.id.homeNav);
        menu_drawer = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        switch (item.getItemId()) {
                            case R.id.nav_about_us:     // About Us
                                Intent intent_about_us = new Intent(
                                        HomeActivity.this, AboutUsActivity.class);
                                finish();
                                startActivity(intent_about_us);
                                return true;
                            case R.id.nav_update_info:
                                Intent intent_update_info = new Intent(
                                        HomeActivity.this, UpdateActivity.class);
                                finish();
                                startActivity(intent_update_info);
                                return true;
                            case R.id.nav_view_info:
                                Intent intent_view_info = new Intent(
                                        HomeActivity.this, ViewDataActivity.class);
                                finish();
                                startActivity(intent_view_info);
                                return true;
                            case R.id.nav_signout:
                                mAuth.signOut();
                                Toast.makeText(HomeActivity.this, "Signed Out",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(
                                        HomeActivity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                                return true;
                        }
                        menu_drawer.closeDrawers();
                        return true;
                    }
                });

        menu_drawer.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        //load_settings();
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        //save_settings();
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                }
        );

        getDatabase();
        findViews();

        DatabaseReference namer = FirebaseDatabase.getInstance().getReference(userID);
        DatabaseReference getSteps = namer.child("steps");

        namer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String realName = dataSnapshot.child("name").getValue(String.class);
                name.setText(realName+ "'s Daily Steps");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Cannot Contact Server", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (HomeActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });



        getSteps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float realMonday = dataSnapshot.child("monday").getValue(Integer.class);
                float realTuesday = dataSnapshot.child("tuesday").getValue(Integer.class);
                float realWednesday = dataSnapshot.child("wednesday").getValue(Integer.class);
                float realThursday = dataSnapshot.child("thursday").getValue(Integer.class);
                float realFriday = dataSnapshot.child("friday").getValue(Integer.class);
                float realSaturday = dataSnapshot.child("saturday").getValue(Integer.class);
                float realSunday = dataSnapshot.child("sunday").getValue(Integer.class);

                int total_steps = (int) (realMonday + realTuesday + realWednesday + realThursday +
                        realFriday + realSaturday + realSunday);

                barChart.setDrawBarShadow(false);
                barChart.setDrawGridBackground(false);
                barChart.setPinchZoom(false);
                barChart.setDoubleTapToZoomEnabled(false);
                barChart.setDragEnabled(true);
                barChart.setScaleEnabled(true);
                barChart.getLegend().setEnabled(false);
                barChart.getDescription().setEnabled(false);
                barChart.getXAxis().setDrawGridLines(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisRight().setDrawGridLines(false);
                barChart.getAxisRight().setDrawLabels(false);
                barChart.getAxisLeft().setTextColor(Color.WHITE);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                barChart.setBackgroundColor(Color.TRANSPARENT);
                barChart.animateY(1000,Easing.Linear);
                barChart.getAxisLeft().setAxisMinimum(0f);

                goal_chart.setDrawHoleEnabled(true);
                goal_chart.setTransparentCircleRadius(70f);
                goal_chart.setHoleRadius(70f);

                ArrayList<BarEntry> barEntries = new ArrayList<>();

                System.out.print(realMonday);

                barEntries.add(new BarEntry(0,realMonday));
                barEntries.add(new BarEntry(1,realTuesday));
                barEntries.add(new BarEntry(2,realWednesday));
                barEntries.add(new BarEntry(3,realThursday));
                barEntries.add(new BarEntry(4,realFriday));
                barEntries.add(new BarEntry(5,realSaturday));
                barEntries.add(new BarEntry(6,realSunday));

                BarDataSet dataSet = new BarDataSet(barEntries, "Steps Taken");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                BarData barData = new BarData(dataSet);
                barData.setValueTextColor(Color.WHITE);
                barData.setValueTextSize(14f);

                barChart.setData(barData);

                String[] days = new String[] {"Mon","Tue","Wed","Thurs","Fri","Sat","Sun"};

                XAxis xAxis = barChart.getXAxis();
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new MyXAxisValueFormatter(days));

                total_steps = (int) (realMonday + realTuesday + realWednesday + realThursday +
                        realFriday + realSaturday + realSunday);

                ArrayList<PieEntry> goal_entries = new ArrayList<>();
                ArrayList<String> pie_label = new ArrayList<>();

                goal_chart.setDrawHoleEnabled(true);
                goal_chart.setBackgroundColor(Color.TRANSPARENT);
                goal_chart.setTransparentCircleRadius(40f);
                goal_chart.setHoleRadius(40f);
                goal_chart.setTouchEnabled(false);
                goal_chart.notifyDataSetChanged();
                goal_chart.invalidate();
                goal_chart.setHoleColor(Color.TRANSPARENT);
                goal_chart.getDescription().setEnabled(false);
                goal_chart.animateY(1000,Easing.EaseInOutCubic);
                goal_chart.getLegend().setEnabled(true);
                goal_chart.getLegend().setTextColor(Color.WHITE);
                goal_chart.getLegend().setTextSize(12f);
                goal_chart.setDrawEntryLabels(false);

                Legend l = goal_chart.getLegend();
                l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                int goalDisplayer = dataSnapshot.child("goalSteps").getValue(Integer.class);
                float goal_steps = dataSnapshot.child("goalSteps").getValue(Integer.class);
                goal.setText("Goal: " + goalDisplayer);

                goal_entries.add(new PieEntry((float)total_steps, "Total Steps"));
                goal_entries.add(new PieEntry((goal_steps - total_steps), "Remaining Steps"));

                int percentSteps = (int)(((double) total_steps / (double) goal_steps) * 100.0);
                currentPercent.setText(percentSteps + "%");

                pie_label.add("Total");
                pie_label.add("Goal");

                PieDataSet data_set = new PieDataSet(goal_entries, "");
                PieData goal_data = new PieData(data_set);

                data_set.setColors(ColorTemplate.JOYFUL_COLORS);
                goal_data.setValueTextColor(Color.WHITE);
                goal_data.setValueTextSize(13f);

                goal_chart.setData(goal_data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                Toast.makeText(HomeActivity.this, "Cannot connect to server.", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);
            }
        });

        nav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

            }
        });

        /*settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PopupMenu popup = new PopupMenu(HomeActivity.this, settingsBtn);

                popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.menuSettings:
                                Intent intent_settings = new Intent(HomeActivity.this, SettingsActivity.class);
                                finish();
                                startActivity(intent_settings);
                                return true;
                            case R.id.menuQuit:
                                finish();
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

        });*/

        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, pop.class);
                startActivity(intent);
            }
        });


    }

    private void findViews(){
        name = findViewById(R.id.welcomeHome);
        goal = findViewById(R.id.goal_steps);
        currentPercent = findViewById(R.id.percentSteps);

        barChart = findViewById(R.id.barChart);
        goal_chart = findViewById(R.id.goal_pieChart);

        goalButton = findViewById(R.id.goalButton);
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter{

        private String[] mValues;
        public MyXAxisValueFormatter(String[] values){
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menu_drawer.openDrawer(GravityCompat.START);
                //load_settings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void load_settings() {
        push_notif.setChecked(shared_pref.getBoolean("push_notif", false));
    }

    private void save_settings() {
        SharedPreferences.Editor settings_pref = shared_pref.edit();
        settings_pref.putBoolean("push_notif", push_notif.isChecked());
        settings_pref.commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}