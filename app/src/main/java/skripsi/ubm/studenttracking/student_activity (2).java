package skripsi.ubm.studenttracking;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

public class student_activity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener,LocationListener
{
private static GoogleMap mMap;
private GoogleApiClient mGoogleApiClient;
private LocationRequest mLocationRequest;
public Circle mCircle;
public static final String TAG = MapsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_student_activity);
            }

    public void circle()
    {
        double radiusInMeters = 100.0;
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;
        mCircle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(-6.130168, 106.818408))
                        .radius(radiusInMeters)
                        .fillColor(shadeColor)
                        .strokeColor(strokeColor)
                        .strokeWidth(1)
        );
    }

    public void setUpMap()
    {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = mapFrag.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        circle();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mLocationRequest = LocationRequest.create()
                .setInterval(3)
                .setFastestInterval(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i(TAG, "Location Services Connected.");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21));
        float[] distance= new float[2];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), -6.130168, 106.818408, distance);
        if (distance[0] < 400.0)
        {
            Toast.makeText(this,"Welcome to UBM",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,indoor.class);
            startActivity(intent);
            super.onStop();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "Location Services suspended. Please Reconnect.");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMap();
        mGoogleApiClient.connect();
    }

    protected void onPause()
    {
        super.onPause();
        if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult(this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.i(TAG,"Location Services Connection Failed With Code " + connectionResult.getErrorCode());
        }
    }

    private void sendSMS()
    {
        String phoneNumber = "085711969176";
        String message = "test";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber,null,message,null,null);
    }

}
