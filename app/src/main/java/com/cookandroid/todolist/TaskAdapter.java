package com.cookandroid.todolist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// RecyclerView의 데이터를 관리
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private Cursor cursor;

    public TaskAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo_list, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (cursor != null && !cursor.isClosed() && cursor.moveToPosition(position)) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
            boolean isComplete = cursor.getInt(cursor.getColumnIndexOrThrow("complete_status")) == 1;

            holder.tvTodoItem.setText(title);
            holder.tvTimeStamp.setText(dueDate);
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

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTodoItem, tvTimeStamp;
        CheckBox cbCheck;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTodoItem = itemView.findViewById(R.id.tvTodoItem);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            cbCheck = itemView.findViewById(R.id.cbCheck);
        }
    }
}
