package com.example.wordtypinggame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FrameLayout container;
    private TextView scoreTextView;
    private EditText inputEditText;
    private Handler handler;
    private Random random;
    private List<String> words;
    private int score;

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        scoreTextView = findViewById(R.id.scoreTextView);
        inputEditText = findViewById(R.id.inputEditText);
        handler = new Handler(Looper.getMainLooper());
        random = new Random();
        words = new ArrayList<>(Arrays.asList("Apple", "Banana", "Cat", "Dog", "Elephant"));
        score = 0;

        // 获取屏幕宽度和高度
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String input = inputEditText.getText().toString().trim();
                    checkWordMatch(input);
                    inputEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        startDroppingTextView();
    }

    private void startDroppingTextView() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addRandomTextView();
                startDroppingTextView();
            }
        }, 3000);
    }

    private void addRandomTextView() {
        TextView textView = new TextView(this);
        textView.setText(getRandomWord());
        textView.setTextSize(20);

        int x = getRandomX();
        int y = 0;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(x, y, 0, 0);

        container.addView(textView, layoutParams);

        // 开始下落动画
        animateTextView(textView);
    }

    private String getRandomWord() {
        int index = random.nextInt(words.size());
        String word = words.get(index);
        words.remove(index);
        if (words.isEmpty()) {
            words.addAll(Arrays.asList("Apple", "Banana", "Cat", "Dog", "Elephant"));
        }
        return word;
    }

    private int getRandomX() {
        int textViewWidth = getResources().getDimensionPixelSize(R.dimen.text_view_width);
        return random.nextInt(screenWidth - textViewWidth);
    }

    private void animateTextView(final TextView textView) {
        final int duration = 8000; // 下落动画的持续时间，单位为毫秒

        textView.animate()
                .translationY(screenHeight - textView.getHeight())
                .setDuration(duration)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        String word = textView.getText().toString();
                        words.add(word);
                        container.removeView(textView);
                    }
                })
                .start();
    }

    private void checkWordMatch(String input) {
        int nearestDistance = Integer.MAX_VALUE;
        TextView nearestTextView = null;

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                String word = textView.getText().toString();
                int distance = Math.abs(container.getHeight() - textView.getBottom());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTextView = textView;
                }
                if (input.equalsIgnoreCase(word)) {
                    container.removeView(textView);
                    updateScore(10);
                    return;
                }
            }
        }

        if (nearestTextView != null) {
            String nearestWord = nearestTextView.getText().toString();
            Toast.makeText(this, "Incorrect word! Nearest word: " + nearestWord, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect word!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateScore(int scoreDelta) {
        score += scoreDelta;
        scoreTextView.setText("Score: " + score);
    }
}
//测试分支3