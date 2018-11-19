package cn.dface.app.message.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author akun
 * @create 2018-10-23 下午5:13
 **/
public class Dateutils {

    /** 日期+时间的格式 */
    final static public String   DATE_TIME_FORMAT       = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将日期类解析成"yyyy-MM-dd HH:mm:ss"格式的日期字符串
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        if (null == date) {
            return null;
        }
        return formatDateTime(date, DATE_TIME_FORMAT);
    }

    /**
     * 将日期类解析成指定格式的日期字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDateTime(Date date, String format) {
        if (date == null){
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
