package mudio.sumanth.come.mydestination;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import mudio.sumanth.come.mydestination.Common.AppPreferences;
import mudio.sumanth.come.mydestination.DataBase.DataBaseHelper;
import mudio.sumanth.come.mydestination.DataBase.InsertDataHelper;
import mudio.sumanth.come.mydestination.Interface.GeofencingInterface;
import mudio.sumanth.come.mydestination.Interface.RecordReload;
import mudio.sumanth.come.mydestination.userdetails.DestinationList;

import static android.R.attr.duration;
import static android.R.attr.height;
import static android.R.attr.padding;
import static android.R.attr.start;
import static android.R.attr.width;



public  class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status>, View.OnClickListener, RoutingListener, GeofencingInterface {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String LOG_TAG = "MapsActivity";
    private GoogleMap map;
    private String mGeoFencigRemoveId;
    private String mGeoFencigRequestId;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private CardView mCardview;
    AutoCompleteTextView destination,source;
    ImageView send,moreItems;
    private TextView textLat, textLong,textRaduis;
    private Geofence mGeofence;
    GeofencingRequest mGeofenceRequest;
    double latitude,longitude;
    protected LatLng start;
    protected LatLng end;
    private PlaceAutoCompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    SupportMapFragment mapFragment;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};
    private static final LatLngBounds BOUNDS_JAMAICA = new LatLngBounds(new LatLng(23.344101, 85.309563),
            new LatLng(28.148735, 77.332024));
    private List<Polyline> polylines;
    public static LocationManager locationManager;
    private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
    private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";
    Geocoder geocoder;
    List<Address> yourAddresses;
    private InsertDataHelper insertDataHelper;
    private DataBaseHelper dataBaseHelper;
    private ImageView mBack;
    private int distance,duration;
    private ClearBroadCast mClearBroadCast;
    IntentFilter mClearIntentFilter;
    public static String userDestiantion;
    private AppPreferences mAppPreferences;
    private List<String> mGeofenceIdsToRemove;
    private EditText mAddDestinationName;

    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, MapsActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mAppPreferences= new AppPreferences(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }
        setContentView(R.layout.activity_main);
        dataBaseHelper=new DataBaseHelper(this);
        insertDataHelper=new InsertDataHelper(this);


       // geocoder = new Geocoder(this, Locale.getDefault());
        try {
            afterCheckingPremisson();
            // initialize GoogleMaps
        }catch (Exception e){

        }

    }



    private void afterCheckingPremisson() {
        polylines = new ArrayList<>();
        // Attaching the layout to the toolbar object
        initGMaps();

        // create GoogleApiClient
        createGoogleApi();
        Toolbar toolbar=(Toolbar)findViewById(R.id.tool_bar) ;
        setSupportActionBar(toolbar);
        textLat = (TextView) findViewById(R.id.lat);
        mCardview=(CardView)findViewById(R.id.cardview);
        mAddDestinationName=(EditText)findViewById(R.id.destination_name);
        textLong = (TextView) findViewById(R.id.lon);
        textRaduis= (TextView) findViewById(R.id.radius);
        textRaduis.setText("Raduis: "+mAppPreferences.getFenceRadius()+" mts");
        mBack=(ImageView)findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        source=(AutoCompleteTextView)findViewById(R.id.start);
        destination=(AutoCompleteTextView)findViewById(R.id.destination);
        send=(ImageView)findViewById(R.id.send);
        mCardview.setVisibility(View.GONE);

        send.setOnClickListener(this);

        mAdapter = new PlaceAutoCompleteAdapter(MapsActivity.this, android.R.layout.simple_list_item_1,
                googleApiClient, BOUNDS_JAMAICA, null);
        destination.setAdapter(mAdapter);
        source.setAdapter(mAdapter);
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int star, int before, int count) {


                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (end != null) {
                    end = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start = place.getLatLng();
                        longitude=place.getLatLng().longitude;
                        latitude=place.getLatLng().latitude;

                    }
                });

            }
        });
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        end = place.getLatLng();

                    }
                });

            }
        });
    }
/// this method is mainly used for geofenceing APi to entery Pont of Geopfenceign
    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addApi(Places.GEO_DATA_API)
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        validateConnections();
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
        mClearBroadCast =
                new ClearBroadCast();
        // Registers the DownloadStateReceiver and its intent filters
        mClearIntentFilter = new IntentFilter(
                GeofenceTrasitionService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mClearBroadCast,mClearIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
    /*   googleApiClient.disconnect();*/
        if(mClearBroadCast!=null)
            try {
                unregisterReceiver(mClearBroadCast);
            }catch (Exception e){
                System.out.println(e);
            }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.geofence: {
                try {
                    startGeofence();
                }catch (Exception e){
                }
                return true;
            }
            case R.id.clear: {
                clearGeofence();
                return true;
            }
            case R.id.refersh:
                map.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final int REQ_PERMISSION = 999;

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
        // TODO close app and warn user
    }

    // Initialize GoogleMaps
    private void initGMaps(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }

    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
      //  mCardview.setVisibility(View.VISIBLE);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
      //  markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  10000;
    private final int FASTEST_INTERVAL = 9000;

    // Start location Updates
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
      //  recoverGeofenceMarker();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                longitude=lastLocation.getLongitude();
                latitude=lastLocation.getLatitude();
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    private void writeActualLocation(Location location) {
        textLat.setText( "Lat: " + location.getLatitude() );
        textLong.setText( "Long: " + location.getLongitude() );
            latitude=location.getLatitude();
        longitude=location.getLongitude();
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private Marker locationMarker;
    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(Title(latLng));
        if ( map!=null ) {
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            float zoom = 10f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
            mCardview.setVisibility(View.VISIBLE);
        }
    }


    private Marker geoFenceMarker;
    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(Title(latLng));
        if ( map!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);

        }
    }
    public String  Title(LatLng latLng){
    String  title = "source";
      /*  try {
            yourAddresses= geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (yourAddresses.size() > 0)
            {
                String yourAddress = yourAddresses.get(0).getAddressLine(0);
                String yourCity = yourAddresses.get(0).getAddressLine(1);
                String yourCountry = yourAddresses.get(0).getAddressLine(2);
                title=yourAddress+","+yourCity+","+yourCountry;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
     return  title;
    }

    // Start Geofence creation process
    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if( geoFenceMarker != null ) {
            mGeofence = createGeofence( geoFenceMarker.getPosition(), mAppPreferences.getFenceRadius() );
            mGeofenceRequest = createGeofenceRequest( mGeofence );
            addGeofence( mGeofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    private static final long GEO_DURATION = 60 * 60 * 10000; //this i am setting Expire time and NEVER_EXPIRE  is used to make Geofence to never Experie
  //  private static final String GEOFENCE_REQ_ID = "Sumanth";
  //  private static float GEOFENCE_RADIUS = ; // in meters//set the radius for the Geofence

    // Create a Geofence
    private Geofence createGeofence( LatLng latLng, float radius ) {
        Log.d(TAG, "createGeofence");
        mGeoFencigRequestId=System.currentTimeMillis()+"";
        return new Geofence.Builder()
                .setRequestId(mGeoFencigRequestId)//this will identify the geofence inside the application
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)//syntax: public Geofence.Builder setCircularRegion (double latitude, double longitude, float radius) The geofence represents a circular area on a flat, horizontal plane.
               //latitude in degrees, between -90 and +90 inclusive,	longitude in degrees, between -180 and +180 inclusive,radius in meters
                .setExpirationDuration( GEO_DURATION )  //this will remove the Geofence AutoMatically after that peroid
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )// sets the transition type of alert in andorid
                .build();// creates Geofence Object
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");

        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();

        //setNotificationReponsevie is used to send alert to the user after certain time and defualt is 0
//        setLoiteringDelay the delay between GEOFENCE_TRANSITION_ENTER and GEOFENCE_TRANSITION_DWELLING in milliseconds. For example, if loitering delay is set to 300000 ms (i.e. 5 minutes) the geofence service will send a GEOFENCE_TRANSITION_DWELL alert roughly 5 minutes after user enters a geofence if the user stays inside the geofence during this period of time. If the user exits from the geofence in this amount of time, GEOFENCE_TRANSITION_DWELL alert won't be sent.
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                getApplicationContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            saveGeofence();
            drawGeofence();
        } else {
            // inform about fail
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( mAppPreferences.getFenceRadius() );
        geoFenceLimits = map.addCircle( circleOptions );
    }



    // Saving GeoFence marker with prefs mng
    private void saveGeofence() {
        Log.d(TAG, "saveGeofence()");
        SharedPreferences sharedPref = getPreferences( Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong( KEY_GEOFENCE_LAT, Double.doubleToRawLongBits( geoFenceMarker.getPosition().latitude ));
        editor.putLong( KEY_GEOFENCE_LON, Double.doubleToRawLongBits( geoFenceMarker.getPosition().longitude ));
        editor.apply();
    }

    // Recovering last Geofence marker
    private void recoverGeofenceMarker() {
        Log.d(TAG, "recoverGeofenceMarker");
        SharedPreferences sharedPref = getPreferences( Context.MODE_PRIVATE );

        if ( sharedPref.contains( KEY_GEOFENCE_LAT ) && sharedPref.contains( KEY_GEOFENCE_LON )) {
            map.clear();
            double lat = Double.longBitsToDouble( sharedPref.getLong( KEY_GEOFENCE_LAT, -1 ));
            double lon = Double.longBitsToDouble( sharedPref.getLong( KEY_GEOFENCE_LON, -1 ));
            LatLng latLng = new LatLng( lat, lon );
            end=latLng;
            markerForGeofence(latLng);
            drawGeofence();
            try {
                progressDialog = ProgressDialog.show(this, "Please wait.",
                        "Fetching route information.", true);
                start = new LatLng(latitude, longitude);
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(start, end)
                        .build();
                routing.execute();
            }catch (Exception e){

            }
        }
    }

    // Clear Geofence
    private void clearGeofence() {
        Log.d(TAG, "clearGeofence()");

        /*LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                createGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if ( status.isSuccess() ) {
                    // remove drawing
                    removeGeofenceDraw();
                    Toast.makeText(MapsActivity.this,"fencing removed",Toast.LENGTH_SHORT).show();
                }
            }
        });*/


       // mGeofenceIdsToRemove = Collections.singletonList(mGeoFencigRemoveId);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, Collections.singletonList(mGeoFencigRemoveId));

        // Start the request. Fail if there's already a request in progress
        try {
         //   LocationServices.GeofencingApi.removeGeofences(googleApiClient,mGeofenceIdsToRemove);
            removeGeofenceDraw();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            // Handle that previous request hasn't finished.
        }

    }

    private void removeGeofenceDraw() {
        RecordReload recordReload = null;
        Log.d(TAG, "removeGeofenceDraw()");
        if ( geoFenceMarker != null)
            geoFenceMarker.remove();
        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        Toast.makeText(this,"Remove from list",Toast.LENGTH_SHORT).show();
        boolean upddated=insertDataHelper.updateStatus(mGeoFencigRemoveId);
       /* try {
            if (insertDataHelper.clearRecordInDb(mGeoFencigRemoveId) == 1) {
                Toast.makeText(getBaseContext(), "delete", Toast.LENGTH_LONG).show();
                recordReload.ReloadAll();
            }
        }catch (Exception e){

        }*/
    }

    @Override
    public void onClick(View view) {
        String addDestinationName=mAddDestinationName.getText().toString().trim();
        if(view.getId() == R.id.send) {
            if (Util.Operations.isOnline(this)) {




                if (start==null || end == null || addDestinationName.length() < 2) {
                  if(start !=null ) {
                if(source.getText().length()>0)
                {
                    source.setError("Choose location from dropdown.");

                }
                else
                {
                    Toast.makeText(this,"Please choose a starting point.",Toast.LENGTH_SHORT).show();
                }
            } else {
                      start = new LatLng(latitude, longitude);
                  }
                    if (end == null) {
                        if (destination.getText().length() > 0) {
                            destination.setError("Choose location from dropdown.");
                        } else {
                            Toast.makeText(this, "Please choose a destination.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(addDestinationName.length() < 2){
                        mAddDestinationName.setError("Choose Add Destination");

                    }

                } else {

                   // showChangeLangDialog(this);
                    saveDestinationDetails(addDestinationName);
                }

            } else {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //addding items to the toolbarr to stop the service in the android

    public void route() {
        buttonClick(true);
        if (/*start==null ||*/ end == null) {
         /*   if(start==null)
            {
                if(starting.getText().length()>0)
                {
                    starting.setError("Choose location from dropdown.");
                }
                else
                {
                    Toast.makeText(this,"Please choose a starting point.",Toast.LENGTH_SHORT).show();
                }
            }*/
            if (end == null) {
                if (destination.getText().length() > 0) {
                    destination.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this, "Please choose a destination.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
               userDestiantion =destination.getText().toString();
            map.clear();
            progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
            if(start ==null) {
                start = new LatLng(latitude, longitude);
            }
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();


        }
    }
    //tryinhg New Concpet of Zoom
    private void addMarkers() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        //add the marker locations that you'd like to display on the map
        boundsBuilder.include(start);
        boundsBuilder.include(end);

        final LatLngBounds bounds = boundsBuilder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        map.animateCamera(cameraUpdate);
    }



    @Override
    public void onRoutingFailure(RouteException e) {
        progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            buttonClick(false);

        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
            buttonClick(false);

        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
        progressDialog.dismiss();
        int padding = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(end);
        LatLngBounds bounds = builder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
        /*    LatLngBounds bounds = new LatLngBounds(start,
                end);// offset from edges of the map in pixels


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
*/
     /*   CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);*/
  /*      map.moveCamera(cameraUpdate);
        map.animateCamera(cameraUpdate);*/
       /* map.moveCamera(center);
        map.animateCamera(zoom);*/

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 5);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
            userDestiantion=route.get(i).getEndAddressText();
            distance=route.get(i).getDistanceValue();
            duration=route.get(i).getDurationValue();
            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();


        }
        //showChangeLangDialog(this);

        // Start marker
/*        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.title("your Location");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        map.addMarker(options);*/

     /*   options = new MarkerOptions();
        options.position(end);
        options.title("destination");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        map.addMarker(options);*/
    }

    @Override
    public void onRoutingCancelled() {

            Log.i(LOG_TAG, "Routing was cancelled.");
        buttonClick(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private void validateConnections() {
        if (!Util.Operations.isOnline(this)) {
            createNetErrorDialog();
        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        } else {
            try {
                afterCheckingPremisson();
            } catch (Exception e) {

            }
        }
    }

    protected void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_connection)
                .setTitle(R.string.titleMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.settings,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton(R.string.button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                System.exit(0);
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void showGPSDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.messahe)
                .setCancelable(false)
                .setPositiveButton(R.string.GpsButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    @Override
    public void clear(boolean values) {
        if(values){
            clearGeofence();

        }
    }
    private class ClearBroadCast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(GeofenceTrasitionService.BROADCAST_ACTION)) {
               mGeoFencigRemoveId= intent.getStringExtra(GeofenceTrasitionService.GEOFENCE_REQUEST_ID);
                clearGeofence();

            }
        }
    }


    public void saveDestinationDetails(String userName)
    {
        markerForGeofence(start);
        markerForGeofence(end);
        startGeofence();
        buttonClick(false);
        distance=(int)Util.distance(start,end);
        insertDataHelper.details(start,end,distance,duration ,mAppPreferences.getFenceRadius(),mGeoFencigRequestId,userName);
        Log.e("RequestID",mGeoFencigRequestId);
        Toast.makeText(getBaseContext(),mGeoFencigRequestId,Toast.LENGTH_LONG).show();;
        Intent intent = new Intent(this,DestinationList.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void buttonClick(Boolean values){
        send.setEnabled(values);
    }


    public  void showChangeLangDialog(final Context  context) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("MyDestination");
        dialogBuilder.setMessage("Enter  Name below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                progressDialog = ProgressDialog.show(MapsActivity.this, "Please wait.",
                        "Fetching route information.", true);
                String userName = edt.getText().toString();
                markerForGeofence(start);
                markerForGeofence(end);
                startGeofence();
                buttonClick(false);
                distance=(int)Util.distance(start,end);
                insertDataHelper.details(start,end,distance,duration ,mAppPreferences.getFenceRadius(),mGeoFencigRequestId,userName);
                Log.e("RequestID",mGeoFencigRequestId);
                Toast.makeText(getBaseContext(),mGeoFencigRequestId,Toast.LENGTH_LONG).show();
                progressDialog .dismiss();
                AppPreferences appPreferences=new AppPreferences(context);
                Intent intent = new Intent(context,DestinationList.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
               // appPreferences.openActivity(context, DestinationList.class);

            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.show();
        ((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    // Disable ok button
                    ((AlertDialog) b).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    // Something into edit text. Enable the button.
                    ((AlertDialog) b).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });


    }

}
