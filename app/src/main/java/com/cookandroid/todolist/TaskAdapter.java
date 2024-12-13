package com.cookandroid.todolist;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// RecyclerView의 데이터를 관리
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private static Context context;
    private static Cursor cursor;
    private static MainActivity.myDBHelper dbHelper;

    public TaskAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo_list, parent, false);
        dbHelper = new MainActivity.myDBHelper(context);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (cursor != null && !cursor.isClosed() && cursor.moveToPosition(position)) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String dueDateTime = cursor.getString(cursor.getColumnIndexOrThrow("due_datetime"));
            boolean isComplete = cursor.getInt(cursor.getColumnIndexOrThrow("complete_status")) == 1;

            holder.tvTodoItem.setText(title);
            holder.tvTimeStamp.setText(dueDateTime);
            holder.cbCheck.setChecked(isComplete);
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void updateCursor(Cursor newCursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public void closeCursor() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public void changeCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close(); // 이전 Cursor 닫기
        }
        cursor = newCursor; // 새로운 Cursor 할당
        notifyDataSetChanged(); // UI 갱신
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTodoItem, tvTimeStamp;
        CheckBox cbCheck;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTodoItem = itemView.findViewById(R.id.tvTodoItem);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            cbCheck = itemView.findViewById(R.id.cbCheck);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curpos = getAdapterPosition();

                    String[] strChoiceItems = {"수정", "삭제"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("원하는 작업을 선택해 주세요");

                    if (cursor != null && cursor.moveToPosition(curpos)) {
                        // 데이터 가져오기
                        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                        String dueDateTime = cursor.getString(cursor.getColumnIndexOrThrow("due_datetime"));
                        boolean isComplete = cursor.getInt(cursor.getColumnIndexOrThrow("complete_status")) == 1;
                        String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
                        builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {

                                if (position == 0) {
                                    //수정하기
                                    Toast.makeText(context, "수정", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(itemView.getContext(), EditTodoActivity.class);
                                    intent.putExtra("editMode", "edit"); // 문자열로 editMode 전달
                                    intent.putExtra("id", id); // task_id 전달
                                    intent.putExtra("title", title);
                                    intent.putExtra("content", content);
                                    intent.putExtra("dueDateTime", dueDateTime);
                                    intent.putExtra("isComplete", isComplete);
                                    itemView.getContext().startActivity(intent);
                                    notifyDataSetChanged();
                                } else if (position == 1) {
                                    //삭제하기
                                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    dbHelper.deleteTask(id);
                                    // Cursor 새로고침
                                    Cursor newCursor = dbHelper.getTasks("DefaultUser");
                                    TaskAdapter.this.changeCursor(newCursor);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
    }
}
