package skripsi.ubm.studenttracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class LoginActivity extends Activity  {
    Button next;
    Button login;
    EditText txtId;
    EditText txtPassword;
    TextView tv;
    HttpPost httpPost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpClient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        next();

        login = (Button)findViewById(R.id.cmdLogin);
        txtId = (EditText)findViewById(R.id.txtId);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        tv = (TextView)findViewById(R.id.tv);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this, "", "Validating user...",true);
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

    public void login()
    {
        try
        {
            httpClient=new DefaultHttpClient();
            httpPost= new HttpPost("http://studentstracking.hol.es/loginDatabase2.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username",txtId.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password",txtPassword.getText().toString().trim()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            response=httpClient.execute(httpPost);
            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });

            if(response.equalsIgnoreCase("User Found")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this,"Login Success", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(LoginActivity.this, student_activity.class));
            }else{
                showAlert();
            }

        }catch(Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void showAlert(){
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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


    public void next()
    {
        final Context context = this;
        next = (Button) findViewById(R.id.cmdRegister);
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(context,Register.class);
                    startActivity(intent);
            }
        });
    }


}



