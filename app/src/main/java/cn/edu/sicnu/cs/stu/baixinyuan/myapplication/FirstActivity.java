package cn.edu.sicnu.cs.stu.baixinyuan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * 第 1 个二级 Activity：演示「正向传参」。
 *
 * <p>MainActivity 通过显式 Intent + {@code putExtra} 把参数传进来，
 * 本页面在 {@code onCreate} 里用 {@code getIntent().getXxxExtra(...)} 取出来显示。</p>
 */
public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 取出 MainActivity 传过来的参数
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        String studentId = intent.getStringExtra(MainActivity.EXTRA_STUDENT_ID);
        int age = intent.getIntExtra(MainActivity.EXTRA_AGE, -1);
        String city = intent.getStringExtra(MainActivity.EXTRA_CITY);
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        setRow(R.id.tvName, R.string.label_name, orNoParam(name));
        setRow(R.id.tvStudentId, R.string.label_student_id, orNoParam(studentId));
        setRow(R.id.tvAge, R.string.label_age, age >= 0 ? String.valueOf(age) : getString(R.string.no_param));
        setRow(R.id.tvCity, R.string.label_city, orNoParam(city));

        TextView tvExtra = findViewById(R.id.tvExtra);
        tvExtra.setText(message == null ? getString(R.string.no_param) : message);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.firstRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** 把「标签：值」写进指定的 TextView。 */
    private void setRow(int viewId, int labelResId, String value) {
        TextView tv = findViewById(viewId);
        tv.setText(getString(R.string.param_format, getString(labelResId), value));
    }

    private String orNoParam(String value) {
        return (value == null || value.isEmpty()) ? getString(R.string.no_param) : value;
    }
}
