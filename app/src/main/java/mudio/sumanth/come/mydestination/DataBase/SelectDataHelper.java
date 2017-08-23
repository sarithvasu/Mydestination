package mudio.sumanth.come.mydestination.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mudio.sumanth.come.mydestination.model.destinationList;

import static mudio.sumanth.come.mydestination.Common.TabelName.ACTUAL_DISTANCE_IN_MILES;
import static mudio.sumanth.come.mydestination.Common.TabelName.DESTINATION_ADDRESS;
import static mudio.sumanth.come.mydestination.Common.TabelName.DESTINATION_NAME;
import static mudio.sumanth.come.mydestination.Common.TabelName.FENCING_RADIUS_IN_METERS;
import static mudio.sumanth.come.mydestination.Common.TabelName.GEO_FENCE_REQUEST_ID;
import static mudio.sumanth.come.mydestination.Common.TabelName.REMAINING_DISTANCE_IN_MILES;
import static mudio.sumanth.come.mydestination.Common.TabelName.REMAINING_DISTANCE_TIME_STAMP;
import static mudio.sumanth.come.mydestination.Common.TabelName.SHORTNAME;
import static mudio.sumanth.come.mydestination.Common.TabelName.TABLE_DESTINATION;
import static mudio.sumanth.come.mydestination.Common.TabelName.TRAVEL_STATUS;

/**
 * Created by sarith.vasu on 03-02-2017.
 */

public class SelectDataHelper extends DataBaseHelper {
    public SelectDataHelper(Context context) {
        super(context);
    }
    public ArrayList<destinationList> detailsShow(){
        ArrayList<destinationList> destinationLists=new ArrayList<destinationList>();
        SQLiteDatabase db=this.getWritableDatabase();
        String query = "select * from "+TABLE_DESTINATION + " where "+TRAVEL_STATUS+"=1  ORDER BY " + ACTUAL_DISTANCE_IN_MILES + " ASC";
      Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() >= 1) {
                if (cursor.moveToFirst()) {
                    do {
                        destinationList locationSummery = new destinationList();
                        locationSummery.setDestination_name(cursor.getString(cursor.getColumnIndex(DESTINATION_NAME)));
                        locationSummery.setDestination_address(cursor.getString(cursor.getColumnIndex(DESTINATION_ADDRESS)));
                        locationSummery.setActual_distance_in_miles(cursor.getDouble(cursor.getColumnIndex(ACTUAL_DISTANCE_IN_MILES)));
                        locationSummery.setRemaining_distance_in_miles(cursor.getDouble(cursor.getColumnIndex(REMAINING_DISTANCE_IN_MILES)));
                        locationSummery.setRemaining_distance_time_stamp(cursor.getString(cursor.getColumnIndex(REMAINING_DISTANCE_TIME_STAMP)));
                        locationSummery.setFencing_radius_in_meters(cursor.getInt(cursor.getColumnIndex(FENCING_RADIUS_IN_METERS)));
                        locationSummery.setGeo_fence_request_id(cursor.getString(cursor.getColumnIndex(GEO_FENCE_REQUEST_ID)));
                        locationSummery.setShort_name(cursor.getString(cursor.getColumnIndex(SHORTNAME)));
                        destinationLists.add(locationSummery);

                    } while (cursor.moveToNext());
                }

            }
        }
        return destinationLists;
    }
    public String getDestinationByRequestId(String requestId){
        SQLiteDatabase db=this.getWritableDatabase();
        String name = null;
        String query = "select * from "+TABLE_DESTINATION + " where "+GEO_FENCE_REQUEST_ID+"= '"+requestId+ "' " ;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() >= 1) {
                if (cursor.moveToFirst()) {
                    do {
                     name=  cursor.getString(cursor.getColumnIndex(SHORTNAME));

                    } while (cursor.moveToNext());
                }

            }
        }
        return name;
    }
}
