package com.example.laba4;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        // Обработка нажатия кнопки "Назад"
        findViewById(R.id.btnBackFromAbout).setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, StrtScreenActivity.class);
            startActivity(intent);
            finish(); // чтобы не оставлять активность в стеке
        });
    }
}