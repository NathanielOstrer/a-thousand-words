package pictures.newsin.athousandwords;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.Arrays;

public class NewSourcesActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    private DragListView listView;
    private DragItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sources);
        buildList();
        findViewById(R.id.add_source_button).setOnClickListener(this);
    }

    public void buildList() {
        listView = (DragListView) findViewById(R.id.sources_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemArrayAdapter
        listView.setAdapter(adapter, false);
        listView.setOnItemClickListener(this);
    }


    public void addSource(View v) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
//        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select a Source to Add:-");

        Object[] sourceList = HomepageActivity.newsKeys.keySet().toArray();
        Arrays.sort(sourceList);

        final ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(this,
                android.R.layout.select_dialog_singlechoice, sourceList);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String news = (String)arrayAdapter.getItem(which);
                HomepageActivity.newsList.add(news);
                HomepageActivity.saveNewsList(NewSourcesActivity.this);
                adapter.notifyDataSetChanged();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onClick(View v) {
        addSource(v);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final String news = HomepageActivity.newsList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builderInner.setMessage(news);
        builder.setTitle(news);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                HomepageActivity.newsList.remove(position);
                HomepageActivity.saveNewsList(NewSourcesActivity.this);
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }
}
