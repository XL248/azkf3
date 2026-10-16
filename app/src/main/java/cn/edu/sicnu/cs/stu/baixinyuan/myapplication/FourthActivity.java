package cn.edu.sicnu.cs.stu.baixinyuan.myapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * 第 4 个二级 Activity：秒表（StopWatch）。
 *
 * <p>重点在于「横屏竖屏改变的时候信息的保存和恢复」：屏幕旋转会让 Activity 销毁重建，
 * 所以把已计时长、是否正在计时、当前起点写进 {@code onSaveInstanceState}，
 * 重建时在 {@code onCreate} 里读回来并<b>按真实流逝时间续算</b>，
 * 这样旋转过程中计时既不中断也不归零。</p>
 */
public class FourthActivity extends AppCompatActivity {

    private static final String STATE_ELAPSED = "state_elapsed";
    private static final String STATE_RUNNING = "state_running";
    private static final String STATE_BASE = "state_base";

    /** 每 100 毫秒刷新一次显示。 */
    private static final long TICK_INTERVAL_MS = 100L;

    private TextView tvTime;
    private TextView tvState;
    private TextView tvOrientation;

    private final Handler handler = new Handler(Looper.getMainLooper());

    /** 已累计的计时时长（毫秒）。 */
    private long elapsedMs = 0L;
    /** 是否正在计时。 */
    private boolean running = false;
    /** 计时起点（SystemClock.elapsedRealtime()），仅在 running 时有意义。 */
    private long startBaseMs = 0L;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateTimeText();
            if (running) {
                handler.postDelayed(this, TICK_INTERVAL_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fourth);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTime = findViewById(R.id.tvTime);
        tvState = findViewById(R.id.tvState);
        tvOrientation = findViewById(R.id.tvOrientation);

        MaterialButton btnStart = findViewById(R.id.btnStart);
        MaterialButton btnPause = findViewById(R.id.btnPause);
        MaterialButton btnReset = findViewById(R.id.btnReset);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        if (savedInstanceState != null) {
            elapsedMs = savedInstanceState.getLong(STATE_ELAPSED, 0L);
            running = savedInstanceState.getBoolean(STATE_RUNNING, false);
            startBaseMs = savedInstanceState.getLong(STATE_BASE, 0L);
            if (running) {
                // 重建期间可能已经过了几百毫秒，这里把这部分时间补回来
                long sinceBase = SystemClock.elapsedRealtime() - startBaseMs;
                if (sinceBase > 0) {
                    elapsedMs += sinceBase;
                }
                startBaseMs = SystemClock.elapsedRealtime();
                handler.post(ticker);
            }
        }

        updateTimeText();
        updateStateText();
        updateOrientationText(getResources().getConfiguration().orientation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fourthRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long current = currentElapsed();
        outState.putLong(STATE_ELAPSED, current);
        outState.putBoolean(STATE_RUNNING, running);
        outState.putLong(STATE_BASE, startBaseMs);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateOrientationText(newConfig.orientation);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }

    /** 开始 / 继续计时。 */
    private void start() {
        if (running) {
            return;
        }
        running = true;
        startBaseMs = SystemClock.elapsedRealtime();
        handler.removeCallbacks(ticker);
        handler.post(ticker);
        updateStateText();
    }

    /** 暂停计时，把这一段的时间累加进 elapsedMs。 */
    private void pause() {
        if (!running) {
            return;
        }
        elapsedMs = currentElapsed();
        running = false;
        handler.removeCallbacks(ticker);
        updateTimeText();
        updateStateText();
    }

    /** 归零。 */
    private void reset() {
        running = false;
        elapsedMs = 0L;
        startBaseMs = 0L;
        handler.removeCallbacks(ticker);
        updateTimeText();
        updateStateText();
    }

    /** 当前真实已计时长。 */
    private long currentElapsed() {
        if (running) {
            return elapsedMs + (SystemClock.elapsedRealtime() - startBaseMs);
        }
        return elapsedMs;
    }

    private void updateTimeText() {
        tvTime.setText(Utils.formatStopwatch(currentElapsed()));
    }

    private void updateStateText() {
        if (running) {
            tvState.setText(R.string.stopwatch_state_running);
        } else if (elapsedMs > 0L) {
            tvState.setText(R.string.stopwatch_state_paused);
        } else {
            tvState.setText(R.string.stopwatch_state_idle);
        }
    }

    private void updateOrientationText(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tvOrientation.setText(R.string.stopwatch_orientation_landscape);
        } else {
            tvOrientation.setText(R.string.stopwatch_orientation_portrait);
        }
    }
}
