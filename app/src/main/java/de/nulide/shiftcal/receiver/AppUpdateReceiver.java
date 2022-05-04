package de.nulide.shiftcal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.nulide.shiftcal.tools.Alarm;

public class AppUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = new Alarm(context.getFilesDir());
        alarm.setDNDAlarm(context);
        alarm.setAlarm(context);
    }
}
