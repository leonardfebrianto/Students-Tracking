package skripsi.ubm.studenttracking;

/**
 * Created by Leon on 04/07/2015.
 */

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver
{
    static String NIM;
    static Context contexts;
    static int jArray;
    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String value = (mSharedPreference.getString("NIM","Default_Value"));
        NIM = value;
        contexts = context;
        new GetLength().execute();
    }

    class GetLength extends AsyncTask<String, String, String> {
        String kode_matkul,jam,hari,latitude,longitude,title,ruang,lantai,nama_mahasiswa,noHp_orangtua;
        JSONParser jParser = new JSONParser();
        ArrayList<String> nama_mahasiswa_list = new ArrayList<String>();
        ArrayList<String> noHp_orangtua_list = new ArrayList<String>();
        ArrayList<String> kode_matkul_list = new ArrayList<String>();
        ArrayList<String> title_list = new ArrayList<String>();
        ArrayList<String> ruang_list = new ArrayList<String>();
        ArrayList<String> lantai_list = new ArrayList<String>();
        ArrayList<String> latitude_list = new ArrayList<String>();
        ArrayList<String> longitude_list = new ArrayList<String>();
        ArrayList<String> jam_list = new ArrayList<String>();
        ArrayList<String> hari_list = new ArrayList<String>();
        String time[];
        PendingIntent pendingIntent;

        protected String doInBackground(String... params) {
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("username", NIM));
                JSONObject json = jParser.makeHttpRequest("http://studentstracking.hol.es/installation.php", "POST", param);
                JSONArray array = json.getJSONArray("length");
                jArray = array.length();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject row = array.getJSONObject(i);
                    nama_mahasiswa = row.getString("nama_user1");
                    noHp_orangtua = row.getString("nohp_user2");
                    kode_matkul = row.getString("kode_matkul");
                    hari = row.getString("hari");
                    latitude = row.getString("latitude");
                    longitude = row.getString("longitude");
                    lantai = row.getString("lantai");
                    ruang = row.getString("ruang");
                    jam = row.getString("jam");
                    title = row.getString("title");
                    nama_mahasiswa_list.add(nama_mahasiswa);
                    noHp_orangtua_list.add(noHp_orangtua);
                    kode_matkul_list.add(kode_matkul);
                    title_list.add(title);
                    ruang_list.add(ruang);
                    lantai_list.add(lantai);
                    latitude_list.add(latitude);
                    longitude_list.add(longitude);
                    jam_list.add(jam);
                    hari_list.add(hari);
                }

            } catch (Exception e) {
                System.out.println("Exception : " + e.getMessage());
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String weekDay;
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            Calendar calendars = Calendar.getInstance();
            weekDay = dayFormat.format(calendars.getTime());
            //Toast.makeText(contexts,"Day : " + weekDay,Toast.LENGTH_SHORT).show();
            for (int i=1; i< jArray; i++)
            {
                String jamx = jam_list.get(i);
                String hari = hari_list.get(i);
                String menit = jamx.substring(3, 5);
                String jams = jamx.substring(0, 2);
                String lantaix = lantai_list.get(i);
                String latitudex = latitude_list.get(i);
                String longitudex = longitude_list.get(i);
                String ruangx = ruang_list.get(i);
                String nama_mahasiswax = nama_mahasiswa_list.get(i);
                String noHp_orangtuax = noHp_orangtua_list.get(i);
                String nama_matkulx = title_list.get(i);
                String kode_matkulx = kode_matkul_list.get(i);
                int jamz = Integer.parseInt(jams);
                int menitz = Integer.parseInt(menit);
                if (weekDay.equals(hari))
                {
                    Intent intent = new Intent(contexts, Service.class);
                    intent.putExtra("nama_mahasiswa",nama_mahasiswax);
                    intent.putExtra("noHP_orangtua",noHp_orangtuax);
                    intent.putExtra("ruang",ruangx);
                    intent.putExtra("lantai",lantaix);
                    intent.putExtra("latitude",latitudex);
                    intent.putExtra("longitude",longitudex);
                    intent.putExtra("title",nama_matkulx);
                    intent.putExtra("kode_matkul",kode_matkulx);
                    intent.putExtra("jam",jamx);
                    Toast.makeText(contexts, "Jam : " + jamz, Toast.LENGTH_SHORT).show();
                    Toast.makeText(contexts, "Menit : " + menitz, Toast.LENGTH_SHORT).show();
                    pendingIntent = PendingIntent.getService(contexts, i, intent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager manager = (AlarmManager) contexts.getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,jamz);
                    calendar.set(Calendar.MINUTE, menitz);
                    calendar.set(Calendar.SECOND, 00);
                    manager.setExact(manager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                }
            }
        }
    }
}