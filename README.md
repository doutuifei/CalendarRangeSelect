# CalendarRangeSelect
Android日历连续区间选择

## 效果预览
![image](https://github.com/TurnTears/CalendarRangeSelect/blob/c6d8f5a5202e53a47ce21d097c85750b69b06100/image/preview.gif)

## Gradle配置

* Step 1. build.gradle(Project:***)添加
```allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

* Step 2. build.gradle(Module:app)
```
dependencies {
	        compile 'com.github.TurnTears:CalendarRangeSelect:1.0'
	}
```

## 基本使用

* 1、布局文件
```
 <com.muzi.calendarrangeselect.widget.CalendarView
        android:id="@+id/calendarView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

* 2、Activity
```
 calendarView = (CalendarView) findViewById(R.id.calendarView);
        
//必须在setOnCalendarSelect()之前
calendarView.setMonthNum(12);

//回调
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
      
@Override
    protected void onDestroy() {
        super.onDestroy();
        calendarView.onDestory();
    }      
```

* 这样就完成了，可以开始点击了



## CalendarView基本功能介绍
* 设置日历显示的月份数量默认为6个月 
```
 calendarView.setMonthNum(12);
```

* 设置不可点击的天数， 从今天以后开始计算，在途状态不可点击
```
 calendarView.setUnableSelectDay(2);
```

* 设置连续选择
```
 calendarView.setMultipleChoice(6);
```


## 感谢
* 一行代码让RecyclerView分页滚动 [HorizontalPage](https://github.com/zhuguohui/HorizontalPage)
