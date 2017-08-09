package com.muzi.calendarrangeselect;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muzi.calendarrangeselect.entity.DayTimeEntity;
import com.muzi.calendarrangeselect.entity.MonthTimeEntity;
import com.muzi.calendarrangeselect.entity.UpdataCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

import static com.muzi.calendarrangeselect.entity.UpdataCalendar.startDay;
import static com.muzi.calendarrangeselect.entity.UpdataCalendar.stopDay;

/**
 * Created by 木子 on 2017/08/08.
 */
public class MonthTimeActivity extends Activity {
    private TextView startTime;          //开始时间
    private TextView stopTime;           //结束时间
    private RecyclerView reycycler;
    private MonthTimeAdapter adapter;
    private ArrayList<MonthTimeEntity> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        EventBus.getDefault().register(this);

    }

    private void initData() {
        startDay = new DayTimeEntity(0, 0, 0, 0);
        stopDay = new DayTimeEntity(-1, -1, -1, -1);
        datas = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        c.add(Calendar.MONTH, 1);
        int nextYear = c.get(Calendar.YEAR);
        int nextMonth = c.get(Calendar.MONTH) + 1;

        datas.add(new MonthTimeEntity(year, month));                //当前月份
        datas.add(new MonthTimeEntity(nextYear, nextMonth));        //下个月
        adapter = new MonthTimeAdapter(datas, MonthTimeActivity.this);
        reycycler.setAdapter(adapter);

    }

    private void initView() {
        startTime = (TextView) findViewById(R.id.plan_time_txt_start);
        stopTime = (TextView) findViewById(R.id.plan_time_txt_stop);

        reycycler = (RecyclerView) findViewById(R.id.plan_time_calender);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,   // 上下文
                        LinearLayout.VERTICAL,  //垂直布局,
                        false);

        reycycler.setLayoutManager(layoutManager);
    }

    public void onEventMainThread(UpdataCalendar event) {
        adapter.notifyDataSetChanged();
        startTime.setText(UpdataCalendar.startDay.getMonth() + "月" + UpdataCalendar.startDay.getDay() + "日" + "\n");
        if (UpdataCalendar.stopDay.getDay() == -1) {
            stopTime.setText("结束" + "\n" + "时间");
        } else {
            stopTime.setText(UpdataCalendar.stopDay.getMonth() + "月" + UpdataCalendar.stopDay.getDay() + "日" + "\n");
            Toast.makeText(this, UpdataCalendar.estimatedDate() + "天", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
