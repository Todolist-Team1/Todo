package com.cookandroid.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditTodoActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDeadline;
    private Spinner spImportance;
    private Button btnSave;
    private MainActivity.myDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        // UI 요소 초기화
        etTitle = findViewById(R.id.etTodoTitle);
        etDescription = findViewById(R.id.etTodoContent);
        etDeadline = findViewById(R.id.etDeadline);
        spImportance = findViewById(R.id.spImportance);
        btnSave = findViewById(R.id.btnSave);

        // DBHelper 초기화
        dbHelper = new MainActivity.myDBHelper(this);

        // 저장 버튼 클릭 이벤트 처리
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
}

    private void saveTask() {
        // 사용자 입력 가져오기
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String importanceStr = spImportance.getSelectedItem().toString();
        int importance = parsePriority(importanceStr);

        if (title.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "제목과 마감일은 필수 입력 항목입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // SQLite 데이터 삽입
        try {
            dbHelper.insertTask("DefaultUser", title, description, deadline, importance);
            Toast.makeText(this, "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        } catch (Exception e) {
            Toast.makeText(this, "할 일 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private int parsePriority(String priorityStr) {
        switch (priorityStr) {
            case "1순위":
                return 1;
            case "2순위":
                return 2;
            case "3순위":
                return 3;
            case "4순위":
                return 4;
            default:
                return 0; // 기본값
        }
    }
}