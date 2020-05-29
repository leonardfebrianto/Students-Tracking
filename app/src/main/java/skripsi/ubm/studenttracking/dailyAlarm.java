package skripsi.ubm.studenttracking;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/**
 * Created by Leon on 27/07/2015.
 */
public class dailyAlarm extends BroadcastReceiver {
    PendingIntent pendingIntent;
    private Context mContext;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,07);
        //manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000 * 5, pendingIntent);
        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
