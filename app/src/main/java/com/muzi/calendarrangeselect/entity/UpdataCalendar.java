package com.muzi.calendarrangeselect.entity;

import java.util.Calendar;

/**
 * Created by 木子 on 2017/08/08.
 * 用于EventBus发送消息
 */
public class UpdataCalendar {
    public static DayTimeEntity startDay;
    public static DayTimeEntity stopDay;
    public static int inTransitDay = 2;//在途天数
    public static int tenancyTerm = 6;//固定租期

    /*
        计算两个日期差
     */
    public static int estimatedDate() {
        Calendar startCalendar = Calendar.getInstance();
        Calendar stopCalendar = Calendar.getInstance();
        startCalendar.set(startDay.getYear(), startDay.getMonth()-1, startDay.getDay());
        stopCalendar.set(stopDay.getYear(), stopDay.getMonth()-1, stopDay.getDay());
        return (int) ((stopCalendar.getTime().getTime() - startCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24) + 1);
    }


    /*
    设置固定租期
 */
    public static void setTenancyTerm(int day) {
        if (day < 1) {
            try {
                throw new Exception("固定租期不能小于1天");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tenancyTerm = day - 1;
    }

    /*
        设置在途时间
     */
    public static void setInTransitDay(int day) {
        inTransitDay = day;
    }
}
