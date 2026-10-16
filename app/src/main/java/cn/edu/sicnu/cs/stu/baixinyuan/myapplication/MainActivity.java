package cn.edu.sicnu.cs.stu.baixinyuan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * 主界面：提供 4 个按钮，分别演示 Activity 的四种典型用法。
 *
 * <ol>
 *   <li>按钮 1：显式 Intent 启动第 1 个二级 Activity，并<b>正向传参</b>；</li>
 *   <li>按钮 2：启动第 2 个二级 Activity，用 {@code startActivityForResult} 拿回<b>返回数据</b>；</li>
 *   <li>按钮 3：启动第 3 个二级 Activity，它内部用<b>隐式 Intent</b> 调起系统相机拍照；</li>
 *   <li>按钮 4：启动第 4 个二级 Activity，实现<b>秒表</b>，横竖屏切换计时不中断。</li>
 * </ol>
 */
public class MainActivity extends AppCompatActivity {

    /** 传给第 1 个 Activity 的各个参数（Intent extra）的 key。 */
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    public static final String EXTRA_AGE = "extra_age";
    public static final String EXTRA_CITY = "extra_city";
    public static final String EXTRA_MESSAGE = "extra_message";

    /** 请求码：启动第 2 个 Activity 并等待它返回结果。 */
    public static final int REQUEST_SECOND = 1;

    /** 第 2 个 Activity 回传结果时使用的 key。 */
    public static final String EXTRA_RESULT_TEXT = "extra_result_text";

    /** 第 2 个 Activity 确认返回时使用的 resultCode。 */
    public static final int RESULT_CONFIRM = 100;

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " · " + getString(R.string.main_title));

        tvResult = findViewById(R.id.tvResult);

        MaterialButton btnFirst = findViewById(R.id.btnFirst);
        MaterialButton btnSecond = findViewById(R.id.btnSecond);
        MaterialButton btnThird = findViewById(R.id.btnThird);
        MaterialButton btnFourth = findViewById(R.id.btnFourth);

        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFirstActivity();
            }
        });
        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivity();
            }
        });
        btnThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ThirdActivity.class));
            }
        });
        btnFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FourthActivity.class));
            }
        });

        // 让内容避开状态栏 / 导航栏，避免被系统栏遮住
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** 第 1 个按钮：显式 Intent 正向传参。 */
    private void openFirstActivity() {
        Intent intent = new Intent(MainActivity.this, FirstActivity.class);
        intent.putExtra(EXTRA_NAME, getString(R.string.extra_sender_name));
        intent.putExtra(EXTRA_STUDENT_ID, getString(R.string.extra_sender_student_id));
        intent.putExtra(EXTRA_AGE, 20);
        intent.putExtra(EXTRA_CITY, getString(R.string.extra_sender_city));
        intent.putExtra(EXTRA_MESSAGE, getString(R.string.extra_sender_message));
        startActivity(intent);
    }

    /** 第 2 个按钮：等第 2 个 Activity 把数据带回来。 */
    private void openSecondActivity() {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivityForResult(intent, REQUEST_SECOND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_SECOND) {
            return;
        }

        if (resultCode == RESULT_CONFIRM && data != null) {
            String text = data.getStringExtra(EXTRA_RESULT_TEXT);
            showResult(getString(R.string.result_prefix)
                    + (text == null || text.isEmpty() ? getString(R.string.result_empty) : text));
        } else {
            Toast.makeText(this, R.string.toast_back_cancel, Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult(String text) {
        tvResult.setText(text);
        tvResult.setVisibility(View.VISIBLE);
    }
}
