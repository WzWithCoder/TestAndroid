package com.example.wangzheng.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wangzheng.R;
import com.example.wangzheng.common.BaseRecyclerAdapter;
import com.example.wangzheng.widget.multi_input.MultiPanelLayout;
import com.example.wangzheng.widget.multi_input.ListenKeyboardLayout;
import com.example.wangzheng.widget.multi_input.Toolkit;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView      mContentRyv;
    private EditText          mInputEdit;
    private MultiPanelLayout mPanelLayout;
    private TextView          mSendButton;
    private CheckBox          mSwitchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_layout);

        mContentRyv = findViewById(R.id.content_ryv);
        mInputEdit = findViewById(R.id.input_container);
        mPanelLayout = findViewById(R.id.panel_root);
        mSendButton = findViewById(R.id.send_button);
        mSwitchButton = findViewById(R.id.switch_input);


        mContentRyv.setLayoutManager(new LinearLayoutManager(this));
        mContentRyv.setAdapter(mAdapter);


        mSwitchButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(
                            CompoundButton v, boolean isChecked) {
                        mPanelLayout.switchPanel(isChecked ?
                                View.VISIBLE : View.GONE, mInputEdit);
                    }
                });

        mInputEdit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mSwitchButton.setChecked(false);
                }
                return false;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = mInputEdit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    mAdapter.add(text);
                    mInputEdit.setText("");
                    mContentRyv.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });

        mPanelLayout.setSwitchListener(
                new ListenKeyboardLayout.SwitchListener() {
            public void onSwitch(boolean isShow) {
                mContentRyv.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
        Toolkit.listenKeyboard(this, bool-> {
            View view = getCurrentFocus();
            if (!bool && view != null) {
                view.clearFocus();
            }
        });

        mContentRyv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mInputEdit.clearFocus();
                    mPanelLayout.cancel(mInputEdit);
                }
                return false;
            }
        });
        for (int i = 0; i < 40; i++) {
            mAdapter.add(i+"");
        }
    }

    BaseRecyclerAdapter<String> mAdapter = new BaseRecyclerAdapter<String>() {
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, String data) {
            TextView tv = (TextView) viewHolder.itemView;
            tv.setText(data);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RecyclerView.ViewHolder(new TextView(viewGroup.getContext())) {
            };
        }
    };


    // 当屏幕分屏/多窗口变化时回调
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        //KPSwitchConflictUtil.onMultiWindowModeChanged(isInMultiWindowMode);
    }
}