package com.example.laba4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Форма обратной связи.
 * Содержит поля email и сообщения.
 * Проверяет корректность email.
 * Сохраняет данные в SharedPreferences.
 * Имитирует отправку с ProgressBar.
 */
public class FeedbackActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editMessage;
    private ProgressBar progressFeedback;
    private Button btnSend, btnBack;

    private static final String PREFS_NAME = "feedback_data";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);

        initViews();
        loadSavedData(); // загрузка ранее введённых данных при повороте или повторном входе

        btnSend.setOnClickListener(v -> handleSend());
        btnBack.setOnClickListener(v -> {
            saveData(); // сохраняем даже если не отправили
            finish();
        });
    }

    private void initViews() {
        editEmail = findViewById(R.id.editEmail);
        editMessage = findViewById(R.id.editMessage);
        progressFeedback = findViewById(R.id.progressFeedback);
        btnSend = findViewById(R.id.btnSendFeedback);
        btnBack = findViewById(R.id.btnBackFromFeedback);
    }

    /**
     * Загружает сохранённые данные из SharedPreferences
     */
    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = prefs.getString(KEY_EMAIL, "");
        String message = prefs.getString(KEY_MESSAGE, "");
        editEmail.setText(email);
        editMessage.setText(message);
    }

    /**
     * Сохраняет текущие данные в SharedPreferences
     */
    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_EMAIL, editEmail.getText().toString());
        editor.putString(KEY_MESSAGE, editMessage.getText().toString());
        editor.apply();
    }

    private void handleSend() {
        String email = editEmail.getText().toString().trim();
        String message = editMessage.getText().toString().trim();

        // Проверка корректности email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Пожалуйста, введите корректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите сообщение", Toast.LENGTH_SHORT).show();
            return;
        }

        // Показываем индикатор загрузки
        progressFeedback.setVisibility(android.view.View.VISIBLE);
        btnSend.setEnabled(false);

        // Имитация отправки (в реальном приложении — сетевой запрос)
        new Handler().postDelayed(this::onFeedbackSent, 1500);
    }

    private void onFeedbackSent() {
        // Скрываем прогресс, показываем успех
        progressFeedback.setVisibility(android.view.View.GONE);
        Toast.makeText(this, "Спасибо за отзыв!", Toast.LENGTH_LONG).show();

        // Очищаем и сохраняем пустые поля
        editEmail.setText("");
        editMessage.setText("");
        saveData();

        // Возвращаем кнопку в активное состояние
        btnSend.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData(); // сохраняем при выходе
    }
}