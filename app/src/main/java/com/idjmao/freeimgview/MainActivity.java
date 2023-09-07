package com.idjmao.freeimgview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.idjmao.freeimgview.library.FreeImgView;
import com.idjmao.freeimgview.library.OnOutClickListener;

public class MainActivity extends AppCompatActivity {

    FreeImgView freeImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        freeImgView =findViewById(R.id.zoom_view);
        freeImgView.setScaleEnable();
        freeImgView.setRotateEnable();
        freeImgView.setMinScale(0.5f);

        freeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "点击事件", Toast.LENGTH_SHORT).show();
            }
        });


        freeImgView.setOnOutClickListener(new OnOutClickListener() {
            @Override
            public void onOutClick(View view) {
                Toast.makeText(MainActivity.this, "点击图像外部", Toast.LENGTH_SHORT).show();
            }
        });
        freeImgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "长按事件", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }
}