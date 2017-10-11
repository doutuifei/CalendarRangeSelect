package com.muzi.calendarrangeselect;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.muzi.calendarrangeselect.entity.DayTimeEntity;
import com.muzi.calendarrangeselect.entity.MonthTimeEntity;
import com.muzi.calendarrangeselect.entity.UpdataCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by muzi on 2017/10/11.
 * 727784430@qq.com
 */

public class CalendarView extends RecyclerView {

    private Context context;

    private NsLinearLayoutManager nsLinearLayoutManager;

    private DividerItemDecoration hDividerItemDecoration;

    private MonthTimeAdapter monthAdapter;

    //RecycleView分页滚动的工具类
    private PagingScrollHelper scrollHelper;

    //日历数据集合
    private ArrayList<MonthTimeEntity> dateList=new ArrayList<>();

    //默认显示6个月数据
    private int monthNum = 6;

    //不可选择天数
    private int unableSelectDay = 0;

    public CalendarView(Context context) {
        super(context);
        this.context = context;
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public int getMonthNum() {
        return monthNum;
    }

    /**
     * 在init();之前调用
     *
     * @param monthNum
     */
    public void setMonthNum(int monthNum) {
        this.monthNum = monthNum;
    }

    public int getUnableSelectDay() {
        return unableSelectDay;
    }

    public void setUnableSelectDay(int unableSelectDay) {
        this.unableSelectDay = unableSelectDay;
        UpdataCalendar.inTransitDay = unableSelectDay;
        if (monthAdapter != null) {
            initState();
        }
    }

    /**
     * 日历数据
     */
    private void getData() {
        Calendar calendar;
        for (int i = 0; i < monthNum; i++) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, i);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            dateList.add(new MonthTimeEntity(year, month));
        }
    }


    public void init() {
        EventBus.getDefault().register(this);
        getData();
        initCalenderSwipe();
        monthAdapter = new MonthTimeAdapter(dateList, context);
        initState();
    }

    /**
     * 还原状态
     */
    private void initState() {
        UpdataCalendar.startDay = new DayTimeEntity(0, 0, 0, 0);
        UpdataCalendar.stopDay = new DayTimeEntity(-1, -1, -1, -1);
        setAdapter(monthAdapter);
    }


    /**
     * 初始化日历滑动
     */
    private void initCalenderSwipe() {
        //翻页效果
        nsLinearLayoutManager = new NsLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        hDividerItemDecoration = new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL);
        setLayoutManager(nsLinearLayoutManager);
        addItemDecoration(hDividerItemDecoration);

        scrollHelper = new PagingScrollHelper();
        scrollHelper.setUpRecycleView(this);
        scrollHelper.setOnPageChangeListener(index -> {
            Log.d("CalendarView", dateList.get(index).getYear() + "-" + dateList.get(index).getMonth());
            Toast.makeText(context, dateList.get(index).getYear() + "-" + dateList.get(index).getMonth(), Toast.LENGTH_SHORT).show();
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventMainThread(UpdataCalendar event) {
        monthAdapter.notifyDataSetChanged();
        Log.d("CalendarView", UpdataCalendar.startDay.getMonth() + "月" + UpdataCalendar.startDay.getDay() + "日");

        if (UpdataCalendar.stopDay.getDay() == -1) {
            Log.d("CalendarView", "结束" + "\n" + "时间");
        } else {
            Log.d("CalendarView", UpdataCalendar.stopDay.getMonth() + "月" + UpdataCalendar.stopDay.getDay() + "日");

            Toast.makeText(context, UpdataCalendar.estimatedDate() + "天", Toast.LENGTH_SHORT).show();
        }
    }
}
