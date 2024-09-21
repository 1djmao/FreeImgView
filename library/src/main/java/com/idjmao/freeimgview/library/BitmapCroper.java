package com.idjmao.freeimgview.library;

import android.graphics.Bitmap;

public interface BitmapCroper {

    public Bitmap getBitmap(int l,int t,int w,int h,float scale);

    public int getBitmapW();
    public int getBitmapH();
    public void destroy();

}
