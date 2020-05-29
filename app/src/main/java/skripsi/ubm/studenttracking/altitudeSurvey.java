package skripsi.ubm.studenttracking;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Leon on 8/3/2015.
 */
public class altitudeSurvey extends android.app.Service {
    static String value;
    static double valueOftxtBaro;
    static String values;
    HttpPost httpPost;
    HttpResponse response;
    HttpClient httpClient;
    List<NameValuePair> nameValuePairs;
    private SensorManager sensorManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT).show();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toast.makeText(getApplicationContext(),"Altitude : " + value,Toast.LENGTH_SHORT).show();
        //new GetLength().execute();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float pressure_value = 0.0f;
            float height = 0.0f;
            if (Sensor.TYPE_PRESSURE == event.sensor.getType())
            {
                pressure_value = event.values[0];
                height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,pressure_value);
            }
            String values = String.valueOf(height);
            value = values;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    class GetLength extends AsyncTask<String, String, String> {
        String asd;
        @Override
        protected String doInBackground(String... params) {
            String time;
            SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Calendar calendars = Calendar.getInstance();
            time = dayFormat.format(calendars.getTime());
            try {
                httpClient = new DefaultHttpClient();
                httpPost = new HttpPost("http://studentstracking.hol.es/altitudeSurvey.php");
                nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("baro", value));
                nameValuePairs.add(new BasicNameValuePair("jam", time));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                final String response = httpClient.execute(httpPost, responseHandler);
                if (response.equalsIgnoreCase("Success")) {
                    asd = "Success";
                } else {
                    asd = "Failed";
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "Data " + asd,Toast.LENGTH_SHORT).show();
        }
    }
}