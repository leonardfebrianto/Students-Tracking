package skripsi.ubm.studenttracking;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.indooratlas.android.CalibrationState;
import com.indooratlas.android.IndoorAtlas;
import com.indooratlas.android.IndoorAtlasException;
import com.indooratlas.android.IndoorAtlasFactory;
import com.indooratlas.android.IndoorAtlasListener;
import com.indooratlas.android.ServiceState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Leon on 11/07/2015.
 */
public class Service2 extends android.app.Service implements IndoorAtlasListener {
    private String mApiKey = "03e4666e-d1b2-44cb-a42f-e0656cefa24e";
    private String mApiSecret = "owSYnnaoKX%AiJ!jPc0UbAU4R&zM!9U6S81hcmbPR&3Im2tnuwxVE(S3J8gHJ0c6L)&oPxZCeim4brGXlDb2dCW3ygMRrs&v8I%z4D(2JQMSHvXPV0yJEdTebJsy248Z";
    private String mVenueId = "01f25e8c-69f7-438e-9401-25db9891cd4e";

    private String mFloorPlanId2 = "408579ab-a591-4d15-a199-0a5ef5ce1de9";
    private String mFloorId2 = "c92aafd4-2768-40ea-944a-72acbe015101";
    private String mFloorPlanId3 = "f6df45cc-3c10-4528-9f24-5c375af02e6a";
    private String mFloorId3 = "23b28ee6-3f2c-4419-a630-bb676f76916d";
    private String mFloorPlanId4 = "2c73ff63-ed81-4bdd-a0cc-e81b9694f301";
    private String mFloorId4 = "1c7abebe-e57b-44f0-952a-260af78048f1";
    private String mFloorPlanId5 = "cef2ba95-3084-445a-a1b4-b8acf0ddb4cf";
    private String mFloorId5 = "ae38e0c8-b808-4c83-8c13-b6a61fa63163";
    private String mFloorPlanId6 = "408579ab-a591-4d15-a199-0a5ef5ce1de9";
    private String mFloorId6 = "c92aafd4-2768-40ea-944a-72acbe015101";
    private String mFloorPlanId7 = "408579ab-a591-4d15-a199-0a5ef5ce1de9";
    private String mFloorId7 = "c92aafd4-2768-40ea-944a-72acbe015101";

    private String mFloorPlanId_Kline = "5ba2e631-bc67-4836-ba2b-0d1fda10f44f";
    private String mFloorId_Kline = "25f32f82-7770-43bd-bc3f-51641b30d67c";
    private String mVenueId_Kline = "28d3748f-38ae-4b68-8f8e-2dd958cd4ac4";

    static String mFloorId;
    static String mFloorPlanId;
    private IndoorAtlas mIndoorAtlas;
    Context context = this;
    private static Boolean isRunning = true;
    private static CountDownTimer countDownTimer;
    private boolean mIsPositioning;
    Handler mHandler;
    static String latitude;
    static String longitude;
    static String ruang;
    static String lantai;
    static String nama_mahasiswa;
    static String noHP_orangtua;
    static String jam;
    static String kode_matkul;
    static String title;
    static String value;
    static float valueOftxtBaro;
    static String floor;
    static float x,y;
    static boolean height_check;
    static boolean room_check_onServiceUpdate;
    static boolean isRoom_check;
    static boolean status = false;
    private SensorManager sensorManager = null;
    public CountDownTimer cdt;
    long total = 20000;
    static long total_onServiceUpdate = 20000;
    static int values;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
        if (lantai.equals("2"))
        {
            x = 16;
            y = 21;
        }
        else if(lantai.equals("3"))
        {
            x = 22;
            y = 27;
        }
        else if(lantai.equals("4"))
        {
            x = 28;
            y = 33;
        }
        else if(lantai.equals("5"))
        {
            x = 34;
            y = 39;
        }
        else if(lantai.equals("6"))
        {
            x = 40;
            y = 45;
        }
        else if(lantai.equals("7"))
        {
            x = 46;
            y = 51;
        }
        initIndoorAtlas();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
        CountDownTimer countDownTimer3 = new CountDownTimer(15*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Toast.makeText(getApplicationContext(),"Seconds : " +millisUntilFinished/1000,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {

                countStartHeight();
                countStartLocation();
            }
        }.start();
        return START_REDELIVER_INTENT;
    }

    public void countStartHeight()
    {
        long interval = 25 * 1000;
        //long interval = 1810 * 1000;
        CountDownTimer countDownTimer1 = new CountDownTimer(interval,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (values < x || values > y)
                {
                    height_check = false;
                    Toast.makeText(getApplicationContext(),"Height checking is false. Student is Absent." , Toast.LENGTH_SHORT).show();
                    sendSMS();
                    stopSelf();
                }
                else
                {
                    height_check = true;
                    Toast.makeText(getApplicationContext(),"Height checking is Valid." , Toast.LENGTH_SHORT).show();
                    if(isRoom_check == true)
                    {
                        status = true;
                        Toast.makeText(getApplicationContext(),"Student is already in class. Now start tracking from now to next 1 minutes ahead." , Toast.LENGTH_SHORT).show();
                        countClassAbsent();
                    }
                    else
                    {
                        sendSMS();
                        stopSelf();
                    }
                }
            }
        }.start();
    }

    public void countStartLocation()
    {
        //long interval = 1800 * 1000;
        long interval = 24 * 1000;
        CountDownTimer countDownTimer2 = new CountDownTimer(interval,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (room_check_onServiceUpdate == false)
                {
                    isRoom_check = false;
                }
                else if(room_check_onServiceUpdate == true)
                {
                    isRoom_check = true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"room_check_OnServiceUpdate Failed",Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    public void countClassAbsent()
    {
        CountDownTimer cdt2 = new CountDownTimer(30 * 1000 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (height_check == true || total_onServiceUpdate > 0)
                {
                    sendSMS2();
                    stopSelf();
                }
            }
        }.start();
    }

    public void sendSMS2()
    {
        String time;
        SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar calendars = Calendar.getInstance();
        time = dayFormat.format(calendars.getTime());
        String phoneNumber = "085711969176";
        String message = "Pesan ini dikirim pada saat : " + time + "\nMahasiswa yang bernama : " + nama_mahasiswa + ", mengikuti perkuliahan pada matakuliah : " + kode_matkul + "-" + title;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(noHP_orangtua, null, message, null, null);
        Toast.makeText(getApplicationContext(),"Pelacakan Telah Selesai",Toast.LENGTH_SHORT).show();
        stopPositioning();
    }



    public void sendSMS()
    {
        String time;
        SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar calendars = Calendar.getInstance();
        time = dayFormat.format(calendars.getTime());
        String phoneNumber = "085711969176";
        String message = "Pesan ini dikirim pada saat : " + time + "\nMahasiswa yang bernama : " + nama_mahasiswa + ", tidak mengikuti perkuliahan pada matakuliah : " + kode_matkul + "-" + title;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(noHP_orangtua, null, message, null, null);
        Toast.makeText(getApplicationContext(),"Pelacakan Telah Selesai",Toast.LENGTH_SHORT).show();
        stopPositioning();
    }
    private void initIndoorAtlas()
    {
        try {
            mIndoorAtlas = IndoorAtlasFactory.createIndoorAtlas(
                    context, this, mApiKey, mApiSecret);
            startPositioning();
        } catch (IndoorAtlasException e) {
            e.printStackTrace();
        }
    }

    public SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float pressure_value = 0.0f;
            float height = 0.0f;
            if (Sensor.TYPE_PRESSURE == event.sensor.getType())
            {
                pressure_value = event.values[0];
                height = SensorManager.getAltitude(1012, pressure_value);
            }
            valueOftxtBaro = height;
            values = Math.round(valueOftxtBaro);

            if(status == true)
            {
                if (values < x || values > y)
                {
                    if (total == 20000)
                    {
                        cdt = new CountDownTimer(total,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                total = millisUntilFinished/1000;
                                Toast.makeText(getApplicationContext(),"X : " + millisUntilFinished/1000,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(getApplicationContext(),"Student is absent",Toast.LENGTH_SHORT).show();
                                sendSMS();
                                stopSelf();
                            }
                        }.start();
                    }
                }
                else
                {
                    if (cdt != null || total < 20000)
                    {
                        cdt.cancel();
                        total = 20000;
                        height_check = true;
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onServiceUpdate(ServiceState state)
    {
        LatLng latLng = new LatLng(state.getGeoPoint().getLatitude(),state.getGeoPoint().getLongitude());
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("Location : " + latLng));
        Double latitudex,longitudex;
        latitudex = Double.parseDouble(latitude);
        longitudex = Double.parseDouble(longitude);
        final float[] distance = new float[2];
        Location.distanceBetween(state.getGeoPoint().getLatitude(), state.getGeoPoint().getLongitude(), latitudex,longitudex, distance);
        if (distance[0] > 25.0)
        {
            room_check_onServiceUpdate = false;
            if (isRunning == false) {
                isRunning = true;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        countDownTimer = new CountDownTimer(total_onServiceUpdate, 1000)
                        {
                            @Override
                            public void onTick(long millisUntilFinished) {

                                if (isRunning == false) {
                                    countDownTimer.cancel();
                                }
                                else if (isRunning == true)
                                {
                                    total_onServiceUpdate = millisUntilFinished / 1000;
                                    Toast.makeText(context, "Second : " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFinish()
                            {
                                sendSMS();
                                stopSelf();
                            }
                        }.start();
                    }
                });
            } else
            {
                isRunning = true;
            }
        } else {
            room_check_onServiceUpdate = true;
            if (isRunning == true)
            {
                if (total_onServiceUpdate < 20000)
                {
                    countDownTimer.cancel();
                    total_onServiceUpdate = 20000;
                }

                isRunning = false;
                mHandler = new Handler(getMainLooper());
                mHandler.post(new DisplayToast("Student position are inside class"));
            }
        }
    }

    @Override
    public void onServiceFailure(int errorCode, String reason) {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("onServiceFailure: reason : " + reason));
    }

    @Override
    public void onServiceInitializing() {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("onServiceInitializing"));
    }

    @Override
    public void onServiceInitialized() {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("onServiceInitialized"));
    }

    @Override
    public void onInitializationFailed(String reason) {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("onInitializationFailed"));
    }

    @Override
    public void onServiceStopped() {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("Service Stopped."));
    }

    @Override
    public void onCalibrationReady() {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("Calibration Ready."));
    }

    @Override
    public void onCalibrationInvalid() {
        mHandler = new Handler(getMainLooper());
        mHandler.post(new DisplayToast("onInitializationFailed"));
    }

    @Override
    public void onCalibrationFailed(String s) {

    }

    @Override
    public void onCalibrationStatus(CalibrationState calibrationState) {

    }

    @Override
    public void onNetworkChangeComplete(boolean b) {

    }

    private void stopPositioning() {
        mIsPositioning = false;
        if (mIndoorAtlas != null) {
            Toast.makeText(context,"Stop Positioning",Toast.LENGTH_SHORT).show();
            mIndoorAtlas.stopPositioning();
            mIndoorAtlas.tearDown();
        }
    }

    private void startPositioning() {
        Toast.makeText(getApplicationContext(),"Lantai : " + lantai,Toast.LENGTH_SHORT).show();
        if (lantai.equals("2"))
        {
            mFloorId = mFloorId2;
            mFloorPlanId = mFloorPlanId2;
        }
        else if(lantai.equals("3"))
        {
            mFloorId = mFloorId3;
            mFloorPlanId = mFloorPlanId3;
        }
        else if(lantai.equals("4"))
        {
            mFloorId = mFloorId4;
            mFloorPlanId = mFloorPlanId4;
        }
        else if(lantai.equals("5"))
        {
            mFloorId = mFloorId5;
            mFloorPlanId = mFloorPlanId5;
        }
        else if(lantai.equals("6"))
        {
            mFloorId = mFloorId6;
            mFloorPlanId = mFloorPlanId6;
        }
        else if(lantai.equals("7"))
        {
            mFloorId = mFloorId7;
            mFloorPlanId = mFloorPlanId7;
        }



        if (mIndoorAtlas != null) {
            try {
                mIndoorAtlas.startPositioning(mVenueId, mFloorId2, mFloorPlanId2);
                mIsPositioning = true;
                mHandler = new Handler(getMainLooper());
                mHandler.post(new DisplayToast("StartPositioning"));
            } catch (IndoorAtlasException e) {
                Toast.makeText(context,"startPositioning failed: " + e,Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context,"calibration not ready, cannot start positioning",Toast.LENGTH_SHORT).show();
        }
    }

    private class DisplayToast implements Runnable{
        String mText;

        public DisplayToast(String text){
            mText = text;
        }

        public void run(){
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }
}
