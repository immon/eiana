package org.iana.rzm.web.util;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    public static final String DEFAULT_PATTERN = "d MMM, yyyy";
    public static final Format DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_PATTERN);

    private static final Logger logger = Logger.getLogger(DateUtil.class.getName());

    public static String formatDate(Date d) {
        return formatDate(d, DEFAULT_PATTERN);
    }

    public static String formatDate(Date d, String pattern) {
        if (d == null) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(d);
    }


    public static Timestamp parseTimeStamp(String timestamp) {
        return new Timestamp(parseDate(timestamp, DEFAULT_PATTERN).getTime());
    }

    public static Timestamp parseTimeStamp(String timestamp, String pattern) {
        Date date = parseDate(timestamp, pattern);
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }


    private static Date parseDate(String date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(date);

        } catch (ParseException e) {
            logger.error("ParseException", e);
            return null;
        }
    }

}
