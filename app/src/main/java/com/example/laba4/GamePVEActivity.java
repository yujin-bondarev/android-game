package com.example.laba4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePVEActivity extends AppCompatActivity {
    private Button[] cells = new Button[9];
    private TextView tvTurn;
    private ProgressBar progressAI;
    private Button btnRestart, btnBack;

    private boolean isPlayerTurn = true;
    private char[] board = new char[9];
    private boolean gameActive = true;

    private final Handler handler = new Handler();
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_pve);

        // 🔹 1. Инициализация UI
        tvTurn = findViewById(R.id.tvTurn);
        progressAI = findViewById(R.id.progressAI);
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
            cells[i].setOnClickListener(v -> onPlayerMove(index));
        }

        btnRestart.setOnClickListener(v -> resetGame());
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(GamePVEActivity.this, StrtScreenActivity.class));
            finish();
        });

        // 🔹 4. Только теперь — загрузка сохранений!
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String savedBoard = prefs.getString("board", null);

        if (savedBoard != null && "PVE".equals(prefs.getString("mode", null))) {
            board = savedBoard.toCharArray();
            isPlayerTurn = prefs.getBoolean("isPlayerTurn", true);
            gameActive = prefs.getBoolean("gameActive", true);
            restoreBoard(); // 👈 теперь безопасно!
            tvTurn.setText(isPlayerTurn ? "Ход: Игрок" : "Ход: ИИ");
        } else {
            board = new char[9];
            isPlayerTurn = true;
            gameActive = true;
            tvTurn.setText("Ход: Игрок");
        }
    }

    private void onPlayerMove(int index) {
        if (!gameActive || !isPlayerTurn || board[index] != '\0') return;

        board[index] = 'X';
        cells[index].setText("X");
        cells[index].setEnabled(false);

        if (checkWin('X')) {
            tvTurn.setText("Победа игрока!");
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

        isPlayerTurn = false;
        tvTurn.setText("Ход: ИИ");
        progressAI.setVisibility(View.VISIBLE);

        handler.postDelayed(this::makeAIMove, 800);
    }

    private void makeAIMove() {
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == '\0') emptyCells.add(i);
        }
        if (emptyCells.isEmpty()) return;

        int choice = findBestMove();
        if (choice == -1) choice = emptyCells.get(random.nextInt(emptyCells.size()));

        board[choice] = 'O';
        cells[choice].setText("O");
        cells[choice].setEnabled(false);

        progressAI.setVisibility(View.GONE);

        if (checkWin('O')) {
            tvTurn.setText("ИИ победил!");
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

        isPlayerTurn = true;
        tvTurn.setText("Ход: Игрок");
    }

    // Новый метод — "умная логика"
    private int findBestMove() {
        int[][] winPositions = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };

        // 1️⃣ Проверяем, может ли ИИ выиграть
        for (int[] pos : winPositions) {
            int a = pos[0], b = pos[1], c = pos[2];
            if (board[a]=='O' && board[b]=='O' && board[c]=='\0') return c;
            if (board[a]=='O' && board[c]=='O' && board[b]=='\0') return b;
            if (board[b]=='O' && board[c]=='O' && board[a]=='\0') return a;
        }

        // 2️⃣ Проверяем, не выигрывает ли игрок — блокируем
        for (int[] pos : winPositions) {
            int a = pos[0], b = pos[1], c = pos[2];
            if (board[a]=='X' && board[b]=='X' && board[c]=='\0') return c;
            if (board[a]=='X' && board[c]=='X' && board[b]=='\0') return b;
            if (board[b]=='X' && board[c]=='X' && board[a]=='\0') return a;
        }

        // 3️⃣ Если центр свободен — берём его
        if (board[4] == '\0') return 4;

        // 4️⃣ Если свободен угол — берём угол
        int[] corners = {0, 2, 6, 8};
        for (int corner : corners) {
            if (board[corner] == '\0') return corner;
        }

        // 5️⃣ Иначе случайная клетка
        return -1;
    }


    private boolean checkWin(char player) {
        int[][] winPositions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
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
            cells[i].setBackgroundTintList(ContextCompat. getColorStateList (this, R.color. win_color ));
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
        isPlayerTurn = true;
        gameActive = true;
        tvTurn.setText("Ход: Игрок");
        progressAI.setVisibility(View.GONE);

        for (Button cell : cells) {
            cell.setText("");
            cell.setEnabled(true);
            cell.setBackgroundTintList(ContextCompat. getColorStateList (this, R.color. game_cell_default_bg )); // Если вы добавили color/game_cell_default_bg
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

        tvTurn.setText(isPlayerTurn ? "Ход: Игрок" : "Ход: ИИ");
        progressAI.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", board);
        outState.putBoolean("isPlayerTurn", isPlayerTurn);
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
                .putString("mode", "PVE")
                .putString("board", new String(board))
                .putBoolean("isPlayerTurn", isPlayerTurn)
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
