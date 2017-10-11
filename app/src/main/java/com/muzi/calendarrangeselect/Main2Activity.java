package com.muzi.calendarrangeselect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.init();
    }
}
