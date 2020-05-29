package skripsi.ubm.studenttracking;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Installation extends Activity {
    private Button cmdSubmit;
    private EditText txtNim;
    PendingIntent pendingIntent;
    HttpPost httpPost;
    HttpResponse response;
    HttpClient httpClient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation);
        cmdSubmit = (Button) findViewById(R.id.cmdSubmit);
        txtNim = (EditText) findViewById(R.id.txtNim);
        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(Installation.this, "", "Validating user...", true);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        login();
                    }
                }).start();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startAt7() {
        Intent alarmIntent = new Intent(Installation.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Installation.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,8);
        calendar.set(Calendar.AM_PM,Calendar.AM);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);
        //manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000 * 5, pendingIntent);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        //manager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_installation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void login()
    {
        try
        {
            httpClient=new DefaultHttpClient();
            httpPost= new HttpPost("http://studentstracking.hol.es/loginDatabase2.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("username",txtNim.getText().toString().trim()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response=httpClient.execute(httpPost);
           ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                   Toast.makeText(Installation.this,"Response from Server : " + response,Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            if(response.equalsIgnoreCase("User Found")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Installation.this,"Login Success", Toast.LENGTH_SHORT).show();
                    }
                });
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("NIM", txtNim.getText().toString().trim());
                editor.commit();
                startAt7();
                finish();
            }else{
                showAlert();
            }

        }catch(Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert(){
        Installation.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Installation.this);
                builder.setTitle("Login Error.");
                builder.setMessage("User not Found.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
