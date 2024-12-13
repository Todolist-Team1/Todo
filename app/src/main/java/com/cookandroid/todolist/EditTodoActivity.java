package com.cookandroid.todolist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.Intent;

public class EditTodoActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etDueDate, etDueTime;

    private Button btnSave, btnEdit;

    private CheckBox spImportance;

    private Calendar calendar;
    private MainActivity.myDBHelper dbHelper;

    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        // UI 요소 초기화
        etTitle = findViewById(R.id.etTodoTitle);
        etDescription = findViewById(R.id.etTodoContent);
        etDueDate = findViewById(R.id.etDueDate);
        etDueTime = findViewById(R.id.etDueTime);
        spImportance = findViewById(R.id.spImportance);
        btnSave = findViewById(R.id.btnSave);
        btnEdit = findViewById(R.id.btnEdit);

        calendar = Calendar.getInstance();calendar = Calendar.getInstance();

        // DBHelper 초기화
        dbHelper = new MainActivity.myDBHelper(this);

        // 날짜 선택
        etDueDate.setOnClickListener(v -> showDatePickerDialog());
        // 시간 선택
        etDueTime.setOnClickListener(v -> showTimePickerDialog());

        // 저장 버튼 클릭 이벤트 처리
        btnSave.setOnClickListener(v -> saveTask());

        btnEdit.setOnClickListener(v -> editTask());

        Intent intent = getIntent();
        String editMode = intent.getStringExtra("editMode"); // editMode는 문자열로 받기
        taskId = intent.getIntExtra("id", -1); // id 기본값으로 -1 사용
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String dueDateTime = intent.getStringExtra("dueDateTime");
        boolean isComplete = intent.getBooleanExtra("isComplete", false);

        if ("edit".equals(editMode) && taskId != -1) {
            etTitle.setText(title);
            etDescription.setText(content);

            // 날짜와 시간을 분리
            String[] dateTime = dueDateTime.split(" ");
            etDueDate.setText(dateTime[0]);
            etDueTime.setText(dateTime[1]);

            btnSave.setVisibility(View.INVISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
        }else if("save".equals(editMode)){
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.INVISIBLE);
        }
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePickerDialog() {
        new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateTimeField();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24시간제
        ).show();
    }

    private void updateDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDueDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateTimeField() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etDueTime.setText(timeFormat.format(calendar.getTime()));
    }

    private void saveTask() {
        // 사용자 입력 가져오기
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String dueTime = etDueTime.getText().toString().trim();
        boolean importanceStr = spImportance.isChecked();

        if (title.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요. (제목, 마감일시 필수)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 중요도를 정수로 변환
        int importance;

        if (importanceStr) {
            importance=1;
        }else{
            importance=0;
        }

        /*try {
            importance = parsePriority(importanceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "중요도를 올바르게 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        // SQLite 데이터 삽입
        try {
            dbHelper.insertTask("DefaultUser", title, description, dueDate, dueTime, importance);
            Toast.makeText(this, "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        } catch (Exception e) {
            Toast.makeText(this, "할 일 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void editTask() {
        // 사용자 입력 가져오기
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String dueTime = etDueTime.getText().toString().trim();
        boolean importanceStr = spImportance.isChecked();

        if (title.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요. (제목, 마감일시 필수)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 중요도를 파싱하여 정수로 변환
        // 중요도를 정수로 변환
        int importance;

        if (importanceStr) {
            importance=1;
        }else{
            importance=0;
        }

        /*
        try {
            importance = parsePriority(importanceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "중요도를 올바르게 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        // SQLite 데이터 삽입
        try {
            dbHelper.updateTask("DefaultUser", title, description, dueDate, dueTime, importance, taskId);
            Toast.makeText(this, "할 일이 수정되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        } catch (Exception e) {
            Toast.makeText(this, "할 일 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
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