package skripsi.ubm.studenttracking;

import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class student_activity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener,LocationListener
{
private GoogleMap mMap;
private GoogleApiClient mGoogleApiClient;
private LocationRequest mLocationRequest;

public static final String TAG = MapsActivity.class.getSimpleName();


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            mLocationRequest = LocationRequest.create()
                    .setInterval(10 * 1000)
                    .setFastestInterval(1 * 1000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_student_activity);
            try {


            } catch (Exception e){
                e.printStackTrace();
            }
    }


    public void setUpMapIfNeeded()
    {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = mapFrag.getMap();
        mMap.setMyLocationEnabled(true);
        PolygonOptions polygons = new PolygonOptions()
                .add(   new LatLng(-6.226222,106.804049),
                        new LatLng(-6.227222,106.804049),
                        new LatLng(-6.227222,106.805049),
                        new LatLng(-6.226222,106.805049),
                        new LatLng(-6.226222,106.804049)
                )
                .fillColor(Color.BLUE)
                .strokeColor(Color.BLACK)
                .strokeWidth(1)
                ;
        Polygon polygon = mMap.addPolygon(polygons);
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.i(TAG,"Location Services Connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location==null)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
        else
        {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location)
    {
        Log.d(TAG,location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude,currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here !");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG,"Location Services suspended. Please Reconnect.");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
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

    public void setUpMap()
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
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




}
