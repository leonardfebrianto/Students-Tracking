package skripsi.ubm.studenttracking;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.prefs.BackingStoreException;

public class backgroundApplication extends Activity
{
    private PendingIntent pendingIntent;
    Service service;
    ArrayList<String> kode_matkul = new ArrayList<String>();
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<String> ruang = new ArrayList<String>();
    ArrayList<String> lantai = new ArrayList<String>();
    ArrayList<String> latitude = new ArrayList<String>();
    ArrayList<String> longitude = new ArrayList<String>();
    ArrayList<String> hari = new ArrayList<String>();
    ArrayList<String> jam = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_application);
        kode_matkul = getIntent().getStringArrayListExtra("kode_matkul");
        title = getIntent().getStringArrayListExtra("title");
        ruang = getIntent().getStringArrayListExtra("ruang");
        lantai = getIntent().getStringArrayListExtra("lantai");
        latitude = getIntent().getStringArrayListExtra("latitude");
        longitude = getIntent().getStringArrayListExtra("longitude");
        hari = getIntent().getStringArrayListExtra("hari");
        jam = getIntent().getStringArrayListExtra("jam");
        //String asd = kode_matkul.get(2);
        //Toast.makeText(this,"Jam : " + asd,Toast.LENGTH_SHORT).show();

        /* Retrieve a PendingIntent that will perform a broadcast */

        findViewById(R.id.startAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        findViewById(R.id.stopAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        findViewById(R.id.stopAlarmAt10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAt10();
            }
        });
        findViewById(R.id.cmdCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        findViewById(R.id.cmdServiceGPS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msgIntent = new Intent(backgroundApplication.this, skripsi.ubm.studenttracking.Service.class);
                msgIntent.putExtra("kode_matkul",kode_matkul);
                startService(msgIntent);
            }
        });

        findViewById(R.id.cmdServiceIA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msgIntent = new Intent(backgroundApplication.this, Service2.class);
                startService(msgIntent);
            }
        });
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;
        Intent alarmIntent = new Intent(getApplicationContext(), altitudeSurvey.class);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(backgroundApplication.this, altitudeSurvey.class);
        PendingIntent pendingIntents;
        pendingIntents = PendingIntent.getService(backgroundApplication.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntents.cancel();
        manager.cancel(pendingIntents);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startAt10() {
        Intent alarmIntent = new Intent(backgroundApplication.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(backgroundApplication.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK,calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 00);
        //manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000 * 5, pendingIntent);
        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //sendSMS();
    }

    public void sendSMS()
    {
        String time;
        SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar calendars = Calendar.getInstance();
        time = dayFormat.format(calendars.getTime());
        String phoneNumber = "085711969176";
        String message = "This SMS Was sent at : " + time;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void check()
    {
       Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = null;
        if (day==1)
        {
            today = "Sunday";
        }
        else if(day==2)
        {
            today = "Monday";
        }
        else if(day==3)
        {
            today = "Tuesday";
        }
        else if(day==4)
        {
            today = "Wednesday";
        }
        else if(day==5)
        {
            today = "Thursday";
        }
        else if(day==6)
        {
            today = "Friday";
        }
        else if(day==7)
        {
            today = "Saturday";
        }
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String value = (mSharedPreference.getString("NIM","Default_Value"));
        Toast.makeText(getApplicationContext(),"Today is : " + value, Toast.LENGTH_SHORT).show();
        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(backgroundApplication.this,AlarmReceiver.class),PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmUp)
        {
            Toast.makeText(getApplicationContext(),"Alarm On!",Toast.LENGTH_SHORT).show();
        }
        else if(!alarmUp)
        {
            Toast.makeText(getApplicationContext(),"Alarm Off!",Toast.LENGTH_SHORT).show();
        }
    }
}