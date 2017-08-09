package com.muzi.calendarrangeselect.entity;

import java.util.Calendar;

/**
 * Created by 木子 on 2017/08/08.
 * 用于EventBus发送消息
 */
public class UpdataCalendar {
    public static DayTimeEntity startDay;
    public static DayTimeEntity stopDay;

    /*
        计算两个日期差
     */
    public static int estimatedDate() {
        Calendar startCalendar = Calendar.getInstance();
        Calendar stopCalendar = Calendar.getInstance();
        startCalendar.set(startDay.getYear(), startDay.getMonth(), startDay.getDay());
        stopCalendar.set(stopDay.getYear(), stopDay.getMonth(), stopDay.getDay());
        return (int) ((stopCalendar.getTime().getTime() - startCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24) + 1);
    }

}
