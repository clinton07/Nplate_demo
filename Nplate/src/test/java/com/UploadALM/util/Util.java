package com.UploadALM.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
	
    private static String relativePath;

	/**
     * Function to get the absolute path of the framework (to be used as a relative path)
     * 
     * @return The absolute path of the framework
     */
    public static String getRelativePath() {

        if (relativePath.equals("") || relativePath == null)
            return new File(System.getProperty("user.dir")).getAbsolutePath();
        else
            return relativePath;

    }

    /**
     * Function to set the absolute path of the framework (to be used as a relative path)
     * 
     * @param relativePath
     *            The absolute path of the framework
     */
    public static void setRelativePath(String relativePath) {

        Util.relativePath = relativePath;
    }

    /**
     * Function to get the separator string to be used for directories and files based on the
     * current OS
     * 
     * @return The file separator string
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * Function to return the current time
     * 
     * @return The current time
     * @see #getCurrentFormattedTime(String)
     */
    public static Date getCurrentTime() {
        final Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * Function to return the current time, formatted as per the DateFormatString setting
     * 
     * @param dateFormatString
     *            The date format string to be applied
     * @return The current time, formatted as per the date format string specified
     * @see #getCurrentTime()
     * @see #getFormattedTime(Date, String)
     */
    public static String getCurrentFormattedTime(String dateFormatString) {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Function to format the given time variable as specified by the DateFormatString setting
     * 
     * @param time
     *            The date/time variable to be formatted
     * @param dateFormatString
     *            The date format string to be applied
     * @return The specified date/time, formatted as per the date format string specified
     * @see #getCurrentFormattedTime(String)
     */
    public static String getFormattedTime(Date time, String dateFormatString) {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        return dateFormat.format(time);
    }
}
