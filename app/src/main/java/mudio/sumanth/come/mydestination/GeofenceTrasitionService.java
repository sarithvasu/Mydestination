package mudio.sumanth.come.mydestination;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mudio.sumanth.come.mydestination.Common.AppPreferences;
import mudio.sumanth.come.mydestination.DataBase.InsertDataHelper;
import mudio.sumanth.come.mydestination.DataBase.SelectDataHelper;
import mudio.sumanth.come.mydestination.Interface.GeofencingInterface;


public class GeofenceTrasitionService extends IntentService implements TextToSpeech.OnInitListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();

    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    public static final String GEOFENCE_REQUEST_ID = "geofence_request_id";
    public static final String BROADCAST_ACTION = "mudio.sumanth.come.mydestination.BROADCAST";
    private GoogleApiClient googleApiClient;
    private InsertDataHelper insertDataHelper;

    public GeofenceTrasitionService() {
        super(TAG);
    }

    private TextToSpeech tts;
    String geofenceTransitionDetails;
    private String geofenceRequestId;
    private String mEntryStatus;
    GeofencingInterface geofencingInterface;

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors

        insertDataHelper = new InsertDataHelper(getApplicationContext());
        if (geofencingEvent.hasError()) {

            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMsg);
            return;
        }
        //Toast.makeText(getApplicationContext(),"In Service",Toast.LENGTH_SHORT).show();
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofence that were triggered
       /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sendNotification("entering");
            }*/
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            geofenceRequestId = triggeringGeofences.get(0).getRequestId();
            geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);

            // Send notification details as a String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sendNotification(geofenceTransitionDetails);
            }
        }
    }


    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {

        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
        }

        mEntryStatus = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            mEntryStatus = "Entering ";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            mEntryStatus = "Exiting ";
        return mEntryStatus;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String msg) {
        Log.i(TAG, "sendNotification: " + msg);
        // Intent to start the main Activity
        Intent notificationIntent = MapsActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        try {
            tts = new TextToSpeech(getApplicationContext(), this);
        } catch (Exception e) {
        }
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
        if (msg.equals("Exiting ")) {
           /* Intent intent = new Intent(this, GeofenceTrasitionService.class);
            stopService(intent);*/

             /* if(!googleApiClient.isConnected()){
                  googleApiClient.connect();
              }*/


            Intent localIntent =
                    new Intent(BROADCAST_ACTION);
            localIntent.putExtra(GEOFENCE_REQUEST_ID, geofenceRequestId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            // geofencingInterface.clear(true);
        }

        try {
            if (msg.equals("Exiting ")) {
                speakOut(createVOiceText(geofenceRequestId, R.string.voice_existing));
            } else {
                speakOut(createVOiceText(geofenceRequestId, R.string.voice_entering));
            }
        } catch (Exception e) {
        }

    }


    public String createVOiceText(String requestId, int voice_entering) {
        String finalMsg = null;
        if (requestId != null) {

            SelectDataHelper selectDataHelper = new SelectDataHelper(getBaseContext());
            AppPreferences appPreferences = new AppPreferences(getBaseContext());
            String requsetName = null;

            String userName = appPreferences.getUserName() + " " + getString(voice_entering);
            try {
                requsetName = selectDataHelper.getDestinationByRequestId(requestId);
            } catch (Exception e) {

            }
            if (requsetName != null) {
                finalMsg = userName + " " + requsetName;
            } else {
                finalMsg = "Some thing wrong happened";
            }
        } else {
            finalMsg = "Some thing wrong happened";
        }

        return finalMsg;
    }


    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        AppPreferences appPreferences = new AppPreferences(getBaseContext());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        int name;
        if (msg.equals("Exiting ")) {
            name = R.string.voice_existing;
        } else {
            name = R.string.voice_entering;
        }
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText(getString(name) + " " + appPreferences.getUserName())
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";/// Geofence Services are not avial;bel
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";// we can havae 100 feofences for one app
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";//You have provided more than 5 different PendingIntents to the addGeofences(GoogleApiClient, GeofencingRequest, PendingIntent) call.
            default:
                return "Unknown error.";
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (tts != null) {
                int result = tts.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    if (mEntryStatus != null) {
                        if (mEntryStatus.equals("Exiting ")) {
                            speakOut(createVOiceText(geofenceRequestId, R.string.voice_existing));
                        } else {
                            speakOut(createVOiceText(geofenceRequestId, R.string.voice_entering));
                        }

                    }


                }
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String textmsg) {


        tts.speak(textmsg, TextToSpeech.QUEUE_FLUSH, null);
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
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

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
