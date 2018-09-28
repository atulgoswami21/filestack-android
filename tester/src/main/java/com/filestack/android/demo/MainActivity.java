package com.filestack.android.demo;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.filestack.Config;
import com.filestack.android.FilestackPicker;
import com.filestack.android.FsActivity;
import com.filestack.android.FsConstants;
import com.filestack.android.Selection;
import com.filestack.android.internal.Util;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FILESTACK = RESULT_FIRST_USER;
    private static final int REQUEST_SETTINGS = REQUEST_FILESTACK + 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            IntentFilter intentFilter = new IntentFilter(FsConstants.BROADCAST_UPLOAD);
            TextView logView = findViewById(R.id.log);
            UploadStatusReceiver receiver = new UploadStatusReceiver(logView);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Locale locale = Locale.getDefault();

        super.onActivityResult(requestCode, resultCode, data);

        if (FilestackPicker.canHandleResult(requestCode, resultCode)) {
            List<Selection> selections = FilestackPicker.unpackResults(data);
            for (int i = 0; i < selections.size(); i++) {
                Selection selection = selections.get(i);
                String msg = String.format(locale, "selection %d: %s", i, selection.getName());
                Log.i(TAG, msg);
            }
        }
    }

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    public void launch(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> sources = sharedPref.getStringSet("upload_sources", null);
        boolean autoUpload = sharedPref.getBoolean("auto_upload", false);
        boolean multipleFilePick = sharedPref.getBoolean("multiple_file_pick", true);
        String mimeFilter = sharedPref.getString("mime_filter", null);
        String[] mimeTypes = mimeFilter.split(",");
        String apiKey = sharedPref.getString("api_key", null);
        String policy = sharedPref.getString("policy", null);
        String signature = sharedPref.getString("signature", null);
        if (apiKey == null) {
            Toast.makeText(this, R.string.error_no_api_key, Toast.LENGTH_SHORT).show();
            return;
        }
        Config config = new Config(apiKey, getString(R.string.return_url), policy, signature);


        FilestackPicker filestackPicker =
                new FilestackPicker.Builder()
                        .config(config)
                .allowMultipleFiles(multipleFilePick)
                .autoUpload(autoUpload)
                .mimeTypes(Arrays.asList(mimeTypes))
                .sources(new ArrayList<>(sources))
                .build();

        filestackPicker.show(this);
    }
}
