package com.example.yuvalarbek;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class GameOverActivity extends AppCompatActivity {

    private MaterialButton gameOver_BTN_restart;
    private MaterialButton gameOver_BTN_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        findViews();
        initViews();
    }

    private void findViews() {
        gameOver_BTN_restart = findViewById(R.id.gameOver_BTN_restart);
        gameOver_BTN_exit = findViewById(R.id.gameOver_BTN_exit);
    }

    private void initViews() {
        gameOver_BTN_restart.setOnClickListener(v -> restartGame());
        gameOver_BTN_exit.setOnClickListener(v -> exitGame());
    }

    private void restartGame() {
        Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void exitGame() {
        finishAffinity(); // Close all activities
    }
}
