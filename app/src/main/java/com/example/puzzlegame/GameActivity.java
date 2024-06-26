package com.example.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private int emptyX = 3;
    private int emptyY = 3;
    private RelativeLayout group;
    private Button[][] buttons;
    private int[] tiles;
    private TextView stepsView, timerView;
    private int stepCount = 0;
    private Timer timer;
    private int timerCount = 0;
    private ImageButton shuffleButton, stopButton, restartButton;
    private boolean isTimeRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        callFunctions();
    }

    private void callFunctions() {
        loadViews();
        loadNumbers();
        generateNumbers();
        DataToViews();
    }

    @SuppressLint("WrongViewCast")
    private void findId() {
        group = findViewById(R.id.group);
        stepsView = findViewById(R.id.stepsView);
        timerView = findViewById(R.id.timerView);
        shuffleButton =findViewById(R.id.ShuffleBtn);
        stopButton=findViewById(R.id.StopBtn);
        restartButton=findViewById(R.id.restartBtn);

    }

    private void loadViews() {
        findId();
        loadTimer();
        buttons = new Button[4][4];
        for (int i = 0; i < group.getChildCount(); i++) {
            buttons[i / 4][i % 4] = (Button) group.getChildAt(i);
        }
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNumbers();
                DataToViews();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimeRunning) {
                    timer.cancel();
                    stopButton.setImageResource(R.drawable.ic_start);
                    isTimeRunning = false;
                    for (int i = 0; i < group.getChildCount(); i++) {
                        buttons[i / 4][i % 4].setClickable(false);
                    }
                } else {
                    loadTimer();
                    stopButton.setImageResource(R.drawable.ic_stop);
                    for (int i = 0; i < group.getChildCount(); i++) {
                        buttons[i / 4][i % 4].setClickable(true);
                    }
                }
            }


        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
                dialogBuilder.setMessage("Game Restart")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            stepCount = 0;
                            timerCount=0;
                            stepsView.setText("Steps: " + stepCount);
                            generateNumbers();
                            DataToViews();
                            loadTimer();
                            isTimeRunning = true;
                            for (int i = 0; i < group.getChildCount(); i++) {
                                buttons[i / 4][i % 4].setClickable(true);
                            }
                            shuffleButton.setClickable(true);
                            stopButton.setClickable(true);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                        })
                        .show();
            }
        });
    }

    private void loadTimer() {
        isTimeRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerCount++;
                setTime(timerCount);
            }
        }, 1000, 1000);
    }

    @SuppressLint("DefaultLocale")
    private void setTime(int timerCount) {
        int second = timerCount % 60;
        int hour = timerCount / 60;
        int minute = (timerCount - hour * 3600) / 60;
        timerView.setText(String.format(" %d :: %d :: %d ", hour , minute , second ));
    }

    private void loadNumbers() {
        tiles = new int[16];
        for (int i = 0; i < group.getChildCount() - 1; i++) {
            tiles[i] = i + 1;
        }
    }

    private void generateNumbers() {
        int n = 15;
        Random random = new Random();
        while (n > 1) {
            int randomNumber = random.nextInt(n--);
            int temp = tiles[randomNumber];
            tiles[randomNumber] = tiles[n];
            tiles[n] = temp;
        }
        if (!isSolvable()) {
            generateNumbers();
        }
    }

    private boolean isSolvable() {
        int countInversions = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i]) {
                    countInversions++;
                }
            }
        }
        return countInversions % 2 == 0;
    }

    private void DataToViews() {
        emptyX = 3;
        emptyY = 3;
        for (int i = 0; i < group.getChildCount() - 1; i++) {
            buttons[i / 4][i % 4].setText(String.valueOf(tiles[i]));
            buttons[i / 4][i % 4].setBackgroundResource(android.R.drawable.btn_default);
        }
        buttons[emptyX][emptyY].setText("");
        buttons[emptyX][emptyY].setBackgroundColor(ContextCompat.getColor(this, R.color.colorFreeButton));
    }

    public void buttonClick(View view) {
        Button button = (Button) view;
        int x = Integer.parseInt(button.getTag().toString().substring(0, 1));
        int y = Integer.parseInt(button.getTag().toString().substring(1, 2));
        if ((Math.abs(emptyX - x) == 1 && emptyY == y) || (Math.abs(emptyY - y) == 1 && emptyX == x)) {
            buttons[emptyX][emptyY].setText(button.getText().toString());
            buttons[emptyX][emptyY].setBackgroundResource(android.R.drawable.btn_default);
            button.setText("");
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFreeButton));
            emptyX = x;
            emptyY = y;
            stepCount++;
            stepsView.setText("Steps: " + stepCount);
            CheckWin();
        }
    }

    private void CheckWin() {
        boolean isWin = false;
        if (emptyX == 3 && emptyY == 3) {
            for (int i = 0; i < group.getChildCount() - 1; i++) {
                if (buttons[i / 4][i % 4].getText().toString().equals(String.valueOf(i + 1))) {
                    isWin = true;
                } else {
                    isWin = false;
                    break;
                }
            }
        }
        if (isWin) {
            Toast.makeText(this, "Win!!!\nSteps:" + stepCount, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < group.getChildCount(); i++) {
                buttons[i / 4][i % 4].setClickable(false);
            }
            timer.cancel();
            shuffleButton.setClickable(false);
            stopButton.setClickable(false);
        }
    }
}
