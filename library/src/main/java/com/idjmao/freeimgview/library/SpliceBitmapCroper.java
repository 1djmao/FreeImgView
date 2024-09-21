package com.idjmao.freeimgview.library;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Gravity;

import java.util.List;

public class SpliceBitmapCroper implements BitmapCroper{

    FreeImgView freeImgView;

    List<Bitmap> bitmapList;
    int bitmapW,bitmapH;

    int orientation= ORIENTATION_VERTICAL;

    public static final int ORIENTATION_VERTICAL=0;
    public static final int ORIENTATION_HORIZONTAL=1;

    private Bitmap tmpBitmap;
    int startHeight=0;
    int endHeight=0;



    public SpliceBitmapCroper(FreeImgView freeImgView,List<Bitmap> bitmapList,int orientation) {
        this.freeImgView=freeImgView;
        this.bitmapList = bitmapList;
        this.orientation=orientation;
        Log.i("TAG", "SpliceBitmapCroper: "+bitmapList.size());
        bitmapW=0;
        bitmapH=0;

        if (orientation==ORIENTATION_VERTICAL){
            for (int i = 0; i < bitmapList.size(); i++) {
                if (bitmapW<bitmapList.get(i).getWidth()){
                    bitmapW=bitmapList.get(i).getWidth();
                }
                bitmapH+=bitmapList.get(i).getHeight();
            }
        }else {
            for (int i = 0; i < bitmapList.size(); i++) {
                if (bitmapH<bitmapList.get(i).getHeight()){
                    bitmapH=bitmapList.get(i).getHeight();
                }
                bitmapW+=bitmapList.get(i).getWidth();
            }
        }

    }

    @Override
    public Bitmap getBitmap(int l, int t, int w, int h, float scale) {
        Log.i("TAG", "getBitmap: "+l+" "+t+" "+w+" "+h+" "+scale);
        long startTime=System.currentTimeMillis();
        Bitmap  result=null;
        if (orientation==ORIENTATION_VERTICAL){
            if (tmpBitmap!=null&&!tmpBitmap.isRecycled()&&startHeight<t&&t-startHeight+h<=tmpBitmap.getHeight()){
                Log.i("pppppp", "getBitmap: 复用");
                result=tmpBitmap;
            }
//            else if (tmpBitmap!=null&&!tmpBitmap.isRecycled()&&freeImgView.mTranslate.isRuning){
//                Log.i("pppppp", "getBitmap: 滑动中");
//                Bitmap bitmap= Bitmap.createBitmap(bitmapW, h, Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                if (t<startHeight){
//                    canvas.drawBitmap(tmpBitmap, startHeight-t, 0, null);
//                    startHeight=t;
//                }else {
//                    canvas.drawBitmap(tmpBitmap, 0, 0, null);
//                }
//                tmpBitmap=bitmap;
//                result=bitmap;
//
//
//            }
            else {
                Log.i("pppppp", "getBitmap: 重新拼接");
                if (tmpBitmap!=null){
                    tmpBitmap.recycle();
                    tmpBitmap=null;
                }
                startHeight=0;
                endHeight=0;
                for (int i = 0; i < bitmapList.size(); i++) {

                    endHeight+=bitmapList.get(i).getHeight();

                    if (result==null&&startHeight<=t&&endHeight>t){
                        result=BitmapUtils.copy(bitmapList.get(i));
                    }else if (result!=null&&endHeight-bitmapList.get(i).getHeight()<=t+h){
                        Bitmap bitmap= Bitmap.createBitmap(bitmapW, result.getHeight()+bitmapList.get(i).getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawBitmap(result, 0, 0, null);
                        canvas.drawBitmap(bitmapList.get(i), 0, result.getHeight(), null);
                        result=bitmap;
                    } else if (result!=null&&endHeight > t + h) {
                        break;
                    }

                    if (result==null){
                        startHeight+=bitmapList.get(i).getHeight();
                    }
                }
                tmpBitmap=result;
            }

            if (result!=null&&result.getHeight()!=h){
                Matrix matrix=new Matrix();
                matrix.postScale(scale,scale);
                Bitmap newBitmap=Bitmap.createBitmap(result, l,t-startHeight,w,h, matrix, false);
                result=newBitmap;
            }
        }
        Log.i("pppppp", "getBitmap: "+(System.currentTimeMillis()-startTime));

        return result;

    }


    @Override
    public int getBitmapW() {
        return bitmapW;
    }

    @Override
    public int getBitmapH() {
        return bitmapH;
    }

    @Override
    public void destroy() {

    }
}
