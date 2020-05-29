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


public class ParentOrSibling extends Activity {
    Button back;
    Button next;

    @Override
    public void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_parent_or_sibling);
        backToRegister();
        next();
    }

    public void backToRegister()
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

    public void next()
    {
        final Context contextNext = this;
        next = (Button) findViewById(R.id.cmdNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contextNext,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
