package com.example.ryan.myapplication;

import android.content.Intent;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
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

    int goal_steps;

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

                goal_steps = dataSnapshot.child("goalSteps").getValue(Integer.class);
                goal.setText("Goal: " + goal_steps);

                int  realSteps = dataSnapshot.child("realSteps").getValue(Integer.class);
                current.setText("Current Steps: " + realSteps);

                int percentSteps = (int)(((double) realSteps / (double) goal_steps) * 100.0);
                currentPercent.setText("Percent Complete: " + percentSteps + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                barChart.getAxisLeft().setDrawLabels(false);
                barChart.getAxisRight().setDrawLabels(false);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                barChart.setBackgroundColor(Color.TRANSPARENT);

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

                update_vsgoal(total_steps);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
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

        barChart = findViewById(R.id.barChart);
        goal_chart = findViewById(R.id.goal_pieChart);
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

    private void update_vsgoal(int total_steps) {
        ArrayList<PieEntry> goal_entries = new ArrayList<>();
        ArrayList<String> pie_label = new ArrayList<>();

        goal_entries.add(new PieEntry((float)total_steps, "Total"));
        goal_entries.add(new PieEntry((float)goal_steps, "Goal"));

        pie_label.add("Total");
        pie_label.add("Goal");

        PieDataSet data_set = new PieDataSet(goal_entries, "Weekly Goal");
        PieData goal_data = new PieData(data_set);

        goal_chart.setData(goal_data);
    }
}