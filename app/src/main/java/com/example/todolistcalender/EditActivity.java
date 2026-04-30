package com.example.todolistcalender;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    private String selectDate;
    private ListView lvEditList;
    private ArrayList<String> todoList;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper _helper;
    private EditText etEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectDate = getIntent().getStringExtra("date");
        TextView tvEditDate = findViewById(R.id.tvEditDate);
        tvEditDate.setText(selectDate + "のTODOリスト");
        lvEditList = findViewById(R.id.lvEditList);
        todoList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
        lvEditList.setAdapter(adapter);
        etEdit = findViewById(R.id.etEdit);
        _helper = new DatabaseHelper(EditActivity.this);
        selectDateTodo();



        //長押しでリストとデータベースから削除
        lvEditList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String deleteTodo = todoList.get(position);
                todoList.remove(position);
                adapter.notifyDataSetChanged();
                SQLiteDatabase connection = _helper.getWritableDatabase();
                SQLiteStatement stmt = connection.compileStatement("DELETE FROM todolist WHERE date = ? AND todo = ?");
                stmt.bindString(1, selectDate);
                stmt.bindString(2, deleteTodo);
                stmt.executeUpdateDelete();
                connection.close();
                return true;
            }
        });

    }
    public void onButtonBack(View view) {
        Intent intent = new Intent();
        intent.putExtra("date", selectDate);
        setResult(RESULT_OK);
        finish();
    }

    public void onButtonEdit(View view) {
        String todo = etEdit.getText().toString();
        todoList.add(todo);
        adapter.notifyDataSetChanged();
        etEdit.setText("");
        SQLiteDatabase connection = _helper.getWritableDatabase();
        SQLiteStatement stmt = connection.compileStatement("INSERT INTO todolist (date, todo) VALUES (?, ?)");
        stmt.bindString(1, selectDate);
        stmt.bindString(2, todo);
        stmt.executeInsert();
        connection.close();
    }

    public void selectDateTodo() {
        if (selectDate == null) {
            return;
        }
        SQLiteDatabase connection = _helper.getReadableDatabase();
        Cursor cursor = connection.rawQuery("SELECT * FROM todolist WHERE date = ?", new String[]{selectDate});
        todoList.clear();
        while (cursor.moveToNext()) {
            int idxDate = cursor.getColumnIndex("date");
            int idxTodo = cursor.getColumnIndex("todo");
            String date = cursor.getString(idxDate);
            String todo = cursor.getString(idxTodo);
            todoList.add(todo);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
        connection.close();
    }


}
