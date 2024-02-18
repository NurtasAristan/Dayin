package com.example.dayin;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MethodsFragment extends Fragment {
    ArrayList<Method> methods;
    private ChipGroup chipGroup;
    private RecyclerView recyclerView;
    private MethodsRVAdapter adapter;
    private FloatingActionButton addMethodButton;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_methods, container, false);
        chipGroup = view.findViewById(R.id.chipGroup);
        recyclerView = view.findViewById(R.id.recyclerviewMethods);
        addMethodButton = view.findViewById(R.id.addMethodButton);

        MethodsRVAdapter.OnMethodClickListener methodClickListener = (method, position) -> {
            Intent intent = new Intent(getContext(), MethodActivity.class);
            intent.putExtra("id", method.getId());
            intent.putExtra("name", method.getName());
            intent.putExtra("category_id", method.getCategory_id());
            intent.putExtra("description", method.getDescription());
            startActivity(intent);
        };

        databaseHelper = new DatabaseHelper(getContext());
        methods = listMethods(chipGroup.getCheckedChipId());
        adapter = new MethodsRVAdapter(methods, methodClickListener);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            methods = listMethods(chipGroup.getCheckedChipId());
            adapter.setMethods(methods);
            adapter.notifyDataSetChanged();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        addMethodButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MethodActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private ArrayList<Method> listMethods(int category_chip_id) {

        ArrayList<Method> methods = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_METHODS;
        if (category_chip_id==R.id.chipHome) {
            selectQuery += " WHERE " + DatabaseHelper.COLUMN_METHODS_CATEGORY_ID + " = 2";
        } else if (category_chip_id==R.id.chipEducation) {
            selectQuery += " WHERE " + DatabaseHelper.COLUMN_METHODS_CATEGORY_ID + " = 3";
        } else if (category_chip_id==R.id.chipWork) {
            selectQuery += " WHERE " + DatabaseHelper.COLUMN_METHODS_CATEGORY_ID + " = 4";
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                int category_id = Integer.parseInt(cursor.getString(2));
                String description = cursor.getString(3);
                methods.add(new Method(id,name,category_id,description));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return methods;
    }

}
