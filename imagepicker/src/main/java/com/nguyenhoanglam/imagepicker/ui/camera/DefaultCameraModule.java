package com.nguyenhoanglam.imagepicker.ui.camera;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.nguyenhoanglam.imagepicker.helper.FileHelper;
import com.nguyenhoanglam.imagepicker.model.Config;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by hoanglam on 8/18/17.
 */

public class DefaultCameraModule implements CameraModule, Serializable {

    private String mediaPath;

    @Override
    public Intent getCameraIntent(Context context, Config config) {
        Log.d("INTENT", String.valueOf(config.isAcceptVideo()));
        Intent intent;
        File mediaFile;
        if (config.isAcceptVideo()) {
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            mediaFile = FileHelper.createVideoFile(config.getSavePath());
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mediaFile = FileHelper.createImageFile(config.getSavePath());
        }
        if (mediaFile != null) {
            Context appContext = context.getApplicationContext();
            String providerName = String.format(Locale.ENGLISH, "%s%s", appContext.getPackageName(), ".fileprovider");
            Uri uri = FileProvider.getUriForFile(appContext, providerName, mediaFile);
            mediaPath = mediaFile.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            FileHelper.grantAppPermission(context, intent, uri);
            return intent;
        }
        return null;
    }

    @Override
    public void getImage(final Context context, Intent intent, final OnImageReadyListener imageReadyListener) {
        if (imageReadyListener == null) {
            throw new IllegalStateException("OnImageReadyListener must not be null");
        }

        if (mediaPath == null) {
            imageReadyListener.onImageReady(null);
            return;
        }

        final Uri imageUri = Uri.parse(new File(mediaPath).toString());
        if (imageUri != null) {
            MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{imageUri.getPath()}
                    , null
                    , new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (path != null) {
                                path = mediaPath;
                            }
                            imageReadyListener.onImageReady(FileHelper.singleListFromPath(path));
                            FileHelper.revokeAppPermission(context, imageUri);
                        }
                    });
        }
    }
}
