package com.cookandroid.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private myDBHelper dbHelper;
    private Cursor cursor;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.rvTodoList);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        dbHelper = new myDBHelper(this);
        cursor = dbHelper.getTasks("DefaultUser");

        adapter = new TaskAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add 버튼 클릭 시 새 작업 추가
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditTodoActivity.class);
            startActivity(intent);
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        // 데이터 새로 로드
        if (cursor != null && !cursor.isClosed()) {
            cursor.close(); // 이전 Cursor 닫기
        }

        cursor = dbHelper.getTasks("DefaultUser"); // 새로운 Cursor 가져오기
        adapter.changeCursor(cursor); // 어댑터에 새 Cursor 적용
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity가 종료되면 cursor 닫기
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }


    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 2);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // user Table 생성
            db.execSQL("CREATE TABLE user (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_name TEXT NOT NULL" +
                    ");");

            // task Table 생성
            db.execSQL("CREATE TABLE task (" +
                    "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "content TEXT, " +
                    "due_datetime DATETIME, " +
                    "priority INTEGER, " +
                    "complete_status INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                    ");");
             }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS user");
            db.execSQL("DROP TABLE IF EXISTS task");
            onCreate(db);
        }



        public void insertTask(String userName, String title, String content, String dueDate, String dueTime, int priority) {
            SQLiteDatabase db = this.getWritableDatabase();

            // user 추가 또는 기존 user_id 조회
            Cursor cursor = db.rawQuery("SELECT user_id FROM user WHERE user_name = ?", new String[]{userName});
            int userId;
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            } else {
                db.execSQL("INSERT INTO user (user_name) VALUES (?);", new Object[]{userName});
                Cursor newCursor = db.rawQuery("SELECT user_id FROM user WHERE user_name = ?", new String[]{userName});
                if (newCursor.moveToFirst()) {
                    userId = newCursor.getInt(0);
                } else {
                    throw new RuntimeException("User ID not found after insert");
                }
                newCursor.close();
            }
            cursor.close();

            // dueDate와 dueTime을 DATETIME 형식으로 변환
            String dueDateTime = dueDate + " " + dueTime + ":00";

            // task 추가
            db.execSQL("INSERT INTO task (user_id, title, content, due_datetime, priority) VALUES (?, ?, ?, ?, ?);",
                    new Object[]{userId, title, content, dueDateTime, priority});
        }

        // 데이터 조회
        public Cursor getTasks(String userName) {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery(
                    "SELECT t.task_id, t.title, t.content, t.due_datetime, t.priority, t.complete_status " +
                            "FROM task t INNER JOIN user u ON t.user_id = u.user_id " +
                            "WHERE u.user_name = ? " +
                            "ORDER BY t.due_datetime ASC;",
                    new String[]{userName}
            );
        }
    }
}
