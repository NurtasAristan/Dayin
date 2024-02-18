package com.example.dayin;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Objects;

//ChatGPT
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MethodActivity extends AppCompatActivity {
    Method method;
    MaterialToolbar materialToolbar;
    EditText editMethodName, editMethodDescription;
    EditText GPT;
    Spinner categorySpinner;
    Button saveMethodButton, sendToGPT;

    DatabaseHelper helper;
    SQLiteDatabase db;

    //ChatGPT
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_method);
        materialToolbar = findViewById(R.id.materialToolbar);
        editMethodName = findViewById(R.id.editMethodName);
        editMethodDescription = findViewById(R.id.editMethodDescription);
        GPT = findViewById(R.id.GPT);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveMethodButton = findViewById(R.id.saveMethodButton);
        sendToGPT = findViewById(R.id.sendToGPT);

        helper = new DatabaseHelper(this);
        db = helper.getWritableDatabase();
        Bundle arguments = getIntent().getExtras();

        //ChatGPT
        apiService = ApiClient.getClient().create(ApiService.class);

        if (arguments!=null) {
            getMethod(arguments.getInt("id"));

            editMethodName.setText(method.getName());
            editMethodDescription.setText(method.getDescription());
            GPT.setText(method.getGpt());
        } else {
            method = new Method(0, "", 1, "", "");
        }

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listCategories());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(method.getCategory_id()-1);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);
                method.setCategory_id(category.getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        categorySpinner.setOnItemSelectedListener(itemSelectedListener);

        materialToolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete) {
                db.delete(DatabaseHelper.TABLE_METHODS, "id = ?", new String[]{String.valueOf(method.getId())});
                goHome();
                return true;
            }
            return false;
        });

        //ChatGPT
        sendToGPT.setOnClickListener(v -> {
            String question = editMethodDescription.getText().toString();
            if (!question.isEmpty()) {
                sendChatRequest(question);
            }
        });

        saveMethodButton.setOnClickListener(v -> {
            method.setName(editMethodName.getText().toString());
            method.setDescription(editMethodDescription.getText().toString());
            method.setGpt(GPT.getText().toString());
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_METHODS_NAME, method.getName());
            cv.put(DatabaseHelper.COLUMN_METHODS_CATEGORY_ID, method.getCategory_id());
            cv.put(DatabaseHelper.COLUMN_METHODS_DESCRIPTION, method.getDescription());
            cv.put(DatabaseHelper.COLUMN_METHODS_GPT, method.getGpt());

            if (method.getId()>0) {
                db.update(DatabaseHelper.TABLE_METHODS, cv,
                        DatabaseHelper.COLUMN_METHODS_ID + " = ?",
                        new String[]{String.valueOf(method.getId())});
            } else {
                db.insert(DatabaseHelper.TABLE_METHODS, null, cv);
            }
            goHome();
        });
    }

    public void getMethod(int method_id) {
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_METHODS + " WHERE " +
                DatabaseHelper.COLUMN_METHODS_ID + " = " + method_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        method = new Method();
        method.setId(Integer.parseInt(cursor.getString(0)));
        method.setName(cursor.getString(1));
        method.setCategory_id(Integer.parseInt(cursor.getString(2)));
        method.setDescription(cursor.getString(3));
        method.setGpt(cursor.getString(4));
        cursor.close();
    }

    public ArrayList<Category> listCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                categories.add(new Category(id,name));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    //ChatGPT
    private void sendChatRequest(String message) {
        String model = "gpt-3.5-turbo";
        Message[] messages = {
                new Message("user", message)
        };
        ChatRequest request = new ChatRequest(model, messages);

        Call<ChatResponse> call = apiService.getChatCompletion(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful()) {
                    ChatResponse chatResponse = response.body();
                    if (chatResponse != null) {
                        String answer = chatResponse.getResponse();
                        Log.d("ChatGPT Response", answer);
                        // Обработка полученного ответа
                        GPT.setText(answer);
                    }
                } else {
                    Log.e("ChatGPT Error", "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e("ChatGPT Error", "Request failed: " + t.getMessage());
            }
        });
    }

}
