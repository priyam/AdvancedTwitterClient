package com.pc.apps.simpletweets.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getCreatedAtPrettyTime(String dateTime) {
        String TwitterDateFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TwitterDateFormat, Locale.US);
        sf.setLenient(true);
        try {
            Date now = new Date();
            Date date = sf.parse(dateTime);
            PrettyTime p = new PrettyTime();
            String sDate = p.format(date);
            long diffInSeconds = (now.getTime() - date.getTime())/1000;

            return sDate.replace("moments ago", (diffInSeconds < 60? diffInSeconds + "s" : diffInSeconds/60 + "m")).
                    replace(" minute ago", "m").
                    replace(" minutes ago", "m").
                    replace(" hour ago", "h").
                    replace(" hours ago", "h").
                    replace(" day ago", "d").
                    replace(" days ago", "d").
                    replace("week ago", "w").
                    replace("weeks ago", "w").
                    replace("month ago", "mo").
                    replace("months ago", "mo").
                    replace("year ago", "y").
                    replace("years ago", "y").
                    replace(" moments from now", "0s")
                    ;

        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
