package com.example.silentphotography;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CaptureActivity extends AppCompatActivity {
    private final int RESULT_CODE_STARTCAMERA = 1;
    private SurfaceView mySurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySurfaceView = findViewById(R.id.sf);
        getPermition();
        findViewById(R.id.takePicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TakePicture(CaptureActivity.this,mySurfaceView);
            }
        });
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    private void getPermition(){
        //判断是否开户相机权限
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(CaptureActivity.this, android.Manifest.permission.CAMERA)) {

        }else{
            //提示用户开户权限
            String[] perms = {"android.permission.CAMERA"};
            ActivityCompat.requestPermissions(CaptureActivity.this,perms, RESULT_CODE_STARTCAMERA);
        }

    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case RESULT_CODE_STARTCAMERA:
                boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    //授权成功之后，调用系统相机进行拍照操作等
                }else{
                    //用户授权拒绝之后，友情提示一下就可以了
                    Toast.makeText(CaptureActivity.this,"请开启动相机权限！", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
