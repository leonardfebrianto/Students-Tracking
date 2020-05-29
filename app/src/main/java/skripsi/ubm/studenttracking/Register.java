package skripsi.ubm.studenttracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Register extends Activity {
    Button back;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addListenerOnButton2();
        backToLogin();
    }

    public void backToLogin()
    {
        final Context contextBack = this;
        back = (Button) findViewById(R.id.cmdBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
                Intent intent = new Intent(contextBack,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addListenerOnButton2()
    {
        final Context contextNext = this;
        next = (Button) findViewById(R.id.cmdNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contextNext,ParentOrSibling.class);
                startActivity(intent);
            }
        });
    }
}
