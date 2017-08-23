package mudio.sumanth.come.mydestination.userdetails;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import mudio.sumanth.come.mydestination.Common.AppPreferences;
import mudio.sumanth.come.mydestination.R;

public class Settings extends AppCompatActivity {

    private ImageView mBack;
    private SeekBar mSeekBar;
    private TextView mTvProress;
    private AppPreferences mAppPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }
        setContentView(R.layout.activity_settings);
        mAppPreferences=new AppPreferences(this);
        getView();
    }

    private void getView() {

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setProgress((int)mAppPreferences.getFenceRadius());

        mBack = (ImageView) findViewById(R.id.iv_back);
        mTvProress=(TextView)findViewById(R.id.progress);
        mTvProress.setText(""+(int)mAppPreferences.getFenceRadius()+"m");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTvProress.setText(""+i);
                if(i<100){
                    mSeekBar.setProgress(100);
                }else {
                    mAppPreferences.setFenceRadius((float) i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


    }
}
