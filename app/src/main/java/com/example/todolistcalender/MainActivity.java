package com.example.todolistcalender;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String selectDate;
    private ListView lvTodo;
    private DatabaseHelper _helper;

    private ArrayList<String> todoList;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lvTodo = findViewById(R.id.lvTodo);
        todoList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoList);
        lvTodo.setAdapter(adapter);
        _helper = new DatabaseHelper(MainActivity.this);


        //カレンダーのリスナーを設定
        CalendarView cvCalender = findViewById(R.id.cvCalender);
        cvCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                selectDateTodo();
            }
        });
        selectDateTodo();

    }

    //カレンダーの日付のTODOリストをデータベースから取得してリストビューに表示
    private void selectDateTodo() {
        if (selectDate == null ){
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

    //ボタンをタップしたらEditActivityに遷移
    public void onButtonAdd(View view) {
        if (selectDate == null) {
            Toast.makeText(this, "日付を選択してください", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("date", selectDate);
        startActivityForResult(intent,1);
    }
}