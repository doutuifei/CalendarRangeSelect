package com.muzi.calendarrangeselect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.muzi.calendarrangeselect.entity.DayTimeEntity;
import com.muzi.calendarrangeselect.entity.MonthTimeEntity;
import com.muzi.calendarrangeselect.widget.CalendarView;

public class Main2Activity extends AppCompatActivity {

    private TextView textView;
    private TextView startTime;          //开始时间
    private TextView stopTime;           //结束时间
    private Button btn1, btn2;
    private EditText edit1, edit2;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = (TextView) findViewById(R.id.month);

        startTime = (TextView) findViewById(R.id.plan_time_txt_start);
        stopTime = (TextView) findViewById(R.id.plan_time_txt_stop);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);

        btn1.setOnClickListener(v ->
                calendarView.setUnableSelectDay(Integer.parseInt(edit1.getText().toString()))
        );

        btn2.setOnClickListener(v ->
                calendarView.setMultipleChoice(Integer.parseInt(edit2.getText().toString()))
        );

        initCalendar();
    }

    private void initCalendar() {
        calendarView = (CalendarView) findViewById(R.id.calendarView);

        calendarView.setMonthNum(12);

        calendarView.setOnCalendarSelect(new CalendarView.onCalendarSelect() {
            @Override
            public void OnMonthSwhit(MonthTimeEntity entity) {
                textView.setText(entity.toString());
            }

            @Override
            public void OnDaySelect(DayTimeEntity startDay, DayTimeEntity endDay, int day) {
                Toast.makeText(Main2Activity.this, "开始时间："
                        + startDay.toString() + "\n结束时间："
                        + endDay.toString() + "\n共"
                        + day + "天", Toast.LENGTH_SHORT).show();

                startTime.setText(startDay.getMonth() + "月" + startDay.getDay() + "日" + "\n");
                stopTime.setText(endDay.getMonth() + "月" + endDay.getDay() + "日");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        calendarView.onDestory();
    }
}
