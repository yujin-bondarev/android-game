package com.example.laba4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class GamePVPActivity extends AppCompatActivity {
    private Button[] cells = new Button[9];
    private TextView tvTurn;
    private Button btnRestart, btnBack;

    private boolean isPlayerXTurn = true;
    private char[] board = new char[9]; // 'X', 'O', или '\0'
    private boolean gameActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_pvp);

        // 🔹 1. Инициализация UI
        tvTurn = findViewById(R.id.tvTurn);
        btnRestart = findViewById(R.id.btnRestart);
        btnBack = findViewById(R.id.btnBack);

        // 🔹 2. Привязка всех 9 кнопок
        cells[0] = findViewById(R.id.btn0);
        cells[1] = findViewById(R.id.btn1);
        cells[2] = findViewById(R.id.btn2);
        cells[3] = findViewById(R.id.btn3);
        cells[4] = findViewById(R.id.btn4);
        cells[5] = findViewById(R.id.btn5);
        cells[6] = findViewById(R.id.btn6);
        cells[7] = findViewById(R.id.btn7);
        cells[8] = findViewById(R.id.btn8);

        // 🔹 3. Установка обработчиков кнопок
        for (int i = 0; i < 9; i++) {
            final int index = i;
            cells[i].setOnClickListener(v -> onCellClicked(index));
        }

        btnRestart.setOnClickListener(v -> resetGame());
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(GamePVPActivity.this, StrtScreenActivity.class));
            finish();
        });

        // 🔹 4. Только теперь — загрузка сохранений!
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String savedBoard = prefs.getString("board", null);

        if (savedBoard != null && "PVP".equals(prefs.getString("mode", null))) {
            board = savedBoard.toCharArray();
            isPlayerXTurn = prefs.getBoolean("isPlayerXTurn", true);
            gameActive = prefs.getBoolean("gameActive", true);
            restoreBoard(); // 👈 теперь безопасно!
            tvTurn.setText(isPlayerXTurn ? "Ход: Игрок" : "Ход: ИИ");
        } else {
            board = new char[9];
            isPlayerXTurn = true;
            gameActive = true;
            tvTurn.setText("Ход: Игрок");
        }
    }

    private void onCellClicked(int index) {
        if (!gameActive || board[index] != '\0') return;

        char symbol = isPlayerXTurn ? 'X' : 'O';
        board[index] = symbol;
        cells[index].setText(String.valueOf(symbol));
        cells[index].setEnabled(false);

        if (checkWin(symbol)) {
            tvTurn.setText("Победа: " + symbol);
            gameActive = false;
            clearSavedGame();
            return;
        }

        if (isBoardFull()) {
            tvTurn.setText("Ничья!");
            gameActive = false;
            clearSavedGame();
            return;
        }

        isPlayerXTurn = !isPlayerXTurn;
        tvTurn.setText("Ход: " + (isPlayerXTurn ? "Игрок X" : "Игрок O"));
    }

    private boolean checkWin(char player) {
        int[][] winPositions = {
                {0,1,2}, {3,4,5}, {6,7,8},
                {0,3,6}, {1,4,7}, {2,5,8},
                {0,4,8}, {2,4,6}
        };

        for (int[] pos : winPositions) {
            if (board[pos[0]] == player &&
                    board[pos[1]] == player &&
                    board[pos[2]] == player) {
                highlightWin(pos);
                return true;
            }
        }
        return false;
    }

    private void highlightWin(int[] pos) {
        for (int i : pos) {
            cells[i].setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.win_color)
            );
        }
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == '\0') return false;
        }
        return true;
    }

    private void resetGame() {
        clearSavedGame();
        board = new char[9];
        isPlayerXTurn = true;
        gameActive = true;
        tvTurn.setText("Ход: Игрок X");

        for (Button cell : cells) {
            cell.setText("");
            cell.setEnabled(true);
            cell.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.game_cell_default_bg)
            );
        }
    }

    private void restoreBoard() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == 'X' || board[i] == 'O') {
                cells[i].setText(String.valueOf(board[i]));
                cells[i].setEnabled(false);
            } else {
                cells[i].setText("");
                cells[i].setEnabled(true);
            }
        }

        tvTurn.setText("Ход: " + (isPlayerXTurn ? "Игрок X" : "Игрок O"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", board);
        outState.putBoolean("isPlayerXTurn", isPlayerXTurn);
        outState.putBoolean("gameActive", gameActive);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGame();
    }

    private void saveGame() {
        getSharedPreferences("game_data", MODE_PRIVATE)
                .edit()
                .putString("mode", "PVP")
                .putString("board", new String(board))
                .putBoolean("isPlayerXTurn", isPlayerXTurn)
                .putBoolean("gameActive", gameActive)
                .apply();
    }

    private void clearSavedGame() {
        getSharedPreferences("game_data", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }


}
