package mudio.sumanth.come.mydestination.model;

/**
 * Created by sarith.vasu on 03-02-2017.
 */

public class destinationList {


    private String destination_address;
    private String destination_name;
    private String geo_fence_request_id;
    private Double actual_distance_in_miles;
    private Double remaining_distance_in_miles;
    private String remaining_distance_time_stamp;
    private int fencing_radius_in_meters;
    private String short_name;

    public int getFencing_radius_in_meters() {
        return fencing_radius_in_meters;
    }

    public void setFencing_radius_in_meters(int fencing_radius_in_meters) {
        this.fencing_radius_in_meters = fencing_radius_in_meters;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getGeo_fence_request_id() {
        return geo_fence_request_id;
    }

    public void setGeo_fence_request_id(String geo_fence_request_id) {
        this.geo_fence_request_id = geo_fence_request_id;
    }


    public String getDestination_address() {
        return destination_address;
    }

    public void setDestination_address(String destination_address) {
        this.destination_address = destination_address;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public void setDestination_name(String destination_name) {
        this.destination_name = destination_name;
    }

    public Double getActual_distance_in_miles() {
        return actual_distance_in_miles;
    }

    public void setActual_distance_in_miles(Double actual_distance_in_miles) {
        this.actual_distance_in_miles = actual_distance_in_miles;
    }

    public Double getRemaining_distance_in_miles() {
        return remaining_distance_in_miles;
    }

    public void setRemaining_distance_in_miles(Double remaining_distance_in_miles) {
        this.remaining_distance_in_miles = remaining_distance_in_miles;
    }

    public String getRemaining_distance_time_stamp() {
        return remaining_distance_time_stamp;
    }

    public void setRemaining_distance_time_stamp(String remaining_distance_time_stamp) {
        this.remaining_distance_time_stamp = remaining_distance_time_stamp;
    }
}
