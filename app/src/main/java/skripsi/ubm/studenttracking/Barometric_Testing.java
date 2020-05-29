package skripsi.ubm.studenttracking;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;


public class Barometric_Testing extends Activity {

    TextView pressView;
    private SensorManager sensorManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barometric__testing);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressView = (TextView)findViewById(R.id.pressView);
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
            String value = String.valueOf(height);
            pressView.setText(value);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
