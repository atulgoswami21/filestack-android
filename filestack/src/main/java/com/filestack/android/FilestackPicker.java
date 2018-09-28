package com.filestack.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.filestack.Config;
import com.filestack.StorageOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FilestackPicker {

    public static boolean canHandleResult(int requestCode, int resultCode) {
        return requestCode == FILESTACK_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK;
    }

    @SuppressWarnings("unchecked")
    public static List<Selection> unpackResults(Intent data) {
        if (data == null || !data.hasExtra(FsConstants.EXTRA_SELECTION_LIST)) {
            return Collections.emptyList();
        }
        List<Selection> selections =
                (List<Selection>) data.getSerializableExtra(FsConstants.EXTRA_SELECTION_LIST);
        return new ArrayList<>(selections);
    }

    private static final int FILESTACK_PICKER_REQUEST_CODE = 1918;

    private final Builder builder;

    private FilestackPicker(Builder builder) {
        this.builder = builder;
    }

    //showWith?
    public void show(Activity activity) {
        Intent intent = new Intent(activity, FsActivity.class);
        intent.putExtra(FsConstants.EXTRA_SOURCES, new ArrayList<>(builder.sources));
        intent.putExtra(FsConstants.EXTRA_AUTO_UPLOAD, builder.autoUpload);
        intent.putExtra(FsConstants.EXTRA_MIME_TYPES, new ArrayList<>(builder.mimeTypes));
        intent.putExtra(FsConstants.EXTRA_CONFIG, builder.config);
        intent.putExtra(FsConstants.EXTRA_ALLOW_MULTIPLE_FILES, builder.allowMultipleFiles);
        activity.startActivityForResult(intent, FILESTACK_PICKER_REQUEST_CODE);
    }

    public static class Builder {

        Config config;
        StorageOptions storageOptions;
        List<String> mimeTypes = new ArrayList<>();
        List<String> sources = new ArrayList<>();
        boolean autoUpload;
        boolean allowMultipleFiles;

        public Builder config(Config config) {
            this.config = config;
            return this;
        }

        public Builder autoUpload(boolean autoUpload) {
            this.autoUpload = autoUpload;
            return this;
        }

        public Builder storageOptions(StorageOptions storageOptions) {
            this.storageOptions = storageOptions;
            return this;
        }

        public Builder mimeTypes(List<String> mimeTypes) {
            this.mimeTypes.addAll(mimeTypes);
            return this;
        }

        public Builder sources(List<String> sources) {
            this.sources.addAll(sources);
            return this;
        }

        public Builder allowMultipleFiles(boolean allowMultipleFiles) {
            this.allowMultipleFiles = allowMultipleFiles;
            return this;
        }

        public FilestackPicker build() {
            return new FilestackPicker(this);
        }
    }

}
