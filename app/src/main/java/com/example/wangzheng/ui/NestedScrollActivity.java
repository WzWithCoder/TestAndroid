package com.example.wangzheng.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wangzheng.R;


public class NestedScrollActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nested_scroll_layout);

        RecyclerView rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new Holder(new TextView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Holder h = (Holder) holder;
                h.textView.setText("item " + position);
            }

            @Override
            public int getItemCount() {
                return 100;
            }

            class Holder extends RecyclerView.ViewHolder {
                TextView textView;

                public Holder(TextView tv) {
                    super(tv);
                    textView = tv;
                }
            }
        });
    }
}
