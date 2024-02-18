package com.example.dayin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MethodsRVAdapter extends RecyclerView.Adapter<MethodsRVAdapter.ViewHolder> {
    interface OnMethodClickListener{
        void onMethodClick(Method method, int position);
    }

    private OnMethodClickListener onClickListener;
    private ArrayList<Method> methods;

    MethodsRVAdapter(ArrayList<Method> methods, OnMethodClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.methods = methods;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.method_name);
        }
    }

    // +
    @NonNull
    @Override
    public MethodsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.method_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // +
    @Override
    public void onBindViewHolder(MethodsRVAdapter.ViewHolder viewHolder, final int position) {
        Method method = methods.get(position);
        viewHolder.view.setText(method.getName());

        viewHolder.itemView.setOnClickListener(v -> onClickListener.onMethodClick(method, position));
    }

    // +
    @Override
    public int getItemCount() {
        return methods.size();
    }

    public void setMethods(ArrayList<Method> newProjects) {
        this.methods = newProjects;
    }
}
