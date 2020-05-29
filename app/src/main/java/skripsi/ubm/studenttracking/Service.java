package skripsi.ubm.studenttracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Leon on 11/07/2015.
 */
public class Service extends android.app.Service{
    public LocationManager locationManager;
    public MyLocationListener listener;
    static String ruang;
    static String lantai;
    static String latitude;
    static String longitude;
    static String nama_mahasiswa;
    static String noHP_orangtua;
    static String jam;
    static String kode_matkul;
    static String title;
    static PendingIntent pendingIntent;
    Context context = this;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        nama_mahasiswa = intent.getStringExtra("nama_mahasiswa");
        noHP_orangtua = intent.getStringExtra("noHP_orangtua");
        ruang = intent.getStringExtra("ruang");
        lantai = intent.getStringExtra("lantai");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        kode_matkul = intent.getStringExtra("kode_matkul");
        title = intent.getStringExtra("title");
        jam = intent.getStringExtra("jam");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        //Toast.makeText(getApplicationContext(),"Ruang : " + ruang + "Lantai : " + lantai + "nama : " + nama_mahasiswa + "No : " + noHP_orangtua,Toast.LENGTH_SHORT).show();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
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
    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location location)
        {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            Toast.makeText(getApplicationContext(),"Location : " + latLng,Toast.LENGTH_SHORT).show();
            float[] distance= new float[2];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), -6.130168, 106.818408, distance);
            if (distance[0] < 400.0)
            {
                Toast.makeText(getApplicationContext(),"Welcome to UBM",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Service2.class);
                intent.putExtra("nama_mahasiswa",nama_mahasiswa);
                intent.putExtra("noHP_orangtua",noHP_orangtua);
                intent.putExtra("ruang",ruang);
                intent.putExtra("lantai",lantai);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("kode_matkul",kode_matkul);
                intent.putExtra("title",title);
                intent.putExtra("jam",jam);
                Toast.makeText(getApplicationContext(),"Jamxxx : " + jam,Toast.LENGTH_SHORT).show();
                startService(intent);
                stopSelf();

            }
            else
            {
                Toast.makeText(getApplicationContext(),"You are outside UBM",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Service2.class);
                intent.putExtra("nama_mahasiswa",nama_mahasiswa);
                intent.putExtra("noHP_orangtua",noHP_orangtua);
                intent.putExtra("ruang",ruang);
                intent.putExtra("lantai", lantai);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("kode_matkul",kode_matkul);
                intent.putExtra("title",title);
                intent.putExtra("jam",jam);
                Toast.makeText(getApplicationContext(),"Jamxxx : " + jam,Toast.LENGTH_SHORT).show();
                //sendSMS();
                startService(intent);
                stopSelf();
            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }
}