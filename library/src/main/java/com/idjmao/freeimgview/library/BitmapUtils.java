package com.idjmao.freeimgview.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BitmapUtils {
    public static Bitmap file2Bitmap(File f){
        return BitmapFactory.decodeFile(f.getPath());
    }

    public static boolean bitmap2File(Bitmap bitmap,File file,Bitmap.CompressFormat format){
        try {
            if (!file.exists()){
                if (!file.createNewFile()){
                    return false;
                }
            }
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(format, 100, os);
            os.flush();
            os.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Bitmap uri2Bitmap(Context context, Uri uri){
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String bitmap2Base64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //读取图片到ByteArrayOutputStream
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, baos); //参数如果为100那么就不压缩
        byte[] bytes = baos.toByteArray();
        String strbm = Base64.encodeToString(bytes,Base64.DEFAULT);
        return strbm;
    }
    public static Bitmap base64ToBitmap(String string) {
        Bitmap bitmap = null;
//        try {
//            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
//            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        byte [] input = Base64.decode(string, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(input, 0, input.length);
        return bitmap;
    }

    /**
     * bitmap 转  inputstream
     */
    public static InputStream bitmap2InputStream(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
    /**
     * inputstream  转   bitmap
     */
    public static Bitmap inputStream2Bitmap(InputStream is){
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }


    public static Bitmap copy(Bitmap bitmap){
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        // 拿着可以被修改的图片创建一个画布.
        Canvas canvas = new Canvas(bitmap2);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return bitmap2;
    }
    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
//        origin.recycle();
        return newBM;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }
    public static Bitmap cropBitmap(Bitmap bitmap, RectF rectf) {
//        int w = bitmap.getWidth(); // 得到图片的宽，高
//        int h = bitmap.getHeight();
//        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
//        cropWidth /= 2;
//        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, (int) rectf.left, (int)rectf.top, (int)rectf.width(), (int)rectf.height(), null, false);
    }

    public static Bitmap spliceBitmapUpDown(List<Bitmap> bitmaps){
        Bitmap bitmap= bitmaps.get(0);
        int width =bitmap.getWidth();
        int height =bitmap.getHeight();

        for (int i = 1; i < bitmaps.size(); i++) {
            Bitmap bitmap2 = bitmaps.get(i);
            int bitmap2Height = bitmap2.getHeight();
            int bitmap2Width = bitmap2.getWidth();
            width = Math.max(width,bitmap2Width);
            height = height + bitmap2Height;
            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.drawBitmap(bitmap2, 0, bitmap.getHeight(), null);
            bitmap = result;
        }
        return bitmap;
    }

    public static Bitmap spliceBitmapLeftRight(List<Bitmap> bitmaps){
        Bitmap bitmap= bitmaps.get(0);
        int width =bitmap.getWidth();
        int height =bitmap.getHeight();
        for (int i = 1; i < bitmaps.size(); i++) {
            Bitmap bitmap2 = bitmaps.get(i+1);
            int bitmap2Height = bitmap2.getHeight();
            int bitmap2Width = bitmap2.getWidth();
            height =   Math.max(height,bitmap2Height);
            width = width + bitmap2Width;
            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.drawBitmap(bitmap2, bitmap.getWidth(), 0, null);
            bitmap = result;
        }
        return bitmap;
    }


    /**
     * 旋转变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
    /**
     * 偏移效果
     * @param origin 原图
     * @return 偏移后的bitmap
     */
    public static Bitmap skewBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postSkew(-0.6f, -0.3f);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * View 转为 bitmap
     * @param view
     * @return
     */
    public static Bitmap view2Bitmap(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

//        view.destroyDrawingCache();
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.setDrawingCacheEnabled(true);
//        return view.getDrawingCache(true);
    }
}
