package com.idjmao.freeimgview.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

public class RegionDecoderCroper implements BitmapCroper {

    Bitmap mBaseBitmap;
    private BitmapRegionDecoder mDecoder;
    private static BitmapFactory.Options mDecodeOptions = new BitmapFactory.Options();
    static{
        mDecodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    public RegionDecoderCroper(Bitmap mBaseBitmap) {
        this.mBaseBitmap = mBaseBitmap;
        try {
            mDecoder = BitmapRegionDecoder.newInstance(BitmapUtils.bitmap2InputStream(mBaseBitmap), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Bitmap getBitmap(int l, int t, int w, int h, float scale) {
        Rect mRect=new Rect(l,t,l+w,t+h);
        Bitmap bitmap = mDecoder.decodeRegion(mRect, mDecodeOptions);
        bitmap=BitmapUtils.scaleBitmap(bitmap,scale);
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

    }
}
