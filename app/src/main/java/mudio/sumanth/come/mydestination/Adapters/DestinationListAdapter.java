package mudio.sumanth.come.mydestination.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mudio.sumanth.come.mydestination.Common.Constant;
import mudio.sumanth.come.mydestination.R;
import mudio.sumanth.come.mydestination.Util;
import mudio.sumanth.come.mydestination.model.destinationList;
import mudio.sumanth.come.mydestination.userdetails.DestinationList;

/**
 * Created by sarith.vasu on 07-02-2017.
 */

public class DestinationListAdapter extends ArrayAdapter<destinationList> implements View.OnClickListener {
    private LayoutInflater inflater;
    private List<destinationList> addressDetails;
    public DestinationListAdapter(Context context, int resource, List<destinationList> objects) {
        super(context, resource, objects);
        addressDetails=objects;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ItemViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.destinationlist_item, null);
            holder = new ItemViewHolder();
            holder.mDestinationName = (TextView ) vi.findViewById(R.id.destination_name);
            holder.mDestinationAddress = (TextView ) vi.findViewById(R.id.destination_address);
            holder.mDistance = (TextView ) vi.findViewById(R.id.actual_distance);
            holder.mActualDistance = (TextView ) vi.findViewById(R.id.remaing_distance);
            holder.mTimeStamp = (TextView ) vi.findViewById(R.id.time);
            holder.mRefersh = (ImageView ) vi.findViewById(R.id.refersh_icon);

            vi.setTag( holder );
        }
        else
            holder = (ItemViewHolder) vi.getTag();

        if (addressDetails.size() <= 0) {
            holder.mDestinationName.setText("No Data");

        } else {
            /***** Get each Model object from Arraylist ********/

            destinationList value = (destinationList) addressDetails.get(position);

            /************  Set Model values in Holder elements ***********/
            holder.mDestinationName.setText(value.getShort_name());
            holder.mDestinationAddress.setText(value.getDestination_address());
            holder.mDistance.setText("Radius: "+value.getFencing_radius_in_meters()+", Distance:"+String.format( "%.2f", value.getActual_distance_in_miles() )+" Miles");
            //holder.mActualDistance.setText("Remaining Dis"+String.format( "%.2f", value.getRemaining_distance_in_miles() )+" Miles,");
            //holder.mTimeStamp.setText(""+Util.convertDateFormate(value.getRemaining_distance_time_stamp(), Constant.DATE_FORMAT,Constant.DATE_FORMAT_TIME));
            holder.mRefersh.setOnClickListener(this);


        }

        return vi;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(),"nthng to do",Toast.LENGTH_LONG).show();
    }

    static class ItemViewHolder {
        TextView mDestinationName;
        TextView mDestinationAddress;
        TextView mDistance;
        TextView mActualDistance;
        TextView mTimeStamp;
        ImageView mRefersh;
    }
    public void setListData(List<destinationList> data){
        addressDetails = data;
    }
}
