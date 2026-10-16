package cn.edu.sicnu.cs.stu.baixinyuan.myapplication;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * 第 3 个二级 Activity：用<b>隐式 Intent</b> 调用系统相机拍照，并显示拍到的照片。
 *
 * <p>核心是 {@code new Intent(MediaStore.ACTION_IMAGE_CAPTURE)} 这种只说明「要做什么」、
 * 不指定由谁做的隐式调用，系统会弹出可以处理该动作的相机应用。</p>
 */
public class ThirdActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    private ImageView ivPhoto;
    private TextView tvPhotoDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivPhoto = findViewById(R.id.ivPhoto);
        tvPhotoDesc = findViewById(R.id.tvPhotoDesc);

        MaterialButton btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndTakePhoto();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.thirdRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** Android 6.0 及以上需要运行时申请相机权限。 */
    private void checkPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    /** 隐式 Intent：让系统去找能处理「拍照」这件事的应用。 */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Android 11 起 queryIntentActivities 默认只能看到「已声明可见性」的应用，
        // 所以这里先按官方写法尝试解析；解析不到再直接尝试启动，交给 try/catch 兜底。
        boolean canResolve = takePictureIntent.resolveActivity(getPackageManager()) != null;
        if (!canResolve) {
            canResolve = !getPackageManager()
                    .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    .isEmpty();
        }

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // 设备上确实没有任何相机应用
            if (!canResolve) {
                showStatus(getString(R.string.photo_no_camera));
            } else {
                showStatus(getString(R.string.photo_failed));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, R.string.photo_denied, Toast.LENGTH_SHORT).show();
            showStatus(getString(R.string.photo_denied));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMAGE_CAPTURE) {
            return;
        }

        if (resultCode != RESULT_OK) {
            showStatus(getString(R.string.photo_canceled));
            return;
        }

        // 相机把拍到的照片作为缩略图放在 extras 的 "data" 里返回
        if (data != null && data.getExtras() != null) {
            Object extra = data.getExtras().get("data");
            if (extra instanceof Bitmap) {
                Bitmap imageBitmap = (Bitmap) extra;
                ivPhoto.setImageBitmap(imageBitmap);
                showStatus(getString(R.string.photo_ok));
                return;
            }
        }
        showStatus(getString(R.string.photo_failed));
    }

    private void showStatus(String text) {
        tvPhotoDesc.setText(text);
    }
}
