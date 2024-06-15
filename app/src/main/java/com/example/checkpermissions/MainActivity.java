package com.example.checkpermissions;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button checkPermissionsButton = findViewById(R.id.BTN_login);
        checkPermissionsButton.setOnClickListener(v -> {


            if (checkPermissions()) {
                Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public boolean checkPermissions() {
        boolean isAllPermissionsGranted = true;
        String permissions = "";
        if (isDarkMode()) {
            isAllPermissionsGranted = false;
            permissions += ", Dark Mode";
        }
        if (!isBrightnessAutomatic()) {
            isAllPermissionsGranted = false;
            permissions += ", Brightness Automatic";
        }
        if (isMuted()) {
            isAllPermissionsGranted = false;
            permissions += ", Muted";
        }
        if (!isWifiConnected()) {
            isAllPermissionsGranted = false;
            permissions += ", Wifi Connected";
        }
        if (getVolumePercent() <= 70) {
            isAllPermissionsGranted = false;
            permissions += ", Volume Percent";
        }
        if (getBatteryLevel() <= 50) {
            isAllPermissionsGranted = false;
            permissions += ", Battery Level";
        }
        if (isAllPermissionsGranted) {
            return true;
        } else {
            permissions = permissions.substring(2);
            Snackbar
                    .make(findViewById(R.id.main), "Permissions not granted: " + permissions, BaseTransientBottomBar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                    .setTextMaxLines(5)
                    .show();
            return false;
        }
    }

    public boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }


    private Boolean isBrightnessAutomatic() {
        ContentResolver cResolver = this.getApplicationContext().getContentResolver();
        return Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, -1) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

    }

    private boolean isMuted() {
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            Network network = connManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            }
        }
        return false;

    }

    private float getVolumePercent() {
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float cuttentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return cuttentVolume / maxVolume * 100;
    }

    private int getBatteryLevel() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

}