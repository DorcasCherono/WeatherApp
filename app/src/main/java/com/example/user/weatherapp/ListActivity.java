package com.example.user.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView list;
    List<Summary> summary;
    ArrayAdapter<Summary> summaryArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = findViewById(R.id.toolbar);
        list = findViewById(R.id.list);
        list.setLongClickable(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        summary = SQLite.select().from(Summary.class).orderBy(Summary_Table.id, false).queryList();
        summaryArrayAdapter = new ArrayAdapter<>(ListActivity.this, android.R.layout.simple_list_item_1
                , summary);
        list.setAdapter(summaryArrayAdapter);
        list.setLongClickable(true);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                PopupMenu popupMenu = new PopupMenu(ListActivity.this, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_summary_item, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_delete:
                                Summary sum = summaryArrayAdapter.getItem(position);
                                if (sum != null) {
                                    sum.delete();

                                    summary.clear();
                                    summary.addAll(SQLite.select().from(Summary.class).orderBy(Summary_Table.id, false).queryList());
                                    summaryArrayAdapter.notifyDataSetChanged();

                                    Toast.makeText(ListActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }
}
