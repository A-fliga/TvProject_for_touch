package com.app.tvproject.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具类
 * Created by www on 2017/4/13.
 */
public class TimeUtil {

    /**
     * 获取当前时间
     *
     * @return 时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        String time = "";
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");
            time = sDateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDay() {
        return getTime().substring(0, 10).trim().replace("-", "");
    }

    /**
     * 获取天
     *
     * @return
     */
    public static String getDetailDay(String time1) {
        String time = "";
        try {
            if (time1.length() < 1) {
                return time;
            }
            String time2 = getTime();
            if (!time2.subSequence(0, 4).equals(time1.subSequence(0, 4))) {
                time = time1.substring(0, 10);
                return time;
            }
            String mmdd1 = time1.substring(5, 10).trim();
            mmdd1 = mmdd1.replace("-", "");
            if (mmdd1.startsWith("0")) {
                mmdd1 = mmdd1.replaceFirst("0", "");
            }
            String mmdd2 = time2.substring(5, 10).trim();
            mmdd2 = mmdd2.replace("-", "");
            if (mmdd2.startsWith("0")) {
                mmdd2 = mmdd2.replaceFirst("0", "");
            }
            int day = Integer.parseInt(mmdd2) - Integer.parseInt(mmdd1);
            if (day >= 1 && day < 2) {
                String[] newtime = time1.split(" ");
                if (newtime.length < 2) {
                    return time;
                }
                time = "昨天 ";
                return time;
            }
            if (day < 1) {
                String[] newtime = time1.split(" ");
                if (newtime.length < 2) {
                    return time;
                }
                time = "今天 ";
                return time;
            }
            if (day >= 2) {
                time = time1.substring(5, 10);
                return time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取GMT时间的毫秒值
     *
     * @param time
     * @return
     */
    public static long getGmtMillions(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdf.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取本地时间的毫秒值
     *
     * @return
     */
    public static long getLocMillions() {
        try {
            return new Date().getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取时间差
     *
     * @return
     */
//    public static String getTimesToNow(String date) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String returnText = null;
//        try {
//            long from = format.parse(date).getTime();
//            long to = new Date().getTime();
//            int days = (int) ((to - from) / (1000 * 60 * 60 * 24));
//            if (days == 0) {//一天以内，以分钟或者小时显示
//                int hours = (int) ((to - from) / (1000 * 60 * 60));
//                if (hours == 0) {
//                    int minutes = (int) ((to - from) / (1000 * 60));
//                    if (minutes == 0) {
//                        returnText = ContextUtil.getContext().getString(R.string.recent);
//                    } else {
//                        returnText = minutes + " " + ContextUtil.getContext().getString(R.string.minutes_ago);
//                    }
//                } else {
//                    returnText = hours + " " + ContextUtil.getContext().getString(R.string.hours_ago);
//                }
//            } else if (days == 1) {
//                returnText = ContextUtil.getContext().getString(R.string.yesterday);
//            } else {
//                returnText = days + " " + ContextUtil.getContext().getString(R.string.days_ago);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return returnText;
//    }

    public static String getDuration(int duration) {
        StringBuilder sb = new StringBuilder();
        if (duration < 60) {
            sb.append(duration).append('"');
        } else {
            if (duration < (60 * 60)) {
                int minutes = duration / 60;
                int seconds = duration % 60;
                sb.append(minutes).append("'");
                if (seconds != 0) {
                    sb.append(seconds).append('"');
                }
            } else {
                int hours = duration / (60 * 60);
                int minutes = (duration % (60 * 60)) / 60;
                int seconds = duration % 60;
                sb.append(hours);
                if (minutes != 0) {
                    sb.append(":").append(minutes).append("'");
                    if (seconds != 0) {
                        sb.append(seconds).append('"');
                    }
                } else {
                    if (seconds != 0) {
                        sb.append(":00'").append(seconds).append('"');
                    } else {
                        sb.append("h");
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String getTimer(int duration) {
        StringBuilder sb = new StringBuilder();
        if (duration < 60) {
            sb.append("0:");
            if (duration < 10) {
                sb.append("0" + duration);
            } else {
                sb.append(duration);
            }
        } else {
            if (duration < (60 * 60)) {
                int minutes = duration / 60;
                sb.append(minutes).append(":");
                int seconds = duration % 60;
                if (seconds < 10) {
                    sb.append("0" + seconds);
                } else {
                    sb.append(seconds);
                }
            } else {
                int hours = duration / (60 * 60);
                int minutes = (duration % (60 * 60)) / 60;
                int seconds = duration % 60;
                sb.append(hours).append(":");
                if (minutes < 10) {
                    sb.append("0" + minutes).append(":");
                } else {
                    sb.append(minutes).append(":");
                }
                if (seconds < 10) {
                    sb.append("0" + seconds);
                } else {
                    sb.append(seconds);
                }
            }
        }
        return sb.toString();
    }
}