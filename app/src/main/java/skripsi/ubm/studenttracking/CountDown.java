package skripsi.ubm.studenttracking;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CountDown extends Activity implements View.OnClickListener {
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    Button cmdCount;
    TextView txtCount;
    private final long startTime = 30000;
    private final long interval = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        txtCount = (TextView)findViewById(R.id.txtCount);
        cmdCount = (Button)findViewById(R.id.cmdCount);
        cmdCount.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_down, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        countDownTimer = new CountDownTimer(startTime,interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtCount.setText("Seconds Remaining : " + millisUntilFinished / 1000);
                if ((millisUntilFinished / 1000) == 25)
                {
                    txtCount.setText("Success");
                    countDownTimer.cancel();
                }
            }

            @Override
            public void onFinish() {
                txtCount.setText("Finish !");
            }
        }.start();
    }
}
