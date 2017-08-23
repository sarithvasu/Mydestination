package mudio.sumanth.come.mydestination.Common;

/**
 * Created by sarith.vasu on 03-02-2017.
 */

public class TabelName {
    public final static String DATA_BASE_NAME = "My_Destination";
    public final static String TABLE_DESTINATION="destination";
    public final static String CURRENT="currentlocation";
    public final static String SOURCE_LATITUDE="source_latitude";
    public final static String SOURCE_LONGITUDE="source_longitude";
    public final static String SOURCE_ADDRESS="source_address";

    public final static String DESTINATION_LATITUDE="destination_latitude";
    public final static String DESTINATION_LONGITUDE="destination_longitude";
    public final static String DESTINATION_ADDRESS="destination_address";

    public final static String DESTINATION_NAME="destination_name";

    public final static String ACTUAL_DISTANCE_IN_MILES="actual_distance_in_miles";
    public final static String REMAINING_DISTANCE_IN_MILES ="remaining_distance_in_miles";
    public final static String REMAINING_DISTANCE_TIME_STAMP="remaining_distance_time_stamp";

    public final static String FENCING_RADIUS_IN_METERS="fencing_radius_in_meters";
    public final static String TRAVEL_STATUS="travel_status";
    public final static String GEO_FENCE_REQUEST_ID="geo_fence_request_id";
    public final static  String SHORTNAME="short_name";

    public final static String DESTINATION="destiantonlocation";

    public static final String CREATE_TABLE_TRAVEL="CREATE TABLE `destination` (`source_latitude` REAL NOT NULL,`source_longitude` REAL NOT NULL,`source_address` TEXT NOT NULL,`destination_latitude` REAL NOT NULL,`destination_longitude` REAL NOT NULL,`destination_address` TEXT NOT NULL,`destination_name` TEXT NOT NULL,`actual_distance_in_miles` INTEGER NOT NULL,`remaining_distance_in_miles` INTEGER,`remaining_distance_time_stamp` TEXT,`fencing_radius_in_meters` INTEGER NOT NULL,`travel_status` INTEGER NOT NULL,`destination_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,`geo_fence_request_id` INTEGER NOT NULL,'short_name' VARCHAR);";


}
