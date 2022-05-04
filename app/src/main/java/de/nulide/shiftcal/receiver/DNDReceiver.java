package de.nulide.shiftcal.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import de.nulide.shiftcal.tools.Alarm;

public class DNDReceiver extends BroadcastReceiver {

    public static final int DND_ID_START = 9999991;
    public static final int DND_ID_STOP = 9999990;
    public static final String DND_START_STOP = "DNDSS";
    public static final int START = 1;
    public static final int STOP = 0;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = new Alarm(context.getFilesDir());
        Bundle bundle = intent.getExtras();
        int SS = bundle.getInt(DND_START_STOP);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (SS == START) {
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
        } else {
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
        alarm.setDNDAlarm(context);
    }
}
