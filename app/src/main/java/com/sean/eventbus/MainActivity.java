package com.sean.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sean.eventbus.core.SeanEventBus;
import com.sean.eventbus.core.Subscribe;
import com.sean.eventbus.core.SubscribeMode;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EventBus.getDefault().register(this);
        SeanEventBus.getDefault().register(this);

        textView = (TextView) findViewById(R.id.tv);
    }

    public void change(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }


    @Subscribe(SUBSCRIBE_MODE = SubscribeMode.Main)
    public void receive(Friend friend){
//        textView.setText(friend.toString() + "==" + Thread.currentThread().getName());
        Toast.makeText(this, friend.toString(), Toast.LENGTH_SHORT).show();
    }

//    @Subscribe
//    public void receive(String s){
//        textView.setText(s);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        SeanEventBus.getDefault().unRegister(this);
    }
}

