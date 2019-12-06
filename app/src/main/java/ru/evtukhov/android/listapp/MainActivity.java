package ru.evtukhov.android.listapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences TxtSharedPref;
    private static String LARGE_TEXT = "large_text";
    private List<Map<String, String>> contentList;
    private  BaseAdapter listContentAdapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSaveInSharedPreferences();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initList();
        initSwipeToRefresh();
    }

    @NonNull
    private SimpleAdapter createAdapter(String[] stringsTxt) {
        contentList = new ArrayList<>();
        prepareAdapterContent(stringsTxt);
        return new SimpleAdapter(this, contentList, R.layout.list, new String[]{"text","length"}, new int[]{R.id.textView, R.id.textViewNumberOfChars});
    }

    @NonNull
    private List<Map<String, String>> prepareAdapterContent(String[] text) {
        Map<String, String> mapForList;
        for (String value: text) {
            mapForList = new HashMap<>();
            mapForList.put("text",value);
            mapForList.put("length",Integer.toString(value.length()));
            contentList.add(mapForList);
        }
        return contentList;
    }

    private String[] prepareContent(){
        return TxtSharedPref.getString(LARGE_TEXT, "").split("\n\n");
    }

    private void initList() {
        ListView list = findViewById(R.id.list);
        listContentAdapter = createAdapter(prepareContent());
        list.setAdapter(listContentAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contentList.remove(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initSwipeToRefresh (){
        swipeLayout = findViewById(R.id.swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void updateList(){
        contentList.clear();
        prepareAdapterContent(prepareContent());
        listContentAdapter.notifyDataSetChanged();
    }

    private void checkSaveInSharedPreferences(){
        TxtSharedPref = getSharedPreferences("LargeTxt", MODE_PRIVATE);
        if (!TxtSharedPref.getString(LARGE_TEXT, "").equals(getString(R.string.large_text))){
            SharedPreferences.Editor myEditor = TxtSharedPref.edit();
            String LargeTxt = getString(R.string.large_text);
            myEditor.putString(LARGE_TEXT, LargeTxt);
            myEditor.apply();
        }
    }
}