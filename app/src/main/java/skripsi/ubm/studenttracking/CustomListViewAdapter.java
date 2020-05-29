package skripsi.ubm.studenttracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Leon on 22/07/2015.
 */
public class CustomListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public CustomListViewAdapter(Context context,
                                 ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView kode_matkul;
        TextView title;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        kode_matkul = (TextView) itemView.findViewById(R.id.kode_matkul);
        title = (TextView) itemView.findViewById(R.id.title);

        // Capture position and set results to the TextViews
        kode_matkul .setText(resultp.get("kode_matkul"));
        title.setText(resultp.get("title"));

        return itemView;
    }
}