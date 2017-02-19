package pictures.newsin.athousandwords;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewSourcesActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    private DragListView listView;
    private ItemAdapter adapter;
    private ArrayList<Pair<Long, String>> itemArray;

    public void constructItemArray() {
        itemArray = new ArrayList<>();
        for (int i = 0; i < HomepageActivity.newsList.size(); i++) {
            String s = HomepageActivity.newsList.get(i);
            itemArray.add(new Pair<>(Long.valueOf(s.hashCode()), s));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sources);
        buildList();
        findViewById(R.id.add_source_button).setOnClickListener(this);
    }

    public void buildList() {
        listView = (DragListView) findViewById(R.id.sources_list);
        listView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                List<Pair<Long, String>> itemList = adapter.getItemList();
                for(int i=0; i<itemList.size(); i++) {
                    HomepageActivity.newsList.set(i, itemList.get(i).second);
                }
            }
        });
        listView.setLayoutManager(new LinearLayoutManager(this));
        constructItemArray();
        adapter = new ItemAdapter(itemArray, R.layout.sourcelist, R.id.label, false);
        adapter.setItemListener(new ItemAdapter.ItemListener() {
            @Override
            public void onItemClicked(View view) {
                final String news = ((TextView) view).getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(NewSourcesActivity.this);
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
                        HomepageActivity.newsList.remove(news);
                        constructItemArray();
                        adapter.setItemList(itemArray);
                    }
                });
                builder.show();
            }

            @Override
            public void onItemLongClicked(View view) {
            }
        });
        listView.setAdapter(adapter, false);
        listView.setCanDragHorizontally(false);
        listView.setCustomDragItem(null);
    }


    public void addSource(View v) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
//        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select a Source to Add:-");

        final Object[] sourceList = HomepageActivity.newsKeys.keySet().toArray();
        Arrays.sort(sourceList);
        final CharSequence[] choices = new CharSequence[sourceList.length];
        for(int i=0; i<sourceList.length; i++) {
            choices[i] = ((String)sourceList[i]);
        }

        final boolean[] selectedItems = new boolean[sourceList.length];

        for(int i=0; i<sourceList.length; i++) {
            if(HomepageActivity.newsList.contains(sourceList[i])) {
                selectedItems[i] = true;
            }
        }

        final ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(this,
                android.R.layout.select_dialog_singlechoice, sourceList);

        AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("Select a Source to Add:")
        .setMultiChoiceItems(choices, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    selectedItems[indexSelected] = true;
                } else if (selectedItems[indexSelected]) {
                    // Else, if the item is already in the array, remove it
                    selectedItems[indexSelected] = false;
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                for(int i = 0; i<sourceList.length; i++) {
                    if(selectedItems[i]) {
                        if(!HomepageActivity.newsList.contains(sourceList[i]))
                            HomepageActivity.newsList.add((String) sourceList[i]);
                    }else {
                        if(HomepageActivity.newsList.contains(sourceList[i]))
                            HomepageActivity.newsList.remove(sourceList[i]);
                    }
                }
                constructItemArray();
                adapter.setItemList(itemArray);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create();

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        addSource(v);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        HomepageActivity.saveNewsList(NewSourcesActivity.this);
    }
}
