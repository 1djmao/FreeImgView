package com.idjmao.freeimgview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Debug;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.idjmao.freeimgview.library.BitmapUtils;
import com.idjmao.freeimgview.library.FreeImgView;
import com.idjmao.freeimgview.library.OnOutClickListener;
import com.idjmao.freeimgview.library.RegionDecoderCroper;
import com.idjmao.freeimgview.library.SpliceBitmapCroper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FreeImgView freeImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        freeImgView =findViewById(R.id.zoom_view);
        freeImgView.setScaleEnable();
        freeImgView.setRotateDisable();
//        freeImgView.setBigPicMode();
//        freeImgView.setImageResource(R.mipmap.eso1242a);

// 开始 TraceView 记录
        Debug.startMethodTracing("my_trace");

        initPdf();
        freeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

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


    private void initPdf(){
        List<Bitmap> bitmapList=new ArrayList<>();
        final Bitmap[] bitmap = {null};
        new ThreadRun(new Runnable() {
            @Override
            public void run() {
                File pdfFile=new File(getFilesDir(),"aaa.pdf");
                FileUtils.copyAssetsFile2Phone(MainActivity.this,"2023082829九师联盟高三联考河南01-原佳烨.pdf",pdfFile);
                bitmapList.addAll(getPdfBitmap(pdfFile));
                bitmap[0] = BitmapUtils.spliceBitmapUpDown(bitmapList);
            }
        }, new Runnable() {
            @Override
            public void run() {


//                freeImgView.setImageBitmap(bitmap[0]);
//                freeImgView.setBigPicMode();

                freeImgView.setBitmapCroper(new SpliceBitmapCroper(freeImgView,bitmapList,SpliceBitmapCroper.ORIENTATION_VERTICAL));

//                freeImgView.setBitmapCroper(new RegionDecoderCroper(bitmap[0]));
            }
        },this).start();
    }

    private List<Bitmap> getPdfBitmap(File file){
        ParcelFileDescriptor pdfFile = null;
        try {
            pdfFile = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY); //以只读的方式打开文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PdfRenderer renderer = null;
        try {
            renderer = new PdfRenderer(pdfFile);//用上面的pdfFile新建PdfRenderer对象
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int pageCount = renderer.getPageCount();//获取pdf的页码数
        Bitmap[] bitmaps=new Bitmap[pageCount];//新建一个bmp数组用于存放pdf页面

        WindowManager wm = this.getWindowManager();//获取屏幕的高和宽，以决定pdf的高和宽
        float width = wm.getDefaultDisplay().getWidth()*2;
        float height=wm.getDefaultDisplay().getHeight();

        for (int i = 0; i < pageCount; i++) {//这里用循环把pdf所有的页面都写入bitmap数组，真正使用的时候最好不要这样，
//因为一本pdf的书会有很多页，一次性全部打开会非常消耗内存，我打开一本两百多页的书就消耗了1.8G的内存，而且打开速度很慢。
//真正使用的时候要采用动态加载，用户看到哪页才加载附近的几页。而且最好使用多线程在后台打开。

            PdfRenderer.Page page = renderer.openPage(i);//根据i的变化打开每一页
            Bitmap mBitmap=Bitmap.createBitmap((int)(width),(int)(page.getHeight()*width/page.getWidth()),Bitmap.Config.ARGB_8888);//根据屏幕的高宽缩放生成bmp对象
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);//将pdf的内容写入bmp中

            bitmaps[i]=mBitmap;//将pdf的bmp图像存放进数组中。

            // close the page
            page.close();
        }

        // close the renderer
        renderer.close();

        return Arrays.asList(bitmaps);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止 TraceView 记录
        Debug.stopMethodTracing();
    }
}