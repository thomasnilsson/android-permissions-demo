package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    UsageStatsManager usageStatsManager;

    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usageStatsManager = (UsageStatsManager) this.getApplicationContext()
                .getSystemService("usagestats");

        checkSwitches();
    }


    void checkSwitches() {
        Switch switchActivity = (Switch) findViewById(R.id.switchActivity);
        Switch switchLocation = (Switch) findViewById(R.id.switchLocation);
        Switch switchNotifications = (Switch) findViewById(R.id.switchNotifications);
        Switch switchAppusage = (Switch) findViewById(R.id.switchAppUsage);

        final int locationEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        final int activityEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);

        if (locationEnabled == PackageManager.PERMISSION_GRANTED) switchLocation.setChecked(true);
        if (activityEnabled == PackageManager.PERMISSION_GRANTED) switchActivity.setChecked(true);
        if (usageStatsAreAvailable()) switchAppusage.setChecked(true);
        if (notificationPermissionGiven())
            switchNotifications.setChecked(true);
    }

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions(String[] requiredPermissions) {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : requiredPermissions) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            } else {
                Toast.makeText(this, "Required permission '" + permission
                        + "' already granted", Toast.LENGTH_LONG).show();
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[requiredPermissions.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, requiredPermissions,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    private void initialize() {
        Toast.makeText(this, "Hurray!", Toast.LENGTH_LONG);
    }

    public void askForSendingNotifications(View v) {
        Log.d(TAG, "askForLocation");
        String[] p = {Manifest.permission.ACCESS_NOTIFICATION_POLICY};
        checkPermissions(p);
    }

    public void askForLocation(View v) {
        Log.d(TAG, "askForLocation");
        String[] p = {Manifest.permission.ACCESS_FINE_LOCATION};
        checkPermissions(p);
    }

    public void askForActivity(View v) {
        Log.d(TAG, "askForActivity");
        String[] p = {Manifest.permission.ACTIVITY_RECOGNITION};
        checkPermissions(p);
    }

    public void askForNotifications(View v) {
        Log.d(TAG, "askForNotifications");
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    @SuppressLint("WrongConstant")
    public void askForAppUsageStats(View v) {
        Log.d(TAG, "askForAppUsageStats");

        // If stats are not available, show the permission screen to give access to them
        if (!usageStatsAreAvailable()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            this.startActivity(intent);
        }
    }

    public boolean usageStatsAreAvailable() {
        long now = Calendar.getInstance().getTimeInMillis();

        // Check if any usage stats are available from the beginning of time until now
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, now);

        // Return whether or not stats are available
        return stats.size() > 0;
    }

    private boolean notificationPermissionGiven() {
        String packageName = this.getPackageName();
        String flat = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            String[] names = flat.split(":");
            for (String name : names) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                boolean nameMatch = TextUtils.equals(packageName, componentName.getPackageName());
                if (nameMatch) {
                    return true;
                }
            }
        }
        return false;
    }


}