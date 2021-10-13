package com.tud.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = this.getIntent();
        String username = intent.getStringExtra("USERNAME");
        setContentView(R.layout.activity_list);

        TextView greetingDisplay = findViewById(R.id.greetingsLabel);

        greetingDisplay.setText(username);
    }
}