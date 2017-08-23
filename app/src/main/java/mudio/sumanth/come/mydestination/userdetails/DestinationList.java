package mudio.sumanth.come.mydestination.userdetails;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.Collections;
import java.util.List;

import mudio.sumanth.come.mydestination.Adapters.DestinationListAdapter;
import mudio.sumanth.come.mydestination.Common.AppPreferences;
import mudio.sumanth.come.mydestination.DataBase.DataBaseHelper;
import mudio.sumanth.come.mydestination.DataBase.InsertDataHelper;
import mudio.sumanth.come.mydestination.DataBase.SelectDataHelper;
import mudio.sumanth.come.mydestination.Interface.RecordReload;
import mudio.sumanth.come.mydestination.MapsActivity;
import mudio.sumanth.come.mydestination.R;
import mudio.sumanth.come.mydestination.model.destinationList;

public class DestinationList extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, RecordReload {
    private SwipeRefreshLayout swipeContainer;
    private ListView mListView;
    private ImageView mBack,mRefresh;
    private TextView mAddDestination;
    DataBaseHelper dataBaseHelper;
    SelectDataHelper selectDataHelper;
    InsertDataHelper insertDataHelper;
    DestinationListAdapter destinationListAdapter;
    private List<String> mGeofenceIdsToRemove;
    private GoogleApiClient googleApiClient;
    private AppPreferences appPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectDataHelper = new SelectDataHelper(this);
        appPreferences = new AppPreferences(this);
        createGoogleApi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }
        setContentView(R.layout.activity_destination_list);
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        getView();
    }

    private void getView() {
        insertDataHelper = new InsertDataHelper(this);
        mListView = (ListView) findViewById(R.id.lvItems);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mRefresh=(ImageView)findViewById(R.id.iv_refresh);
        mAddDestination=(TextView) findViewById(R.id.tv_addlocation);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationListAdapter = new DestinationListAdapter(DestinationList.this, R.layout.destinationlist_item, selectDataHelper.detailsShow());
                mListView.setAdapter(destinationListAdapter);
            }
        });
        mAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appPreferences.openActivity(DestinationList.this, MapsActivity.class);
            }
        });
        destinationListAdapter = new DestinationListAdapter(this, R.layout.destinationlist_item, selectDataHelper.detailsShow());
        mListView.setAdapter(destinationListAdapter);
        mListView.setOnItemLongClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String requestId = ((destinationList) adapterView.getAdapter().getItem(i)).getGeo_fence_request_id();
        try {
            if (insertDataHelper.clearRecordInDb(requestId) == 1) {
                Toast.makeText(getBaseContext(), "delete", Toast.LENGTH_LONG).show();
                if ( googleApiClient == null ) {
                    googleApiClient = new GoogleApiClient.Builder( this )
                            .addApi(Places.GEO_DATA_API)
                            .addApi( LocationServices.API )
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    LocationServices.GeofencingApi.removeGeofences(googleApiClient, Collections.singletonList(requestId));
                                }

                                @Override
                                public void onConnectionSuspended(int i) {

                                }
                            })
                            .build();
                    googleApiClient.connect();
                }

                ReloadAll();
            }
        } catch (Exception e) {

        }
        return false;
    }

    public void onUnregisterClicked(String requestId) {

        // Don't remove the geofence is Google Play is unavailabe,
        // or if there are none already registered
  /*

        mGeofenceIdsToRemove = Collections.singletonList(requestId);
        mCurrentGeofences.clear();
        mCurrentGeofence = null;

        // Start the request. Fail if there's already a request in progress
        try {
            mGeofenceRequester.removeGeofencesById(mGeofenceIdsToRemove);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            // Handle that previous request hasn't finished.
        }*/

    }

    @Override
    public void ReloadAll() {
        destinationListAdapter = new DestinationListAdapter(this, R.layout.destinationlist_item, selectDataHelper.detailsShow());
        mListView.setAdapter(destinationListAdapter);
        destinationListAdapter.notifyDataSetChanged();
    }
    private void createGoogleApi() {



    }
}
