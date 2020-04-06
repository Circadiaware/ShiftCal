package de.nulide.shiftcal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import de.nulide.shiftcal.ExecutedAlarmActivity;
import de.nulide.shiftcal.tools.Alarm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ExecutedAlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = intent.getExtras();
        i.putExtra(Alarm.EXT_SHIFT, bundle.getInt(Alarm.EXT_SHIFT));
        context.startActivity(i);
    }
}
