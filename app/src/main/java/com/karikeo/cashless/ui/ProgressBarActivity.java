package com.karikeo.cashless.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karikeo.cashless.R;

public abstract class ProgressBarActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView progressText;
    private ViewGroup content;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressText = (TextView) findViewById(R.id.progress_text);
        content = (ViewGroup) findViewById(R.id.content);
        progress = findViewById(R.id.progress);

        content.addView(LayoutInflater.from(this).inflate(getLayoutId(), null));
    }

    @Override
    protected void onPause() {
        super.onPause();
        showContent();
    }

    protected abstract int getLayoutId();

    protected void showContent() {
        content.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        progressText.setText(R.string.empty);
    }

    protected void showProgress(int message) {
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        progressText.setText(message);
    }

    protected void setProgressPercent(int progress) {
        progressBar.setProgress(progress);
    }
}
