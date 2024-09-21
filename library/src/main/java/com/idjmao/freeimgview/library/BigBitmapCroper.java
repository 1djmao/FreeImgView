package com.idjmao.freeimgview.library;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BigBitmapCroper implements BitmapCroper{

    Bitmap mBaseBitmap;

    List<Bitmap> bitmaps=new ArrayList<>();
    List<Float> scales=new ArrayList<>();


    public BigBitmapCroper(float minScale,float maxScale,Bitmap mBaseBitmap) {
        this.mBaseBitmap = mBaseBitmap;

        float scale=1;
        while (scale/2>minScale){
            scale=scale/2;
            scales.add(scale);
            bitmaps.add(BitmapUtils.scaleBitmap(mBaseBitmap,scale));
        }
    }

    @Override
    public Bitmap getBitmap(int l, int t, int w, int h, float scale) {
        float thmScale=1;
        Bitmap thmBitmap = mBaseBitmap;
        if (scale<1){
            for (int i = 0; i < scales.size(); i++) {
                if (scales.get(i)>scale){
                    thmScale=scales.get(i);
                    thmBitmap=bitmaps.get(i);
                }
            }
        }

        Matrix matrix=new Matrix();

        matrix.postScale(scale/thmScale,scale/thmScale);
        Bitmap bitmap=Bitmap.createBitmap(thmBitmap, (int) (l*thmScale), (int) (t*thmScale), (int) (w*thmScale), (int) (h*thmScale), matrix, false);


        return bitmap;
    }

    @Override
    public int getBitmapW() {
        return mBaseBitmap.getWidth();
    }

    @Override
    public int getBitmapH() {
        return mBaseBitmap.getHeight();
    }

    @Override
    public void destroy() {
        for (Bitmap bitmap :bitmaps) {
            bitmap.recycle();
        }
    }


}
