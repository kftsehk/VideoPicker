package com.nguyenhoanglam.imagepicker.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.SavePath;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hoanglam on 7/31/16.
 * Updated by kftse on 21/12/19.
 */

public class FileHelper {

    private static final String TAG = "FileHelper";
    // https://en.wikipedia.org/wiki/Comparison_of_video_container_formats
    private static final List<String> videoExtensions = Arrays.asList("3gp", "3g2", "asf", "wmv", "avi",
            "divx", "evo", "f4v", "flv", "mkv", "mk3d", "mp4", "mpg", "mpeg", "m2p", "ps", "ts",
            "m2ts", "mxf", "ogg", "mov", "qt", "rmvb", "vob", "webm");

    public static File createVideoFile(SavePath savePath) {
        // External sdcard location
        final String path = savePath.getPath();
        File mediaStorageDir = savePath.isFullPath()
                ? new File(path)
                : new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), path);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + path);
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MOV_" + timeStamp;

        File videoFile = null;
        try {
            videoFile = File.createTempFile(videoFileName, ".mp4", mediaStorageDir);
        } catch (IOException e) {
            Log.d(TAG, "Oops! Failed create " + videoFileName + " file");
        }
        return videoFile;
    }

    public static File createImageFile(SavePath savePath) {
        // External sdcard location
        final String path = savePath.getPath();
        File mediaStorageDir = savePath.isFullPath()
                ? new File(path)
                : new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), path);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + path);
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", mediaStorageDir);
        } catch (IOException e) {
            Log.d(TAG, "Oops! Failed create " + imageFileName + " file");
        }
        return imageFile;
    }

    public static String getNameFromFilePath(String path) {
        if (path.contains(File.separator)) {
            return path.substring(path.lastIndexOf(File.separator) + 1);
        }
        return path;
    }

    public static void grantAppPermission(Context context, Intent intent, Uri fileUri) {
        List<ResolveInfo> resolvedIntentActivities = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public static void revokeAppPermission(Context context, Uri fileUri) {
        context.revokeUriPermission(fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    public static List<Image> singleListFromPath(String path) {
        List<Image> images = new ArrayList<>();
        images.add(new Image(0, getNameFromFilePath(path), path));
        return images;
    }

    public static boolean isVideoFormat(Image image) {
        String extension = image.getPath().substring(image.getPath().lastIndexOf(".") + 1);
        return videoExtensions.indexOf(extension.toLowerCase()) != -1;
    }
}
