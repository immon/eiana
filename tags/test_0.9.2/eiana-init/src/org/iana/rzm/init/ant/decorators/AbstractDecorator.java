package org.iana.rzm.init.ant.decorators;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Piotr Tkaczyk
 */
abstract class AbstractDecorator {

    static final String DATE_FORMAT = "yyyy-MM-dd";
    static final String TIME_ZONE = "GMT";

    Timestamp getFormatedDate(String value) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = sdf.parse(value);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.setTime(date);
        return new Timestamp(calendar.getTimeInMillis());
    }
}
