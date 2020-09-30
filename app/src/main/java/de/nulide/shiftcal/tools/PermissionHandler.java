package de.nulide.shiftcal.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class PermissionHandler {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 4456;

    public static void RequestOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                ((Activity) context).startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public static boolean CheckForOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                return false;
            }
        }
        return true;
    }

}