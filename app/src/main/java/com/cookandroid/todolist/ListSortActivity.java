package com.cookandroid.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListSortActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sort);

        MainActivity.myDBHelper dbHelper;
        Cursor cursor1,cursor2,cursor3,cursor4 ;

        RecyclerView rv1, rv2, rv3, rv4;
        TaskAdapter adapter1, adapter2, adapter3, adapter4;

        rv1 = findViewById(R.id.rvSortedList1);
        rv2 = findViewById(R.id.rvSortedList2);
        rv3 = findViewById(R.id.rvSortedList3);
        rv4 = findViewById(R.id.rvSortedList4);

        FloatingActionButton btnFin = findViewById(R.id.fin);


        dbHelper = new MainActivity.myDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor1 = db.rawQuery(
                "SELECT * FROM task WHERE (due_datetime BETWEEN datetime('now') AND datetime('now', '+7 days')) " +
                        "AND priority = '1' ORDER BY due_datetime ASC;",null);
        //중요하고 급한 일

        cursor2 = db.rawQuery(
                "SELECT * FROM task WHERE due_datetime > datetime('now', '+7 days') " +
                        "AND priority = '1' ORDER BY due_datetime ASC;",null);
        //중요하지만 급하지 않은 일

        cursor3 = db.rawQuery("SELECT * FROM task WHERE (due_datetime BETWEEN datetime('now') AND datetime('now', '+7 days')) " +
                "AND priority = '0' ORDER BY due_datetime ASC;",null);
        //중요하지 않지만 급한 일

        cursor4 = db.rawQuery("SELECT * FROM task WHERE due_datetime > datetime('now', '+7 days') " +
                "AND priority = '0' ORDER BY due_datetime ASC;",null);
        //중요하지도 급하지도 않은 일




        adapter1 = new TaskAdapter(this, cursor1);
        adapter2 = new TaskAdapter(this, cursor2);
        adapter3 = new TaskAdapter(this, cursor3);
        adapter4 = new TaskAdapter(this, cursor4);



        rv1.setAdapter(adapter1);
        rv1.setLayoutManager(new LinearLayoutManager(this));

        rv2.setAdapter(adapter2);
        rv2.setLayoutManager(new LinearLayoutManager(this));

        rv3.setAdapter(adapter3);
        rv3.setLayoutManager(new LinearLayoutManager(this));

        rv4.setAdapter(adapter4);
        rv4.setLayoutManager(new LinearLayoutManager(this));

        btnFin.setOnClickListener(view -> finish());


        //디버깅
        Log.d("Cursor1 Count", "cursor1 count: " + cursor1.getCount());
        Log.d("Cursor2 Count", "cursor2 count: " + cursor2.getCount());
        Log.d("Cursor3 Count", "cursor3 count: " + cursor3.getCount());
        Log.d("Cursor4 Count", "cursor4 count: " + cursor4.getCount());
        

    }
}