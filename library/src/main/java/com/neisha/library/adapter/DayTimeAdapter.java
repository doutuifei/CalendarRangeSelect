package com.neisha.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neisha.library.R;
import com.neisha.library.entity.DayTimeEntity;
import com.neisha.library.entity.UpdataCalendar;
import com.neisha.library.holder.DayTimeViewHolder;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;


import static com.neisha.library.entity.UpdataCalendar.inTransitDay;
import static com.neisha.library.entity.UpdataCalendar.tenancyTerm;

/**
 * Created by 木子 on 2017/08/08.
 */
public class DayTimeAdapter extends RecyclerView.Adapter<DayTimeViewHolder> {

    private ArrayList<DayTimeEntity> days;
    private Context context;

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
        View v = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        ret = new DayTimeViewHolder(v);

        return ret;
    }

    @Override
    public void onBindViewHolder(final DayTimeViewHolder holder, final int position) {
        final DayTimeEntity dayTimeEntity = days.get(position);
        calendarCurre.set(dayTimeEntity.getYear(), dayTimeEntity.getMonth() - 1, dayTimeEntity.getDay());
        //显示日期
        if (dayTimeEntity.getDay() != 0) {
            holder.select_ly_day.setVisibility(View.VISIBLE);
            holder.select_txt_day.setText(String.valueOf(dayTimeEntity.getDay()));
            //当前日期+在途日期之前的禁止点击
            if (calendarCurre.after(calendarLimit)) {
                //当前日期在 在途日期之后
                holder.select_ly_day.setEnabled(true);
            } else {
                holder.select_ly_day.setEnabled(false);
            }
        } else {
            holder.select_ly_day.setEnabled(false);
            holder.select_ly_day.setVisibility(View.INVISIBLE);
        }

        holder.select_ly_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdataCalendar.startDay.getYear() == 0) {          // 第一次点击开始的位置，因为开始默认参数是 0,0,0,0
                    setStartDay(dayTimeEntity, position);
                } else if (UpdataCalendar.startDay.getYear() > 0 && UpdataCalendar.stopDay.getYear() == -1) {      //已经点击了开始 ，点击结束位置，（默认结束位置-1,-1,-1,-1 说明还没有点击结束位置）
                    if (dayTimeEntity.getYear() > UpdataCalendar.startDay.getYear()) {
                        //如果选中的年份大于开始的年份，说明结束日期肯定大于开始日期 ，合法的 ，将该item的天数的 信息  赋给 结束日期
                        setStopDay(dayTimeEntity, position);
                    } else if (dayTimeEntity.getYear() == UpdataCalendar.startDay.getYear()) {
                        //如果选中的年份 等于 选中的年份
                        if (dayTimeEntity.getMonth() > UpdataCalendar.startDay.getMonth()) {
                            //如果改item的天数的月份大于开始日期的月份，说明结束日期肯定大于开始日期 ，合法的 ，将该item的天数的 信息  赋给 结束日期
                            setStopDay(dayTimeEntity, position);
                        } else if (dayTimeEntity.getMonth() == UpdataCalendar.startDay.getMonth()) {
                            //年份月份 都相等
                            if (dayTimeEntity.getDay() >= UpdataCalendar.startDay.getDay()) {
                                //判断天数 ，如果 该item的天数的 日子大于等于 开始日期的 日子 ，说明结束日期合法的 ，将该item的天数的 信息  赋给 结束日期
                                setStopDay(dayTimeEntity, position);
                            } else {
                                //天数小与初始  从新选择开始  ，结束日期重置，开始日期为当前的位置的天数的信息
                                resetStopDay();
                                setStartDay(dayTimeEntity, position);
                            }
                        } else {
                            //选中的月份 比开始日期的月份还小，说明 结束位置不合法，结束日期重置，开始日期为当前的位置的天数的信息
                            resetStopDay();
                            setStartDay(dayTimeEntity, position);
                        }

                    } else {
                        //选中的年份 比开始日期的年份还小，说明 结束位置不合法，结束日期重置，开始日期为当前的位置的天数的信息
                        resetStopDay();
                        setStartDay(dayTimeEntity, position);
                    }
                } else if (UpdataCalendar.startDay.getYear() > 0 && UpdataCalendar.startDay.getYear() > 1) {
                    //已经点击开始和结束   第三次点击 ，重新点击开始
                    resetStopDay();
                    setStartDay(dayTimeEntity, position);
                }
                EventBus.getDefault().post(new UpdataCalendar()); // 发消息刷新适配器，目的为了显示日历上各个日期的背景颜色
            }
        });


        if (UpdataCalendar.startDay.getYear() == dayTimeEntity.getYear() && UpdataCalendar.startDay.getMonth() == dayTimeEntity.getMonth() && UpdataCalendar.startDay.getDay() == dayTimeEntity.getDay()
                && UpdataCalendar.stopDay.getYear() == dayTimeEntity.getYear() && UpdataCalendar.stopDay.getMonth() == dayTimeEntity.getMonth() && UpdataCalendar.stopDay.getDay() == dayTimeEntity.getDay()) {
            //开始和结束同一天
            setStartAndrStopBackGround(holder);
            setTextState(holder, context.getResources().getString(R.string.text_calendar_one));
            setTextSelectColor(holder);
        } else if (UpdataCalendar.startDay.getYear() == dayTimeEntity.getYear() && UpdataCalendar.startDay.getMonth() == dayTimeEntity.getMonth() && UpdataCalendar.startDay.getDay() == dayTimeEntity.getDay()) {
            //该item是 开始日期
            setStartBackGround(holder);
            setTextState(holder, context.getResources().getString(R.string.text_calendar_start));
            setTextSelectColor(holder);
        } else if (UpdataCalendar.stopDay.getYear() == dayTimeEntity.getYear() && UpdataCalendar.stopDay.getMonth() == dayTimeEntity.getMonth() && UpdataCalendar.stopDay.getDay() == dayTimeEntity.getDay()) {
            //该item是 结束日期
            setStopBackGround(holder);
            setTextState(holder, context.getResources().getString(R.string.text_calendar_stop));
            setTextSelectColor(holder);
        } else if (dayTimeEntity.getMonthPosition() >= UpdataCalendar.startDay.getMonthPosition() && dayTimeEntity.getMonthPosition() <= UpdataCalendar.stopDay.getMonthPosition()) {
            //处于开始和结束之间的点
            setTextState(holder, null);
            if (dayTimeEntity.getMonthPosition() == UpdataCalendar.startDay.getMonthPosition() && dayTimeEntity.getMonthPosition() == UpdataCalendar.stopDay.getMonthPosition()) {
                //开始和结束是一个月份
                if (dayTimeEntity.getDay() > UpdataCalendar.startDay.getDay() && dayTimeEntity.getDay() < UpdataCalendar.stopDay.getDay()) {
                    setBetweenBackGround(holder);
                    setTextSelectColor(holder);
                } else {
                    resetBackGround(holder);
                    setTextUnSelectColor(holder);
                }
            } else if (UpdataCalendar.startDay.getMonthPosition() != UpdataCalendar.stopDay.getMonthPosition()) {
                // 日期和 开始 不是一个月份
                if (dayTimeEntity.getMonthPosition() == UpdataCalendar.startDay.getMonthPosition() && dayTimeEntity.getDay() > UpdataCalendar.startDay.getDay()) {
                    //和初始相同月  天数往后
                    setBetweenBackGround(holder);
                    setTextSelectColor(holder);
                } else if (dayTimeEntity.getMonthPosition() == UpdataCalendar.stopDay.getMonthPosition() && dayTimeEntity.getDay() < UpdataCalendar.stopDay.getDay()) {
                    //和结束相同月   天数往前
                    setBetweenBackGround(holder);
                    setTextSelectColor(holder);
                } else if (dayTimeEntity.getMonthPosition() != UpdataCalendar.startDay.getMonthPosition() && dayTimeEntity.getMonthPosition() != UpdataCalendar.stopDay.getMonthPosition()) {
                    //和 开始结束都不是同一个月
                    setBetweenBackGround(holder);
                    setTextSelectColor(holder);
                } else {
                    resetBackGround(holder);
                    setTextUnSelectColor(holder);

                }
            }
        } else {
            resetBackGround(holder);
            setTextState(holder, null);
            setTextUnSelectColor(holder);
        }

        //在途
        if (calendarCurre.after(calendarToday) && calendarCurre.before(calendarLimit) || calendarCurre.equals(calendarLimit)) {
            setTextState(holder, context.getResources().getString(R.string.text_calendar_transit));
        }

        //今日
        if (calendarCurre.equals(calendarToday)) {
            setTextState(holder, context.getResources().getString(R.string.text_calendar_today));
        }

    }


    /*
        packageDay-选中区间范围
        给定选中区间范围和开始时间，计算结束时间范围
     */
    private void calculateStopDay(int position) {
        calendarStart.set(UpdataCalendar.startDay.getYear(), UpdataCalendar.startDay.getMonth() - 1, UpdataCalendar.startDay.getDay());
        calendarEnd.set(UpdataCalendar.startDay.getYear(), UpdataCalendar.startDay.getMonth() - 1, UpdataCalendar.startDay.getDay());
        calendarEnd.add(Calendar.DAY_OF_MONTH, tenancyTerm);
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
        calendarStart.set(UpdataCalendar.startDay.getYear(), UpdataCalendar.startDay.getMonth() - 1, UpdataCalendar.startDay.getDay());
        calendarEnd.set(UpdataCalendar.startDay.getYear(), UpdataCalendar.startDay.getMonth() - 1, UpdataCalendar.startDay.getDay());
        calendarEnd.add(Calendar.DAY_OF_MONTH, tenancyTerm);
        if (calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
            //起租时间和归还时间在同一个月
            // 归还时间=起租时间+租期-1。
            int endPosition = UpdataCalendar.startDay.getDayPosition() + tenancyTerm;
            setStopDay(days.get(endPosition), endPosition);
        } else {
            //起租时间和归还时间不在同一个月
            // 归还时间在（起租时间+租期-1）之后，从这之后开始遍历。
            for (int i = UpdataCalendar.startDay.getDayPosition() + tenancyTerm; i < MonthTimeAdapter.allDays.size(); i++) {
                if (MonthTimeAdapter.allDays.get(i).getYear() == (int) calendarEnd.get(Calendar.YEAR) && MonthTimeAdapter.allDays.get(i).getMonth() == (calendarEnd.get(Calendar.MONTH) + 1) && MonthTimeAdapter.allDays.get(i).getDay() == (int) calendarEnd.get(Calendar.DAY_OF_MONTH)) {
                    setStopDay(MonthTimeAdapter.allDays.get(i), position);
                    break;
                }
            }
        }
    }

    /*
        设置日历下文字内容
     */
    private void setTextState(DayTimeViewHolder holder, String text) {
        if (text == null) {
            holder.select_txt_day_state.setVisibility(View.GONE);
        } else {
            holder.select_txt_day_state.setVisibility(View.VISIBLE);
        }
        holder.select_txt_day_state.setText(text);
    }

    /*
        设置归还时间
     */
    private void setStopDay(DayTimeEntity dayTimeEntity, int position) {
        UpdataCalendar.stopDay.setDay(dayTimeEntity.getDay());
        UpdataCalendar.stopDay.setMonth(dayTimeEntity.getMonth());
        UpdataCalendar.stopDay.setYear(dayTimeEntity.getYear());
        UpdataCalendar.stopDay.setMonthPosition(dayTimeEntity.getMonthPosition());
        UpdataCalendar.stopDay.setDayPosition(position);
    }

    /*
        重置归还时间
     */
    private void resetStopDay() {
        UpdataCalendar.stopDay.setDay(-1);
        UpdataCalendar.stopDay.setMonth(-1);
        UpdataCalendar.stopDay.setYear(-1);
        UpdataCalendar.stopDay.setMonthPosition(-1);
        UpdataCalendar.stopDay.setDayPosition(-1);
    }

    /*
        设置起租时间
     */
    private void setStartDay(DayTimeEntity dayTimeEntity, int position) {
        UpdataCalendar.startDay.setDay(dayTimeEntity.getDay());           // 该item 天数的 年月日等信息  赋给  开始日期
        UpdataCalendar.startDay.setMonth(dayTimeEntity.getMonth());
        UpdataCalendar.startDay.setYear(dayTimeEntity.getYear());
        UpdataCalendar.startDay.setMonthPosition(dayTimeEntity.getMonthPosition());
        UpdataCalendar.startDay.setDayPosition(position);

        //如果固定租期>0，就计算归还时间
        if (tenancyTerm > 0) {
            calculateStopDayUpdate(position);
        }


    }

    /*
        设置起租选中背景
     */
    private void setStartBackGround(DayTimeViewHolder holder) {
        holder.select_ly_day.setBackgroundResource(R.drawable.bg_time_start);
    }

    /*
       设置归还选中背景
    */
    private void setStopBackGround(DayTimeViewHolder holder) {
        holder.select_ly_day.setBackgroundResource(R.drawable.bg_time_stop);
    }

    /*
        设置起租、归还之间的背景颜色
     */
    private void setBetweenBackGround(DayTimeViewHolder holder) {
        holder.select_ly_day.setBackgroundResource(R.color.calendar_back_between);
    }

    /*
        设置未选中背景颜色
     */
    private void resetBackGround(DayTimeViewHolder holder) {
        holder.select_ly_day.setBackgroundResource(R.color.white);
    }

    /*
        设置起租、归还重叠背景颜色
     */
    private void setStartAndrStopBackGround(DayTimeViewHolder holder) {
        holder.select_ly_day.setBackgroundResource(R.drawable.bg_time_startstop);
    }


    /*
        设置字体选中颜色
     */
    private void setTextSelectColor(DayTimeViewHolder holder) {
        holder.select_txt_day.setTextColor(context.getResources().getColor(R.color.calendar_text_select));
        holder.select_txt_day_state.setTextColor(context.getResources().getColor(R.color.calendar_text_select));
    }

    /*
      设置字体未选中颜色
    */
    private void setTextUnSelectColor(DayTimeViewHolder holder) {
        holder.select_txt_day.setTextColor(context.getResources().getColor(R.color.calendar_text_unselect));
        holder.select_txt_day_state.setTextColor(context.getResources().getColor(R.color.calendar_text_unselect));
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
