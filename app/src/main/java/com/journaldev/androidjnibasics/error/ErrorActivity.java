package com.journaldev.androidjnibasics.error;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;


import com.journaldev.androidjnibasics.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public final class ErrorActivity extends AppCompatActivity {
    private String strCurrentErrorLog;

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        findViewById(R.id.tvCopy).setOnClickListener(v -> copyErrorToClipboard());
        findViewById(R.id.tvShare).setOnClickListener(v -> emailErrorLog());
        findViewById(R.id.tvClose).setOnClickListener(v -> finish());
        findViewById(R.id.tvView).setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(ErrorActivity.this)
                    .setTitle("Error Log")
                    .setMessage(getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent()))
                    .setPositiveButton("Copy Log & Close",
                            (dialog1, which) -> {
                                copyErrorToClipboard();
                                dialog1.dismiss();
                            })
                    .setNeutralButton("Close", (dialog12, which) -> dialog12.dismiss())
                    .create();
            dialog.show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            if (textView != null) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setTextColor(Color.BLACK);
            }
        });
    }

    private String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getActivityLogFromIntent(Intent intent) {
        return intent.getStringExtra(UCEHandler.EXTRA_ACTIVITY_LOG);
    }

    private String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(UCEHandler.EXTRA_STACK_TRACE);
    }

    private void emailErrorLog() {
        String errorLog = getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Joyn Error Logs");
        intent.putExtra(Intent.EXTRA_TEXT, errorLog);
        startActivity(Intent.createChooser(intent, "Sharing Joyn Error Logs"));
    }


    private void copyErrorToClipboard() {
        String errorInformation = getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("View Error Log", errorInformation);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ErrorActivity.this, "Error Log Copied", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAllErrorDetailsFromIntent(Context context, Intent intent) {
        if (TextUtils.isEmpty(strCurrentErrorLog)) {
            String LINE_SEPARATOR = "\n";
            StringBuilder errorReport = new StringBuilder();
            errorReport.append("\nDEVICE INFO:\n-----------------\n");
            errorReport.append("Brand: ");
            errorReport.append(Build.BRAND);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Device: ");
            errorReport.append(Build.DEVICE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Model: ");
            errorReport.append(Build.MODEL);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Manufacturer: ");
            errorReport.append(Build.MANUFACTURER);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Product: ");
            errorReport.append(Build.PRODUCT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("SDK: ");
            errorReport.append(Build.VERSION.SDK);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Release: ");
            errorReport.append(Build.VERSION.RELEASE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\nAPP INFO:\n-----------------\n");
            String versionName = getVersionName(context);
            errorReport.append("Version: ");
            errorReport.append(versionName);
            errorReport.append(LINE_SEPARATOR);
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String firstInstallTime = getFirstInstallTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ");
                errorReport.append(firstInstallTime);
                errorReport.append(LINE_SEPARATOR);
            }
            String lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ");
                errorReport.append(lastUpdateTime);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("Current Date: ");
            errorReport.append(dateFormat.format(currentDate));
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\nERROR LOG:\n-----------------\n");
            errorReport.append(getStackTraceFromIntent(intent));
            errorReport.append(LINE_SEPARATOR);
            String activityLog = getActivityLogFromIntent(intent);
            errorReport.append(LINE_SEPARATOR);
            if (activityLog != null) {
                errorReport.append("\nUSER ACTIVITIES:\n----------------------------------\n");
                errorReport.append("User Activities: ");
                errorReport.append(activityLog);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("\n------> END OF LOG <------\n");
            strCurrentErrorLog = errorReport.toString();
            return strCurrentErrorLog;
        } else {
            return strCurrentErrorLog;
        }
    }

    private String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}