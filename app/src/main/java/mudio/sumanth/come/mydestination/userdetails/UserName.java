package mudio.sumanth.come.mydestination.userdetails;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;

import mudio.sumanth.come.mydestination.Common.AppPreferences;
import mudio.sumanth.come.mydestination.DataBase.DataBaseHelper;
import mudio.sumanth.come.mydestination.MapsActivity;
import mudio.sumanth.come.mydestination.R;

/**
 * Created by sarith.vasu on 02-02-2017.
 */

public class UserName extends Activity implements View.OnClickListener {
    private AppPreferences appPreferences;
    private TextView mLocation, mDestinationList, mSettings;
    private DataBaseHelper dataBaseHelper;
    private String TAG="USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = new AppPreferences(this);
        dataBaseHelper=new DataBaseHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }
        setContentView(R.layout.menuitem);


        if (appPreferences.getUserName() == "") {
            showChangeLangDialog();
        }
        getView();
    }

    public  void showChangeLangDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("MyDestination");
        dialogBuilder.setMessage("Enter your Name below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String userName = edt.getText().toString();
                appPreferences.setUserName(userName);

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

    private void getView() {
        mLocation = (TextView) findViewById(R.id.tv_addlocation);
        mDestinationList = (TextView) findViewById(R.id.tv_destinationlist);
        mSettings = (TextView) findViewById(R.id.tv_settings);
        mLocation.setOnClickListener(this);
        mDestinationList.setOnClickListener(this);
        mSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_addlocation:
                appPreferences.openActivity(this, MapsActivity.class);
                break;
            case R.id.tv_destinationlist:
                appPreferences.openActivity(this, DestinationList.class);
                break;
            case R.id.tv_settings:
                appPreferences.openActivity(this, Settings.class);
                break;
            default:

        }
    }



}
