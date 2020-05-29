package skripsi.ubm.studenttracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.indooratlas.android.CalibrationState;
import com.indooratlas.android.FloorPlan;
import com.indooratlas.android.FutureResult;
import com.indooratlas.android.IndoorAtlas;
import com.indooratlas.android.IndoorAtlasException;
import com.indooratlas.android.IndoorAtlasFactory;
import com.indooratlas.android.IndoorAtlasListener;
import com.indooratlas.android.ResultCallback;
import com.indooratlas.android.ServiceState;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class indoor extends Activity implements IndoorAtlasListener {

    private static final String TAG = "MainActivity";
    private FloorPlan mFloorplan;
    private ListView mLogView;
    private LogAdapter mLogAdapter;
    private IndoorAtlas mIndoorAtlas;
    private boolean mIsPositioning;
    private StringBuilder mSharedBuilder = new StringBuilder();
    private SensorManager sensorManager = null;

    private String mApiKey = "03e4666e-d1b2-44cb-a42f-e0656cefa24e";
    private String mApiSecret = "owSYnnaoKX%AiJ!jPc0UbAU4R&zM!9U6S81hcmbPR&3Im2tnuwxVE(S3J8gHJ0c6L)&oPxZCeim4brGXlDb2dCW3ygMRrs&v8I%z4D(2JQMSHvXPV0yJEdTebJsy248Z";

    private String mFloorPlanId = "408579ab-a591-4d15-a199-0a5ef5ce1de9";
    private String mFloorId = "c92aafd4-2768-40ea-944a-72acbe015101";
    private String mVenueId = "01f25e8c-69f7-438e-9401-25db9891cd4e";

    private String mFloorPlanId_Kline = "5ba2e631-bc67-4836-ba2b-0d1fda10f44f";
    private String mFloorId_Kline = "25f32f82-7770-43bd-bc3f-51641b30d67c";
    private String mVenueId_Kline = "28d3748f-38ae-4b68-8f8e-2dd958cd4ac4";

    private String deviceId;
    private TextView txtBaro;
    private String value;
    private static float valueOftxtBaro;
    private TextView txtCount;
    private TextView txtTest;
    private static boolean isRunning;
    private static CountDownTimer countDownTimer;
    private static int smsCount;
    CountDownTimer cdt = null;
    long total = 20000;
    static int values;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogView = (ListView) findViewById(R.id.list);
        mLogAdapter = new LogAdapter(this);
        mLogView.setAdapter(mLogAdapter);
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        initIndoorAtlas();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        txtBaro = (TextView)findViewById(R.id.txtBaro);
        txtTest = (TextView)findViewById(R.id.txtTest);
        timerTask();
    }

    public void timerTask()
    {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                txtCount = (TextView)findViewById(R.id.txtCount2);
                String myDate = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
                txtCount.setText(myDate);
            }
        };
        timerTask.run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tearDown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_indoor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_log:
                mLogAdapter.clear();
                return true;
            case R.id.action_toggle_positioning:
                togglePositioning();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadFloorPlanImage(FloorPlan floorPlan) {
        BitmapFactory.Options options = createBitmapOptions(floorPlan);
        FutureResult<Bitmap> result = mIndoorAtlas.fetchFloorPlanImage(floorPlan,options);
        result.setCallback(new ResultCallback<Bitmap>() {
            @Override
            public void onResult(final Bitmap result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageBitmap(result);
                        log("oNResult LoadFloorPlanImage");
                    }
                });
            }

            @Override
            public void onSystemError(IOException e) {

            }

            @Override
            public void onApplicationError(IndoorAtlasException e) {

            }
        });
    }

    public SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float pressure_value = 0.0f;
            float height = 0.0f;
            if (Sensor.TYPE_PRESSURE == event.sensor.getType())
            {
                pressure_value = event.values[0];
                height = SensorManager.getAltitude(1012,pressure_value);
            }

            valueOftxtBaro = height;
            values = Math.round(valueOftxtBaro);
            value = String.valueOf(values);
            txtBaro.setText(value);
            if (values <= 39 || values >= 42)
            {
                if (total == 20000)
                {
                    cdt = new CountDownTimer(total,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            total = millisUntilFinished/1000;
                            txtTest.setText("Second : " + total);
                        }

                        @Override
                        public void onFinish() {
                            txtTest.setText("Finish !");
                        }
                    }.start();
                }
            }
            else
            {
                if (cdt != null || total < 20000)
                {
                    cdt.cancel();
                    txtTest.setText("Paused");
                    total = 20000;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void actCountDownTimer ()
    {
        cdt = new CountDownTimer(total,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                total = millisUntilFinished;
                txtCount.setText("Second : " + millisUntilFinished / 1000);
                txtTest.setText("Value : " + total / 1000);
                //Toast.makeText(getApplicationContext(),"Height : " + millisUntilFinished/1000,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                if (values >= 46 || values <= 48 ) {
                    txtTest.setText("Inside");
                    total = 20000;
                }
                else {
                    txtTest.setText("Finish");
                    onDestroy();
                }

            }
        }.start();
    }

    public BitmapFactory.Options createBitmapOptions(FloorPlan floorPlan) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return options;
    }

    private void initIndoorAtlas() {
        try {
            log("Connecting with IndoorAtlas, apiKey: " + mApiKey);
            // obtain instance to positioning service, note that calibrating might begin instantly
            mIndoorAtlas = IndoorAtlasFactory.createIndoorAtlas(
                    getApplicationContext(),
                    this, // IndoorAtlasListener
                    mApiKey,
                    mApiSecret);
            log("IndoorAtlas instance created");

            FutureResult<FloorPlan> result = mIndoorAtlas.fetchFloorPlan(mFloorPlanId);
            result.setCallback(new ResultCallback<FloorPlan>() {
                @Override
                public void onResult(final FloorPlan result) {
                    mFloorplan = result;
                    loadFloorPlanImage(result);
                    log("Load Floor Plan Image");
                }

                @Override
                public void onSystemError(IOException e) {

                }

                @Override
                public void onApplicationError(IndoorAtlasException e) {

                }
            });
        } catch (IndoorAtlasException ex) {
            Log.e("IndoorAtlas", "init failed", ex);
            log("init IndoorAtlas failed, " + ex.toString());
        }
    }

    public void onServiceUpdate(ServiceState state) {
        log("onServiceUpdate");
        mSharedBuilder.setLength(0);
        mSharedBuilder.append("Location: ")
                .append("\n\tPing : ").append(state.getRoundtrip()).append("ms")
                .append("\n\tLatitude : ").append(state.getGeoPoint().getLatitude())
                .append("\n\tLongitude : ").append(state.getGeoPoint().getLongitude())
                .append("\n\tX [meter] : ").append(state.getMetricPoint().getX())
                .append("\n\tY [meter] : ").append(state.getMetricPoint().getY())
                .append("\n\tI [pixel] : ").append(state.getImagePoint().getI())
                .append("\n\tJ [pixel] : ").append(state.getImagePoint().getJ())
                .append("\n\tHeading : ").append(state.getHeadingDegrees())
                .append("\n\tUncertainty: ").append(state.getUncertainty());

        log(mSharedBuilder.toString());
        final int i, j;
        i = state.getImagePoint().getI();
        j = state.getImagePoint().getJ();
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");

            // build HTTP POST request to send these data to the database
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("x", Double.toString(x)));
            //params.add(new BasicNameValuePair("y", Double.toString(y)));
            //params.add(new BasicNameValuePair("i", Integer.toString(i)));
            //params.add(new BasicNameValuePair("j", Integer.toString(j)));
            //params.add(new BasicNameValuePair("time", sd.format(d.getTime())));
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            final ImageView imageFloor = (ImageView) findViewById(R.id.imageView);
            final Bitmap bitmapCircle = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas = new Canvas(bitmapCircle);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(10);
            canvas.drawBitmap(bitmap, new Matrix(), null);
            canvas.drawCircle(i, j, 10, paint);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageFloor.setImageBitmap(bitmapCircle);
                }
            });
        final float[] distance = new float[2];
        Location.distanceBetween(state.getGeoPoint().getLatitude(), state.getGeoPoint().getLongitude(), -6.1303391768200575,106.81802724867924, distance);
        if (distance[0] > 25.0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isRunning == false) {
                        isRunning = true;
                        countDownTimer = new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (isRunning == false)
                                {
                                    countDownTimer.cancel();
                                }
                                else if (isRunning == true){
                                    //txtCount.setText("Second : " + millisUntilFinished / 1000);
                                }
                            }

                            @Override
                            public void onFinish() {
                                txtCount.setText("Finish !");
                            }
                        }.start();
                    }
                    else
                    {
                        isRunning = true;
                    }
                }
            });
    }
        else
        {
            if (isRunning == true)
            {
                countDownTimer.cancel();
                isRunning = false;
                txtCount.setText("Student position are inside class");
            }
        }
    }
    public void count()
    {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtCount.setText("Second : " + millisUntilFinished /1000);
            }

            @Override
            public void onFinish() {
                txtCount.setText("Finish !");
            }
        }.start();
    }

    public void sendSMS()
    {
        String phoneNumber = "085711969176";
        String message = "test";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber,null,message,null,null);
    }

    private void tearDown() {
        if (mIndoorAtlas != null) {
            mIndoorAtlas.tearDown();
        }
    }

    private void stopPositioning() {
        mIsPositioning = false;
        if (mIndoorAtlas != null) {
            log("Stop positioning");
            mIndoorAtlas.stopPositioning();
        }
    }

    private void startPositioning() {

        if (mIndoorAtlas != null) {
            log(String.format("startPositioning, venueId: %s, floorId: %s, floorPlanId: %s",
                    mVenueId,
                    mFloorId,
                    mFloorPlanId));
            try {
                mIndoorAtlas.startPositioning(mVenueId, mFloorId, mFloorPlanId);
                mIsPositioning = true;
            } catch (IndoorAtlasException e) {
                log("startPositioning failed: " + e);
            }
        } else {
            log("calibration not ready, cannot start positioning");
        }
    }

    private void togglePositioning() {
        if (mIsPositioning) {
            stopPositioning();
        } else {
            startPositioning();
        }
    }

    private void log(final String msg) {
        Log.d(TAG, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogAdapter.add(msg);
                mLogAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onServiceFailure(int errorCode, String reason) {
        log("onServiceFailure: reason : " + reason);
    }

    @Override
    public void onServiceInitializing() {
        log("onServiceInitializing");
    }

    @Override
    public void onServiceInitialized() {
        log("onServiceInitialized");
    }

    @Override
    public void onInitializationFailed(final String reason) {
        log("onInitializationFailed: " + reason);
    }

    @Override
    public void onServiceStopped() {
        log("onServiceStopped");
    }

    @Override
    public void onCalibrationStatus(CalibrationState calibrationState) {
        log("onCalibrationStatus, percentage: " + calibrationState.getPercentage());
    }

    @Override
    public void onCalibrationReady() {
        log("onCalibrationReady");
    }

    @Override
    public void onNetworkChangeComplete(boolean success) {
    }

    @Override
    public void onCalibrationInvalid() {
    }

    @Override
    public void onCalibrationFailed(String reason) {
    }

    static class LogAdapter extends BaseAdapter {

        private ArrayList<String> mLines = new ArrayList<String>();
        private LayoutInflater mInflater;

        public LogAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mLines.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = (TextView) convertView;
            if (convertView == null) {
                text = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent,
                        false);
            }
            text.setText(mLines.get(position));
            return text;
        }

        public void add(String line) {
            mLines.add(0, line);
        }

        public void clear() {
            mLines.clear();
            notifyDataSetChanged();
        }
    }
}
