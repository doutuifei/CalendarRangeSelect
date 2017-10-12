package com.neisha.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.neisha.library.adapter.MonthTimeAdapter;
import com.neisha.library.entity.DayTimeEntity;
import com.neisha.library.entity.MonthTimeEntity;
import com.neisha.library.entity.UpdataCalendar;

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
    private ArrayList<MonthTimeEntity> dateList = new ArrayList<>();

    //默认显示6个月数据
    private int monthNum = 6;


    private onCalendarSelect onCalendarSelect;


    public CalendarView(Context context) {
        super(context);
        this.context = context;
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    /**
     * 显示的日历月份数，默认显示6个月
     * 在init();之前调用
     *
     * @param monthNum
     */
    public void setMonthNum(int monthNum) {
        this.monthNum = monthNum;
    }


    /**
     * 设置在途天数
     * 从今天以后开始计算
     * 在途状态不可点击
     *
     * @param unableSelectDay
     */
    public void setUnableSelectDay(int unableSelectDay) {
        UpdataCalendar.inTransitDay = unableSelectDay;
        if (monthAdapter != null) {
            resetState();
        }
    }

    /**
     * 设置连续选择
     *
     * @param day
     */
    public void setMultipleChoice(int day) {
        UpdataCalendar.setTenancyTerm(day);
        if (monthAdapter != null) {
            resetState();
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
            int month = calendar.get(Calendar.MONTH) + 1;
            dateList.add(new MonthTimeEntity(year, month));
        }

        if (onCalendarSelect != null) {
            onCalendarSelect.OnMonthSwhit(dateList.get(0));
        }
    }

    public void setOnCalendarSelect(CalendarView.onCalendarSelect onCalendarSelect) {
        this.onCalendarSelect = onCalendarSelect;
        init();
    }

    /**
     * 初始化界面
     */
    private void init() {
        EventBus.getDefault().register(this);
        getData();
        initCalenderSwipe();
        monthAdapter = new MonthTimeAdapter(dateList, context);
        resetState();
    }

    /**
     * 还原状态
     */
    private void resetState() {
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
        hDividerItemDecoration.setDrawable(new EmptyDrawable());
        setLayoutManager(nsLinearLayoutManager);
        addItemDecoration(hDividerItemDecoration);

        scrollHelper = new PagingScrollHelper();
        scrollHelper.setUpRecycleView(this);

        scrollHelper.setOnPageChangeListener(new PagingScrollHelper.onPageChangeListener() {
            @Override
            public void onPageChange(int index) {
                if (onCalendarSelect != null) {
                    onCalendarSelect.OnMonthSwhit(dateList.get(index));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventMainThread(UpdataCalendar event) {
        monthAdapter.notifyDataSetChanged();
        if (onCalendarSelect != null && UpdataCalendar.stopDay.getDay() != -1) {
            onCalendarSelect.OnDaySelect(UpdataCalendar.startDay, UpdataCalendar.stopDay, UpdataCalendar.estimatedDate());
        }

    }

    public interface onCalendarSelect {
        void OnMonthSwhit(MonthTimeEntity entity);

        void OnDaySelect(DayTimeEntity startDay, DayTimeEntity endDay, int day);
    }

    public void onDestory(){
        EventBus.getDefault().unregister(this);
    }
}
