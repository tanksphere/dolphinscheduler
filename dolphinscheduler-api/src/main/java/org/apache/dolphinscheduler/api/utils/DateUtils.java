/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dolphinscheduler.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


@Slf4j
public final class DateUtils {

	public static String format(DateFormat dateFormat) {
		return format(new Date(), dateFormat.getFormat());
	}

	public static String format(Date date, DateFormat dateFormat) {
		return format(date, dateFormat.getFormat());
	}

	private static String format(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			String dataString = dateFormat.format(date);
			return dataString;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static Date parse(String value, DateFormat dateFormat) {
		return parse(value, dateFormat.getFormat());
	}

	public static Date parse(String value, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			Date date = dateFormat.parse(value);
			return date;
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * <p>
	 * 计算开始日期和结束日期的间隔时间
	 * </p>
	 * 
	 * @param begin
	 *            开始日期
	 * @param end
	 *            结束日期
	 * @return 差值
	 */
	public static long substractDate(Date begin, Date end) {
		if (begin == null || end == null) {
			return 0L;
		}
		return substractTime(begin.getTime(), end.getTime());
	}

	/**
	 * <p>
	 * 计算开始时间和结束时间的间隔时间
	 * </p>
	 * 
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 差值
	 */
	public static long substractTime(long beginTime, long endTime) {
		return endTime - beginTime;
	}

	/**
	 * <p>
	 * 获取时间描述 XX天XX小时XX分钟
	 * </p>
	 * 
	 * @param subTime
	 * @return XX天XX小时XX分钟
	 */
	public static String subTimeDesc(long subTime) {
		long day = subTime / (24 * 60 * 60 * 1000);
		long hour = (subTime / (60 * 60 * 1000) - day * 24);
		long min = ((subTime / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long second = (subTime / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		StringBuilder builder = new StringBuilder();
		if (day > 0) {
			builder.append(day).append("天");
		}
		if (hour > 0) {
			builder.append(hour).append("小时");
		}
		if (min > 0) {
			builder.append(min).append("分钟");
		}
		if (builder.length() == 0 && second > 0) {
			builder.append("1分钟");
		}
		return builder.length() == 0 ? StringUtils.EMPTY : builder.toString();
	}

	private static SimpleDateFormat getDateParser(String pattern) {
		return new SimpleDateFormat(pattern);
	}
	
    public static Date curDate() {
        return new Date();
    }

    public static String curDateStr(String strFormat) {
        Date date = new Date();
        return getDateParser(strFormat).format(date);
    }

    public static String curDateStr() {
        Date date = new Date();
        return getDateParser("yyyy-MM-dd").format(date);
    }

    public static Timestamp curTimestamp() {
        return new Timestamp((new Date()).getTime());
    }

    public static Date toDate(String dateString, String pattern) {
        Date date = null;

        try {
            date = getDateParser(pattern).parse(dateString);
            return date;
        } catch (Exception var4) {
            log.warn("解析date字符串时出错,返回null. dateString:" + dateString + "ex:" + var4);
            return null;
        }
    }

    public static Date toDate(String dateString) {
        Date date = null;

        try {
            date = getDateParser("yyyy-MM-dd").parse(dateString);
            return date;
        } catch (Exception var3) {
            log.warn("日期date转换出错null. dateString:" + dateString + "ex:" + var3);
            return null;
        }
    }

    public static Date toDate(long dateLong) {
        Date date = null;

        try {
            date = new Date(dateLong);
            return date;
        } catch (Exception var4) {
            log.warn("日期date转换出错null. dateLong:" + dateLong + "ex:" + var4);
            return null;
        }
    }

    public static Date toDateTime(String dateString) {
        Date date = null;

        try {
            date = getDateParser("yyyy-MM-dd HH:mm:ss").parse(dateString);
            return date;
        } catch (Exception var3) {
            log.warn("日期date转换出错null. dateString:" + dateString + "ex:" + var3);
            return null;
        }
    }

    public static String toDateStr(Date date, String pattern) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期转换输入的字符串为空");
            }

            return "";
        } else {
            return getDateParser(pattern).format(date);
        }
    }

    public static String toDateStr(Date date) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期转换输入的字符串为空");
            }

            return "";
        } else {
            return getDateParser("yyyy-MM-dd").format(date);
        }
    }

    public static String toDateTimeStr(Date date) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期转换输入的字符串为空");
            }

            return "";
        } else {
            return getDateParser("yyyy-MM-dd HH:mm:ss").format(date);
        }
    }

    public static String curDateStr8() {
        Date date = new Date();
        return getDateParser("yyyyMMdd").format(date);
    }

    public static String curDateStr10() {
        Date date = new Date();
        return getDateParser("yyyy-MM-dd").format(date);
    }

    public static String curDateTimeStr14() {
        Date date = new Date();
        return getDateParser("yyyyMMddHHmmss").format(date);
    }

    public static String curDateTimeStr19() {
        Date date = new Date();
        return getDateParser("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String curTimeStr6() {
        Date date = new Date();
        return getDateParser("HHmmss").format(date);
    }

    public static String curDateMselStr17() {
        Date date = new Date();
        return getDateParser("yyyyMMddHHmmssSSS").format(date);
    }

    public static String curDateMselStr18() {
        Date date = new Date();
        return getDateParser("yyyyMMddHHmmssSSSS").format(date);
    }

    public static Date toDate8(String dateStr) {
        Date date = null;

        try {
            date = getDateParser("yyyyMMdd").parse(dateStr);
            return date;
        } catch (Exception var3) {
            log.warn("日期date转换出错null. dateString:" + dateStr + "ex:" + var3);
            return null;
        }
    }

    public static Date toDate10(String dateStr) {
        Date date = null;

        try {
            date = getDateParser("yyyy-MM-dd").parse(dateStr);
            return date;
        } catch (Exception var3) {
            log.warn("日期date转换出错null. dateString:" + dateStr + "ex:" + var3);
            return null;
        }
    }

    public static Date toDateTime14(String dateStr) {
        Date date = null;

        try {
            date = getDateParser("yyyyMMddHHmmss").parse(dateStr);
            return date;
        } catch (Exception var3) {
            log.warn("日期date转换出错null. dateString:" + dateStr + "ex:" + var3);
            return null;
        }
    }

    public static Date toDateTime19(String dateStr) {
        Date date = null;

        try {
            date = getDateParser("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            return date;
        } catch (Exception var3) {
            log.warn("日期date转换出错null. dateString:" + dateStr + "ex:" + var3);
            return null;
        }
    }

    public static String toDateStr8(Date date) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错");
            }

            return "";
        } else {
            return getDateParser("yyyyMMdd").format(date);
        }
    }

    public static String toDateStr10(Date date) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错");
            }

            return "";
        } else {
            return getDateParser("yyyy-MM-dd").format(date);
        }
    }

    public static String toDateTimeStr14(Date date) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return "";
        } else {
            return getDateParser("yyyyMMddHHmmss").format(date);
        }
    }

    public static String toDateTimeStr19(Date date) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return "";
        } else {
            return getDateParser("yyyy-MM-dd HH:mm:ss").format(date);
        }
    }

    public static String toDateTimeCNStr19(Long dateTime) {
        if (dateTime == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return "";
        } else {
            try {
                return getDateParser("yyyy年MM月dd日 HH点mm分ss秒").format(new Date(dateTime.longValue()));
            } catch (Exception var2) {
                log.warn("日期date转换出错null. dateLong:" + dateTime + "ex:" + var2);
                return null;
            }
        }
    }

    public static Date addDays(Date date, int days) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return null;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(5, days);
            return calendar.getTime();
        }
    }

    public static Date addMonths(Date date, int months) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return null;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, months);
            return calendar.getTime();
        }
    }

    public static Date addHours(Date date, int hours) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return null;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, hours);
            return calendar.getTime();
        }
    }

    public static Date addMinutes(Date date, int minutes) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.info("日期date转换出错，字符串为空");
            }

            return null;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, minutes);
            return calendar.getTime();
        }
    }

    public static boolean isDateBetween(Date date, Date date1, Date date2) {
        return (date1.before(date) || date1.equals(date)) && (date.before(date2) || date.equals(date2));
    }

    public static int getDaysInterval(Date fromDate, Date toDate) {
        if (fromDate != null && toDate != null) {
            long timeInterval = toDate.getTime() - fromDate.getTime();
            int daysInterval = (int)(timeInterval / 86400000L);
            return daysInterval;
        } else {
            log.warn("getDaysInterval输入参数不正确，返回0");
            return 0;
        }
    }

    public static int getWeekOfYear(Date date) {
        if (date == null) {
            log.warn("输入为null，返回ֵ-1");
            return -1;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setFirstDayOfWeek(2);
            calendar.setTime(date);
            int week = calendar.get(3);
            return week;
        }
    }

    public static int getDayOfWeek(Date date) {
        if (date == null) {
            log.warn("输入为null，返回ֵ-1");
            return -1;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int day = calendar.get(7);
            --day;
            if (0 == day) {
                day = 7;
            }

            return day;
        }
    }

    public static Date getLastDayInMonth(Date date) {
        return getLastDayInMonth(date, 0);
    }

    public static Date getLastDayInNextMonth(Date date) {
        return getLastDayInMonth(date, 1);
    }

    public static Date getLastDayInMonth(Date date, int i) {
        if (date == null) {
            if (log.isInfoEnabled()) {
                log.warn("输入为null，返回ֵnull");
            }

            return null;
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(2, i + 1);
            calendar.set(5, 1);
            calendar.add(5, -1);
            return calendar.getTime();
        }
    }

    public static String toDateTime(long times) {
        times /= 1000L;
        long hours = times / 3600L;
        times -= hours * 3600L;
        long minutes = times / 60L;
        times -= minutes * 60L;
        String result = hours + "(h) " + minutes + "(m) " + times + "(s)";
        return result;
    }

    public static String dateDiff(String startTime, String endTime, String format) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        return dateDiff(diff);
    }

    public static String dateDiff(String startTime, String endTime) throws Exception {
        return dateDiff(startTime, endTime, "yyyyMMddHHmmss");
    }

    public static String dateDiff(String startTime) throws Exception {
        return dateDiff(startTime, curDateTimeStr14(), "yyyyMMddHHmmss");
    }

    public static String dateDiff(Date startTime, Date endTime) {
        long diff = endTime.getTime() - startTime.getTime();
        return dateDiff(diff);
    }

    public static long dateDiffDays(Date startTime, Date endTime) {
        return startTime != null && endTime != null ? (endTime.getTime() - startTime.getTime()) / 86400000L : 0L;
    }

    public static String dateDiff(long millisecond) {
        long nd = 86400000L;
        long nh = 3600000L;
        long nm = 60000L;
        long ns = 1000L;
        long day = millisecond / nd;
        long hour = millisecond % nd / nh;
        long min = millisecond % nd % nh / nm;
        long sec = millisecond % nd % nh % nm / ns;
        String ret = "";
        if (sec > 0L) {
            ret = sec + "秒";
        }

        ret = min + "分" + ret;
        if (hour > 0L) {
            ret = hour + "小时" + ret;
        }

        if (day > 0L) {
            ret = day + "天" + ret;
        }

        return ret;
    }

    public static Timestamp toTimestamp(String recReviseTime) {
        try {
            if (recReviseTime != null && !recReviseTime.trim().equals("")) {
                Date date = toDateTime(recReviseTime);
                return date != null ? new Timestamp(date.getTime()) : null;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return null;
    }

    public static Timestamp toTimestampByDateStr10(String recReviseTime) {
        try {
            if (recReviseTime != null && !recReviseTime.trim().equals("")) {
                return new Timestamp(toDate(recReviseTime).getTime());
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return null;
    }

    public static Timestamp toTimestampByDateStr10(Date date) {
        return toTimestampByDateStr10(toDateStr(date));
    }

    public static boolean isSameYear(Date date1, Date date2) {
        GregorianCalendar cal1 = new GregorianCalendar();
        cal1.setTime(date1);
        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.setTime(date2);
        return cal1.get(1) == cal2.get(1);
    }

    public static Date getMinDateInDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static Date getMaxDateInDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static long todaySecondsLeft() {
        return 86400 - org.apache.commons.lang3.time.DateUtils.getFragmentInSeconds(Calendar.getInstance(), Calendar.DATE);
    }

    public static Date[] getDayDateSection(Date date) {
        Date[] dates = new Date[2];
        /**
         * 例
         * Wed Jul 01 00:00:00 CST 2020
         * Fri Jul 31 23:59:59 CST 2020
         */
        dates[0] = getMinDateInDay(date);
        dates[1] = getMaxDateInDay(date);
        return dates;
    }

    public static Date[] getMonthDateSection(Date date) {
        Date[] dates = new Date[2];
        /**
         * 例
         * Wed Jul 01 00:00:00 CST 2020
         * Fri Jul 31 23:59:59 CST 2020
         */
        dates[0] = getMinDateInDay(addDays(getLastDayInMonth(addMonths(date, -1)), 1));
        dates[1] = getMaxDateInDay(getLastDayInMonth(date));
        return dates;
    }

    /**
     * 获取本周的最后一天
     * @return String
     * **/
    public static Date getLastDayInWeek(Date date) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    public static boolean isWorkDay(Date date, Integer startHour, Integer endHour) {
        Calendar cal = Calendar.getInstance();
        if(date != null) {
            cal.setTime(date);
        }
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(Calendar.SUNDAY == day || Calendar.SATURDAY == day) {
            return false;
        }
        if(startHour == null && endHour == null) {
            return true;
        }
        int hour = Integer.parseInt(format(cal.getTime(), "HH"));
        if(startHour != null && startHour > 0 && startHour > hour) {
            return false;
        }
        if(endHour != null && endHour > 0 && endHour <= hour) {
            return false;
        }
        return true;
    }
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
