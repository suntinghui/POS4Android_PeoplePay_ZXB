package com.zxb.view;

import java.util.Calendar;

import com.zxb.util.DateSelectUtil;


/**
 * A Labeler that displays months
 */
public class MonthLabeler extends Labeler {
    private final String mFormatString;

    public MonthLabeler(String formatString) {
        super(180, 60);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectfromCalendar(DateSelectUtil.addMonths(time, val));
    }

    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
        return DateSelectUtil.getMonth(c, mFormatString);
    }
}