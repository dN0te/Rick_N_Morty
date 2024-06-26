package com.example.yuvalarbek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity implements GameManager.ScoreListener, GameManager.LifeListener {

    private MaterialTextView main_LBL_score;
    private MaterialButton main_BTN_right;
    private MaterialButton main_BTN_left;

    private int[] mortyImages;
    private int[][] rickImages;

    private AppCompatImageView[] lifeImages;

    private GameManager gameManager;
    private int score = 0;
    private int life = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
        gameManager = new GameManager(this, mortyImages, rickImages);
        gameManager.setScoreListener(this);
        gameManager.setLifeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameManager.stopTimer(); // Stop the timer when the activity is destroyed
    }

    private void initViews() {
        main_LBL_score.setText(String.valueOf(score));
        main_BTN_right.setOnClickListener(v -> changeMortyPosition(1));
        main_BTN_left.setOnClickListener(v -> changeMortyPosition(-1));
    }

    private void findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_BTN_left = findViewById(R.id.main_BTN_left);

        mortyImages = new int[] {
                R.id.Main_Img_Morty_0_3,
                R.id.Main_Img_Morty_1_3,
                R.id.Main_Img_Morty_2_3,
                R.id.Main_Img_Morty_3_3
        };

        rickImages = new int[][] {
                { R.id.Main_IMG_Rick_0_0, R.id.Main_IMG_Rick_0_1, R.id.Main_IMG_Rick_0_2, R.id.Main_IMG_Rick_0_3 },
                { R.id.Main_IMG_Rick_1_0, R.id.Main_IMG_Rick_1_1, R.id.Main_IMG_Rick_1_2, R.id.Main_IMG_Rick_1_3 },
                { R.id.Main_IMG_Rick_2_0, R.id.Main_IMG_Rick_2_1, R.id.Main_IMG_Rick_2_2, R.id.Main_IMG_Rick_2_3 },
                { R.id.Main_IMG_Rick_3_0, R.id.Main_IMG_Rick_3_1, R.id.Main_IMG_Rick_3_2, R.id.Main_IMG_Rick_3_3 }
        };

        lifeImages = new AppCompatImageView[] {
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
    }

    private void changeMortyPosition(int direction) {
        if (direction == 1) {
            gameManager.moveMortyRight(this);
        } else if (direction == -1) {
            gameManager.moveMortyLeft(this);
        }
        updateMortyVisibility();
    }

    private void updateMortyVisibility() {
        for (int i = 0; i < mortyImages.length; i++) {
            AppCompatImageView imageView = findViewById(mortyImages[i]);
            imageView.setVisibility(gameManager.getMortyPosition() == i ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onScoreUpdated(int newScore) {
        main_LBL_score.setText(String.valueOf(newScore));
    }

    @Override
    public void onLifeUpdated(int newLife) {
        life = newLife;
        updateLifeUI();
        if (life <= 0) {
            startGameOverActivity();
        }
    }

    private void updateLifeUI() {
        for (int i = 0; i < lifeImages.length; i++) {
            lifeImages[i].setVisibility(i < life ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void startGameOverActivity() {
        Intent intent = new Intent(this, GameOverActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    public void refreshRickVisibility(int[][] rickMatrix) {
        for (int row = 0; row < GameManager.ROW_COUNT; row++) {
            for (int col = 0; col < GameManager.COL_COUNT; col++) {
                AppCompatImageView imageView = findViewById(rickImages[row][col]);
                imageView.setVisibility(rickMatrix[row][col] == 1 ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }
}
