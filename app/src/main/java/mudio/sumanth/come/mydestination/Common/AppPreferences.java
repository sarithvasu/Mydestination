package mudio.sumanth.come.mydestination.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by sarith.vasu on 03-02-2017.
 */

public class AppPreferences {
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String APP_SHARED_PREFS	= AppPreferences.class.getSimpleName();
    private String userName="name";
    private static final String FENCINGRADIUS="fencingradius";


    public AppPreferences(Context context) {
        this.sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = sharedPrefs.edit();
    }
    public String getUserName()
    {
        return sharedPrefs.getString(userName, "");
    }
    public void setUserName(String name){
        prefsEditor.putString(userName, name);
        prefsEditor.commit();
    }
    public float getFenceRadius()
    {
        return sharedPrefs.getFloat(FENCINGRADIUS, 100.0f);
    }
    public void setFenceRadius(float name){
        prefsEditor.putFloat(FENCINGRADIUS, name);
        prefsEditor.commit();
    }

    public void openActivity(Context context,Class<?> calledActivity) {
        Intent myIntent = new Intent(context, calledActivity);
        context.startActivity(myIntent);
    }
}
