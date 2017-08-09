package com.muzi.calendarrangeselect.selectTime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muzi.calendarrangeselect.MonthTimeActivity;
import com.muzi.calendarrangeselect.MonthTimeAdapter;
import com.muzi.calendarrangeselect.R;
import com.muzi.calendarrangeselect.entity.DayTimeEntity;
import com.muzi.calendarrangeselect.entity.UpdataCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by 木子 on 2017/08/08.
 */
public class DayTimeAdapter extends RecyclerView.Adapter<DayTimeViewHolder> {

    private ArrayList<DayTimeEntity> days;
    private Context context;
    private int inTransitDay = 2;//在途天数
    private int packageDay = 7;//选中区间范围
    private Calendar calendarToday;//手机当前日期
    private Calendar calendarLimit;//当前日期+在途时间
    private Calendar calendarCurre;//item日期
    private Calendar calendarStart;//点击开始日期
    private Calendar calendarEnd;//结束日期

    public DayTimeAdapter(ArrayList<DayTimeEntity> days, Context context) {
        this.days = days;
        this.context = context;
        calendarToday = Calendar.getInstance();
        calendarLimit = Calendar.getInstance();
        calendarCurre = Calendar.getInstance();
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        calendarLimit.add(Calendar.DAY_OF_MONTH, inTransitDay);
    }

    @Override
    public DayTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DayTimeViewHolder ret = null;
        // 不需要检查是否复用，因为只要进入此方法，必然没有复用
        // 因为RecyclerView 通过Holder检查复用
        View v = LayoutInflater.from(context).inflate(R.layout.item_recycler_selectday, parent, false);
        ret = new DayTimeViewHolder(v);

        return ret;
    }

    @Override
    public void onBindViewHolder(final DayTimeViewHolder holder, final int position) {
        final DayTimeEntity dayTimeEntity = days.get(position);
        calendarCurre.set(dayTimeEntity.getYear(), dayTimeEntity.getMonth() - 1, dayTimeEntity.getDay());
        //显示日期
        if (dayTimeEntity.getDay() != 0) {
            holder.select_txt_day.setText(dayTimeEntity.getDay() + "");
            //当前日期+在途日期之前的禁止点击
            if (calendarCurre.after(calendarLimit)) {
                //当前日期在 在途日期之后
                holder.select_ly_day.setEnabled(true);
            } else {
                holder.select_ly_day.setEnabled(false);
            }
        } else {
            holder.select_ly_day.setEnabled(false);
        }

        holder.select_ly_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MonthTimeActivity.startDay.getYear() == 0) {          // 第一次点击开始的位置，因为开始默认参数是 0,0,0,0
                    setStartDay(dayTimeEntity, position);
                } else if (MonthTimeActivity.startDay.getYear() > 0 && MonthTimeActivity.stopDay.getYear() == -1) {      //已经点击了开始 ，点击结束位置，（默认结束位置-1,-1,-1,-1 说明还没有点击结束位置）
                    if (dayTimeEntity.getYear() > MonthTimeActivity.startDay.getYear()) {
                        //如果选中的年份大于开始的年份，说明结束日期肯定大于开始日期 ，合法的 ，将该item的天数的 信息  赋给 结束日期
                        setStopDay(dayTimeEntity, position);
                    } else if (dayTimeEntity.getYear() == MonthTimeActivity.startDay.getYear()) {
                        //如果选中的年份 等于 选中的年份
                        if (dayTimeEntity.getMonth() > MonthTimeActivity.startDay.getMonth()) {
                            //如果改item的天数的月份大于开始日期的月份，说明结束日期肯定大于开始日期 ，合法的 ，将该item的天数的 信息  赋给 结束日期
                            setStopDay(dayTimeEntity, position);
                        } else if (dayTimeEntity.getMonth() == MonthTimeActivity.startDay.getMonth()) {
                            //年份月份 都相等
                            if (dayTimeEntity.getDay() >= MonthTimeActivity.startDay.getDay()) {
                                //判断天数 ，如果 该item的天数的 日子大于等于 开始日期的 日子 ，说明结束日期合法的 ，将该item的天数的 信息  赋给 结束日期
                                setStopDay(dayTimeEntity, position);
                            } else {
                                //天数小与初始  从新选择开始  ，结束日期重置，开始日期为当前的位置的天数的信息
                                setStartDay(dayTimeEntity, position);
                                resetStopDay();
                            }
                        } else {
                            //选中的月份 比开始日期的月份还小，说明 结束位置不合法，结束日期重置，开始日期为当前的位置的天数的信息
                            setStartDay(dayTimeEntity, position);
                            resetStopDay();
                        }

                    } else {
                        //选中的年份 比开始日期的年份还小，说明 结束位置不合法，结束日期重置，开始日期为当前的位置的天数的信息
                        setStartDay(dayTimeEntity, position);
                        resetStopDay();
                    }
                } else if (MonthTimeActivity.startDay.getYear() > 0 && MonthTimeActivity.startDay.getYear() > 1) {
                    //已经点击开始和结束   第三次点击 ，重新点击开始
                    setStartDay(dayTimeEntity, position);
                    resetStopDay();
                }
                EventBus.getDefault().post(new UpdataCalendar()); // 发消息刷新适配器，目的为了显示日历上各个日期的背景颜色
            }
        });


        if (MonthTimeActivity.startDay.getYear() == dayTimeEntity.getYear() && MonthTimeActivity.startDay.getMonth() == dayTimeEntity.getMonth() && MonthTimeActivity.startDay.getDay() == dayTimeEntity.getDay()
                && MonthTimeActivity.stopDay.getYear() == dayTimeEntity.getYear() && MonthTimeActivity.stopDay.getMonth() == dayTimeEntity.getMonth() && MonthTimeActivity.stopDay.getDay() == dayTimeEntity.getDay()) {
            //开始和结束同一天
            holder.select_ly_day.setBackgroundResource(R.drawable.bg_time_startstop);
            holder.select_txt_day_state.setText("一天");
        } else if (MonthTimeActivity.startDay.getYear() == dayTimeEntity.getYear() && MonthTimeActivity.startDay.getMonth() == dayTimeEntity.getMonth() && MonthTimeActivity.startDay.getDay() == dayTimeEntity.getDay()) {
            //该item是 开始日期
            holder.select_ly_day.setBackgroundResource(R.drawable.bg_time_start);
            holder.select_txt_day_state.setText("起租");
        } else if (MonthTimeActivity.stopDay.getYear() == dayTimeEntity.getYear() && MonthTimeActivity.stopDay.getMonth() == dayTimeEntity.getMonth() && MonthTimeActivity.stopDay.getDay() == dayTimeEntity.getDay()) {
            //该item是 结束日期
            holder.select_ly_day.setBackgroundResource(R.drawable.bg_time_stop);
            holder.select_txt_day_state.setText("归还");
        } else if (dayTimeEntity.getMonthPosition() >= MonthTimeActivity.startDay.getMonthPosition() && dayTimeEntity.getMonthPosition() <= MonthTimeActivity.stopDay.getMonthPosition()) {
            //处于开始和结束之间的点
            holder.select_txt_day_state.setText(null);
            if (dayTimeEntity.getMonthPosition() == MonthTimeActivity.startDay.getMonthPosition() && dayTimeEntity.getMonthPosition() == MonthTimeActivity.stopDay.getMonthPosition()) {
                //开始和结束是一个月份
                if (dayTimeEntity.getDay() > MonthTimeActivity.startDay.getDay() && dayTimeEntity.getDay() < MonthTimeActivity.stopDay.getDay()) {
                    holder.select_ly_day.setBackgroundResource(R.color.blue);
                } else {
                    holder.select_ly_day.setBackgroundResource(R.color.white);
                }
            } else if (MonthTimeActivity.startDay.getMonthPosition() != MonthTimeActivity.stopDay.getMonthPosition()) {
                // 日期和 开始 不是一个月份
                if (dayTimeEntity.getMonthPosition() == MonthTimeActivity.startDay.getMonthPosition() && dayTimeEntity.getDay() > MonthTimeActivity.startDay.getDay()) {
                    //和初始相同月  天数往后
                    holder.select_ly_day.setBackgroundResource(R.color.blue);
                } else if (dayTimeEntity.getMonthPosition() == MonthTimeActivity.stopDay.getMonthPosition() && dayTimeEntity.getDay() < MonthTimeActivity.stopDay.getDay()) {
                    //和结束相同月   天数往前
                    holder.select_ly_day.setBackgroundResource(R.color.blue);
                } else if (dayTimeEntity.getMonthPosition() != MonthTimeActivity.startDay.getMonthPosition() && dayTimeEntity.getMonthPosition() != MonthTimeActivity.stopDay.getMonthPosition()) {
                    //和 开始结束都不是同一个月
                    holder.select_ly_day.setBackgroundResource(R.color.blue);
                } else {
                    holder.select_ly_day.setBackgroundResource(R.color.white);
                }
            }
        } else {
            holder.select_ly_day.setBackgroundResource(R.color.white);
            holder.select_txt_day_state.setText(null);
        }

        //今日
        if (calendarCurre.equals(calendarToday)) {
            holder.select_txt_day_state.setText("今天");
        }

    }


    /*
        packageDay-选中区间范围
        给定选中区间范围和开始时间，计算结束时间范围
     */
    private void calculateStopDay(int position) {
        calendarStart.set(MonthTimeActivity.startDay.getYear(), MonthTimeActivity.startDay.getMonth() - 1, MonthTimeActivity.startDay.getDay());
        calendarEnd.set(MonthTimeActivity.startDay.getYear(), MonthTimeActivity.startDay.getMonth() - 1, MonthTimeActivity.startDay.getDay());
        calendarEnd.add(Calendar.DAY_OF_MONTH, packageDay - 1);
        for (DayTimeEntity day : MonthTimeAdapter.allDays) {
            if (day.getYear() == (int) calendarEnd.get(Calendar.YEAR) && day.getMonth() == (calendarEnd.get(Calendar.MONTH) + 1) && day.getDay() == (int) calendarEnd.get(Calendar.DAY_OF_MONTH)) {
                setStopDay(day, position);
                break;
            }
        }
    }

    /*
       calculateStopDay优化版
       packageDay-选中区间范围
       给定选中区间范围和开始时间，计算结束时间范围
    */
    private void calculateStopDayUpdate(int position) {
        calendarStart.set(MonthTimeActivity.startDay.getYear(), MonthTimeActivity.startDay.getMonth() - 1, MonthTimeActivity.startDay.getDay());
        calendarEnd.set(MonthTimeActivity.startDay.getYear(), MonthTimeActivity.startDay.getMonth() - 1, MonthTimeActivity.startDay.getDay());
        calendarEnd.add(Calendar.DAY_OF_MONTH, packageDay - 1);
        if (calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
            //起租时间和归还时间在同一个月
            // 归还时间=起租时间+租期-1。
            int endPosition = MonthTimeActivity.startDay.getDayPosition() + packageDay - 1;
            setStopDay(days.get(endPosition), endPosition);
        } else {
            //起租时间和归还时间不在同一个月
            // 归还时间在（起租时间+租期-1）之后，从这之后开始遍历。
            for (int i = MonthTimeActivity.startDay.getDayPosition() + packageDay - 1; i < MonthTimeAdapter.allDays.size(); i++) {
                if (MonthTimeAdapter.allDays.get(i).getYear() == (int) calendarEnd.get(Calendar.YEAR) && MonthTimeAdapter.allDays.get(i).getMonth() == (calendarEnd.get(Calendar.MONTH) + 1) && MonthTimeAdapter.allDays.get(i).getDay() == (int) calendarEnd.get(Calendar.DAY_OF_MONTH)) {
                    setStopDay(MonthTimeAdapter.allDays.get(i), position);
                    break;
                }
            }
        }
    }

    private void setStopDay(DayTimeEntity dayTimeEntity, int position) {
        MonthTimeActivity.stopDay.setDay(dayTimeEntity.getDay());
        MonthTimeActivity.stopDay.setMonth(dayTimeEntity.getMonth());
        MonthTimeActivity.stopDay.setYear(dayTimeEntity.getYear());
        MonthTimeActivity.stopDay.setMonthPosition(dayTimeEntity.getMonthPosition());
        MonthTimeActivity.stopDay.setDayPosition(position);
    }

    private void resetStopDay() {
        MonthTimeActivity.stopDay.setDay(-1);
        MonthTimeActivity.stopDay.setMonth(-1);
        MonthTimeActivity.stopDay.setYear(-1);
        MonthTimeActivity.stopDay.setMonthPosition(-1);
        MonthTimeActivity.stopDay.setDayPosition(-1);
    }

    private void setStartDay(DayTimeEntity dayTimeEntity, int position) {
        MonthTimeActivity.startDay.setDay(dayTimeEntity.getDay());           // 该item 天数的 年月日等信息  赋给  开始日期
        MonthTimeActivity.startDay.setMonth(dayTimeEntity.getMonth());
        MonthTimeActivity.startDay.setYear(dayTimeEntity.getYear());
        MonthTimeActivity.startDay.setMonthPosition(dayTimeEntity.getMonthPosition());
        MonthTimeActivity.startDay.setDayPosition(position);
    }


    private void log(String tag, Calendar calendar) {
        Log.d(tag, calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public int getItemCount() {
        int ret = 0;
        if (days != null) {
            ret = days.size();
        }
        return ret;
    }

}
