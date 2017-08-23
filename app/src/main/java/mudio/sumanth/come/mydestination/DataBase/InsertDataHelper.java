package mudio.sumanth.come.mydestination.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mudio.sumanth.come.mydestination.Common.Constant;
import mudio.sumanth.come.mydestination.Util;

import static mudio.sumanth.come.mydestination.Common.TabelName.ACTUAL_DISTANCE_IN_MILES;
import static mudio.sumanth.come.mydestination.Common.TabelName.DESTINATION_ADDRESS;
import static mudio.sumanth.come.mydestination.Common.TabelName.DESTINATION_LATITUDE;
import static mudio.sumanth.come.mydestination.Common.TabelName.DESTINATION_LONGITUDE;
import static mudio.sumanth.come.mydestination.Common.TabelName.DESTINATION_NAME;
import static mudio.sumanth.come.mydestination.Common.TabelName.FENCING_RADIUS_IN_METERS;
import static mudio.sumanth.come.mydestination.Common.TabelName.GEO_FENCE_REQUEST_ID;
import static mudio.sumanth.come.mydestination.Common.TabelName.REMAINING_DISTANCE_IN_MILES;
import static mudio.sumanth.come.mydestination.Common.TabelName.REMAINING_DISTANCE_TIME_STAMP;
import static mudio.sumanth.come.mydestination.Common.TabelName.SHORTNAME;
import static mudio.sumanth.come.mydestination.Common.TabelName.SOURCE_ADDRESS;
import static mudio.sumanth.come.mydestination.Common.TabelName.SOURCE_LATITUDE;
import static mudio.sumanth.come.mydestination.Common.TabelName.SOURCE_LONGITUDE;
import static mudio.sumanth.come.mydestination.Common.TabelName.TABLE_DESTINATION;
import static mudio.sumanth.come.mydestination.Common.TabelName.TRAVEL_STATUS;

/**
 * Created by sarith.vasu on 03-02-2017.
 */

public class InsertDataHelper extends DataBaseHelper {

    Context context;

    public InsertDataHelper(Context context) {
        super(context);
        this.context = context;
    }

    public void details(LatLng start, LatLng end, long distance, long duration, float radius, String geoFencigRequestId,String type) {
        // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
        Calendar cal = Calendar.getInstance();
        String currentDateTimeString = dateFormat.format(cal.getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SOURCE_LATITUDE, start.latitude);
            contentValues.put(SOURCE_LONGITUDE, start.longitude);
            contentValues.put(SOURCE_ADDRESS, getAddress(start.latitude, start.longitude));
            contentValues.put(DESTINATION_LATITUDE, end.latitude);
            contentValues.put(DESTINATION_LONGITUDE, end.longitude);
            contentValues.put(DESTINATION_ADDRESS, getAddress(end.latitude, end.longitude));
            contentValues.put(DESTINATION_NAME, getDesName(end.latitude, end.longitude));
            contentValues.put(ACTUAL_DISTANCE_IN_MILES, Util.convertMetersToMiles(distance));//
            contentValues.put(REMAINING_DISTANCE_IN_MILES, Util.convertMetersToMiles(distance));// distance Between 2 latlng
            contentValues.put(REMAINING_DISTANCE_TIME_STAMP, currentDateTimeString);
            contentValues.put(FENCING_RADIUS_IN_METERS, radius); // geofencing raduis
            contentValues.put(TRAVEL_STATUS, 1);// the user has completed tarvel or not
            contentValues.put(GEO_FENCE_REQUEST_ID, geoFencigRequestId);// geo fencing request id for remove fencing and remove from list
            contentValues.put(SHORTNAME,type);
            long i = db.insert(TABLE_DESTINATION, null, contentValues);
            if (i != 0) {
                System.out.print(true);
            } else {
                System.out.print(false);
            }
        }
    }

    public boolean updateStatus(String geoFencigRequestId) {
        // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
        Calendar cal = Calendar.getInstance();
        String currentDateTimeString = dateFormat.format(cal.getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(TRAVEL_STATUS, 0);// update user has completed tarvel.
            long i = db.update(TABLE_DESTINATION, contentValues, GEO_FENCE_REQUEST_ID + "=?", new String[]{geoFencigRequestId});
            if (i != 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void deleteByRequestId(String geoFencigRequestId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.delete(TABLE_DESTINATION, GEO_FENCE_REQUEST_ID + "=" + geoFencigRequestId, null);
        }
    }


    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getThoroughfare() != null)
                    result.append(address.getThoroughfare()).append(" ,");
                result.append(address.getSubLocality()).append(" ,");
                result.append(address.getLocality()).append(" ,");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private String getDesName(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append(" ,");
                result.append(address.getSubLocality());

            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    public void updateTrackingStatus() {

    }

    public int clearRecordInDb(String mGeoFencigRemoveId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int  value=0;
        if (db != null) {
            try{
                value = db.delete(TABLE_DESTINATION, GEO_FENCE_REQUEST_ID + "=?", new String[] { mGeoFencigRemoveId });
        }catch (Exception e){}
        }
        return value;
    }

}
