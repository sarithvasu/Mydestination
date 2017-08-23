package mudio.sumanth.come.mydestination;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mudio.sumanth.come.mydestination.Common.Constant;

/**
 * Created by sarith.vasu on 20-01-2017.
 */

public final class Util {
    public static final class Operations {
        private Operations() throws InstantiationException {
            throw new InstantiationException("This class is not for instantiation");
        }
        /**
         * Checks to see if the device is online before carrying out any operations.
         *
         * @return
         */
        public static boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }
    }
    private Util() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
    public static void  voiceAlert(TextToSpeech tts,String msg) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }
    public static int getDelayInMinutes(String date_String)
    {


        SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String currentDateTimeString =dateFormat.format(cal.getTime());
        Date Created_convertedDate=null,Expire_CovertedDate=null,todayWithZeroTime=null;
        try
        {
            if(!currentDateTimeString.equals("")&&!date_String.equals("")) {
                Created_convertedDate = dateFormat.parse(currentDateTimeString);
                Expire_CovertedDate = dateFormat.parse(date_String);

                Date today = new Date();

                todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
            }
            else{
                return 0;
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
            return 0;
        }


        int c_year=0,c_month=0,c_day=0;

        if(Created_convertedDate.after(todayWithZeroTime))
        {
            Calendar c_cal = Calendar.getInstance();
            c_cal.setTime(Created_convertedDate);

            c_year = c_cal.get(Calendar.YEAR);
            c_month = c_cal.get(Calendar.MONTH);
            c_day = c_cal.get(Calendar.DAY_OF_MONTH);

        }
        else
        {
            Calendar c_cal = Calendar.getInstance();
            c_cal.setTime(todayWithZeroTime);

            c_year = c_cal.get(Calendar.YEAR);
            c_month = c_cal.get(Calendar.MONTH);
            c_day = c_cal.get(Calendar.DAY_OF_MONTH);
        }
        Calendar e_cal = Calendar.getInstance();
        e_cal.setTime(Expire_CovertedDate);

        int e_year = e_cal.get(Calendar.YEAR);
        int e_month = e_cal.get(Calendar.MONTH);
        int e_day = e_cal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(c_year, c_month, c_day);
        date2.clear();
        date2.set(e_year, e_month, e_day);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float minutes = (float) diff / (60 * 1000);


        return (int) minutes;
    }
    public static double convertMetersToMiles(double meters)
    {
        return (meters / 1609.344);
    }
    public static String convertDateFormate(String dateInString,String fromDateFormat,String toDateFormate) {
        String result = "";
        try {
            DateFormat df = new SimpleDateFormat(fromDateFormat);
            Date startDate = df.parse(dateInString);
            DateFormat df3 = new SimpleDateFormat(toDateFormate);
            result = df3.format(startDate);

        } catch (ParseException e) {
        }
        return result;
    }
    public static void createOKAlert(Context context,String title,String msg ,DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        if(!title.equals("")) {
            builder.setTitle(title);

        }
        builder.setMessage(msg)
                .setCancelable(false)
                .setNeutralButton("OK", listener);

        AlertDialog alert = builder.show();
        alert.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView messageText = (TextView)alert.findViewById(android.R.id.message);
        int titleId =context.getResources().getIdentifier("alertTitle", "id", "android");
        TextView titleText = (TextView)alert.findViewById(titleId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            titleText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }
        titleText.setGravity(Gravity.CENTER);
        messageText.setGravity(Gravity.CENTER);
        alert.show();
    }

    ///this method is used top calclulate distance b/w 2 points
    public static double distance(LatLng start,LatLng end) {

        double lat1=start.latitude; double lng1=start.longitude; double lat2=end.latitude; double lng2=end.longitude;
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }
}
