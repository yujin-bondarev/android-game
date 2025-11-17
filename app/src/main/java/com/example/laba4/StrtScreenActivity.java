package com.example.laba4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StrtScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.starting_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnWithAI).setOnClickListener(v -> {
            Intent intent = new Intent(StrtScreenActivity.this, GamePVEActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnWithFrnd).setOnClickListener(v -> {
            Intent intent = new Intent(StrtScreenActivity.this, GamePVPActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnContin).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
            String mode = prefs.getString("mode", null);

            if (mode == null) {
                Toast.makeText(this, "Нет сохранённой игры", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent;
            if ("PVE".equals(mode)) {
                intent = new Intent(this, GamePVEActivity.class);
            } else if ("PVP".equals(mode)) {
                intent = new Intent(this, GamePVPActivity.class);
            } else {
                Toast.makeText(this, "Данные повреждены", Toast.LENGTH_SHORT).show();
                return;
            }

            startActivity(intent);
        });

        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            Intent intent = new Intent(StrtScreenActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnFeedback).setOnClickListener(v -> {
            Intent intent = new Intent(StrtScreenActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

    }
}