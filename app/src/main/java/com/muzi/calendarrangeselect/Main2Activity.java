package com.muzi.calendarrangeselect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.muzi.calendarrangeselect.entity.DayTimeEntity;
import com.muzi.calendarrangeselect.entity.MonthTimeEntity;

public class Main2Activity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = (TextView) findViewById(R.id.month);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        calendarView.setOnCalendarSelect(new CalendarView.onCalendarSelect() {
            @Override
            public void OnMonthSwhit(MonthTimeEntity entity) {
                textView.setText(entity.toString());
            }

            @Override
            public void OnDaySelect(DayTimeEntity startDay, DayTimeEntity endDay, int day) {
                Log.d("Main2Activity", startDay.toString());
                Log.d("Main2Activity", endDay.toString());
                Log.d("Main2Activity", "day:" + day);
            }
        });
    }
}
