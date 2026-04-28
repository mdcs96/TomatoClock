package com.example.tomatoclock;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView timerText;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Button workButton;
    private Button shortBreakButton;
    private Button longBreakButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning = false;

    // 默认时长（毫秒）
    private static final long WORK_TIME = 25 * 60 * 1000; // 25分钟
    private static final long SHORT_BREAK_TIME = 5 * 60 * 1000; // 5分钟
    private static final long LONG_BREAK_TIME = 15 * 60 * 1000; // 15分钟

    private long currentDuration = WORK_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timer_text);
        startButton = findViewById(R.id.start_button);
        pauseButton = findViewById(R.id.pause_button);
        resetButton = findViewById(R.id.reset_button);
        workButton = findViewById(R.id.work_button);
        shortBreakButton = findViewById(R.id.short_break_button);
        longBreakButton = findViewById(R.id.long_break_button);

        timeLeftInMillis = WORK_TIME;
        updateTimerText();

        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        workButton.setOnClickListener(v -> setDuration(WORK_TIME));
        shortBreakButton.setOnClickListener(v -> setDuration(SHORT_BREAK_TIME));
        longBreakButton.setOnClickListener(v -> setDuration(LONG_BREAK_TIME));

        pauseButton.setEnabled(false);
    }

    private void setDuration(long duration) {
        if (timerRunning) {
            pauseTimer();
        }
        currentDuration = duration;
        timeLeftInMillis = duration;
        updateTimerText();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);

                // 震动提醒
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(1000);
                }

                timerText.setText("00:00");
            }
        }.start();

        timerRunning = true;
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        timeLeftInMillis = currentDuration;
        updateTimerText();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }
}
