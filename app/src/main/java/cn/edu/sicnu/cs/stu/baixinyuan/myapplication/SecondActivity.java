package cn.edu.sicnu.cs.stu.baixinyuan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * 第 2 个二级 Activity：演示「把数据返回给上一个 Activity」。
 *
 * <p>用户在这里输入内容，点「确定并返回」后把内容装进 Intent，
 * 用 {@code setResult(RESULT_CONFIRM, data)} + {@code finish()} 交给 MainActivity，
 * MainActivity 在 {@code onActivityResult} 里接收并显示。</p>
 */
public class SecondActivity extends AppCompatActivity {

    private EditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etInput = findViewById(R.id.etInput);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAndBack();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.secondRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** 输入内容并回传给 MainActivity。 */
    private void confirmAndBack() {
        String text = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, R.string.toast_empty_input, Toast.LENGTH_SHORT).show();
            etInput.requestFocus();
            return;
        }

        Intent data = new Intent();
        data.putExtra(MainActivity.EXTRA_RESULT_TEXT, text);
        setResult(MainActivity.RESULT_CONFIRM, data);
        finish();
    }
}
