package com.example.yuvalarbek;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;
import java.util.Random;

public class GameManager {
    private final int[] mortyImages; // Using int array for Morty
    private final int[][] rickMatrix; // Matrix to hold Rick's visibility states
    private int mortyPosition;
    private int score;
    private int life;
    private Handler handler;
    private Runnable obstacleRunnable;
    private static final int UPDATE_INTERVAL = 1000; // 1 second
    private final Random randomGenerator;
    private ScoreListener scoreListener;
    private LifeListener lifeListener;
    private final Vibrator vibrator;

    public static final int ROW_COUNT = 4;
    public static final int COL_COUNT = 4;

    public GameManager(Context context, int[] mortyImages, int[][] rickImages) {
        this.mortyImages = mortyImages;
        // Using int matrix for Rick
        this.rickMatrix = new int[ROW_COUNT][COL_COUNT]; // Initialize the matrix with zeros
        this.mortyPosition = 1; // Start in the middle position
        this.score = 0;
        this.life = 3; // Start with 3 lives
        this.randomGenerator = new Random();
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        updateMortyVisibility(context);
        startObstacleTimer(context);
    }

    public void moveMortyRight(Context context) {
        if (mortyPosition < mortyImages.length - 1) {
            mortyPosition++;
            updateMortyVisibility(context);
        }
    }

    public void moveMortyLeft(Context context) {
        if (mortyPosition > 0) {
            mortyPosition--;
            updateMortyVisibility(context);
        }
    }

    private void updateMortyVisibility(Context context) {
        for (int i = 0; i < mortyImages.length; i++) {
            AppCompatImageView imageView = ((AppCompatImageView) ((MainActivity) context).findViewById(mortyImages[i]));
            imageView.setVisibility(i == mortyPosition ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public int getMortyPosition() {
        return mortyPosition;
    }

    public int getScore() {
        return score;
    }

    public int getLife() {
        return life;
    }

    public void setScoreListener(ScoreListener listener) {
        this.scoreListener = listener;
    }

    public void setLifeListener(LifeListener listener) {
        this.lifeListener = listener;
    }

    private void startObstacleTimer(Context context) {
        handler = new Handler(Looper.getMainLooper());
        obstacleRunnable = new Runnable() {
            @Override
            public void run() {
                increaseScore(); // Add 10 points to the score every second
                updateRickMatrix(context); // Update the Rick matrix every second
                checkCollision(context); // Check for collisions between Rick and Morty
                refreshRickVisibility(context); // Refresh visibility of Rick images
                if (life > 0) {
                    handler.postDelayed(this, UPDATE_INTERVAL); // Schedule the runnable again if life > 0
                }
            }
        };
        handler.postDelayed(obstacleRunnable, UPDATE_INTERVAL); // Start the runnable
    }

    private void increaseScore() {
        score += 10;
        if (scoreListener != null) {
            scoreListener.onScoreUpdated(score);
        }
    }

    private void updateRickMatrix(Context context) {
        // Move values to the downwards in the matrix
        for (int col = COL_COUNT - 1; col > 0; col--) {
            for (int row = 0; row < ROW_COUNT; row++) {
                rickMatrix[row][col] = rickMatrix[row][col - 1];
            }
        }

        // Clear the first row
        for (int row = 0; row < ROW_COUNT; row++) {
            rickMatrix[row][0] = 0;
        }

        // Generate a new obstacle in the first row
        int randomRow = randomGenerator.nextInt(ROW_COUNT); // Randomly select a row (0, 1, 2, or 3)
        rickMatrix[randomRow][0] = 1; // Set the selected col to 1 in the first row
    }

    private void refreshRickVisibility(Context context) {
        ((MainActivity) context).refreshRickVisibility(rickMatrix);
    }

    private void checkCollision(Context context) {
        // Check collision only in the last row
        for (int row = 0; row < ROW_COUNT; row++) {
            if (rickMatrix[row][COL_COUNT - 1] == 1 && row == mortyPosition) {
                AppCompatImageView mortyImageView = ((AppCompatImageView) ((MainActivity) context).findViewById(mortyImages[mortyPosition]));
                if (mortyImageView.getVisibility() == View.VISIBLE) {
                    reduceLife();
                    break;
                }
            }
        }
    }

    private void reduceLife() {
        life--;
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        if (lifeListener != null) {
            lifeListener.onLifeUpdated(life);
        }
        if (life <= 0) {
            stopTimer();
        }
    }

    public void stopTimer() {
        if (handler != null && obstacleRunnable != null) {
            handler.removeCallbacks(obstacleRunnable);
        }
    }

    public interface ScoreListener {
        void onScoreUpdated(int newScore);
    }

    public interface LifeListener {
        void onLifeUpdated(int newLife);
    }
}
