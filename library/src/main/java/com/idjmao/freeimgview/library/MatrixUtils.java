package com.idjmao.freeimgview.library;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.Queue;

public class MatrixUtils {
    /**
     * 矩阵对象池
     */
    private static final MatrixPool mMatrixPool = new MatrixPool(16);

    /**
     * 获取矩阵对象
     */
    public static Matrix matrixTake() {
        return mMatrixPool.take();
    }

    /**
     * 获取某个矩阵的copy
     */
    public static Matrix matrixTake(Matrix matrix) {
        Matrix result = mMatrixPool.take();
        if (matrix != null) {
            result.set(matrix);
        }
        return result;
    }

    /**
     * 归还矩阵对象
     */
    public static void matrixGiven(Matrix matrix) {
        mMatrixPool.given(matrix);
    }

    /**
     * 矩形对象池
     */
    private static final RectFPool mRectFPool = new RectFPool(16);

    /**
     * 获取矩形对象
     */
    public static RectF rectFTake() {
        return mRectFPool.take();
    }

    /**
     * 按照指定值获取矩形对象
     */
    public static RectF rectFTake(float left, float top, float right, float bottom) {
        RectF result = mRectFPool.take();
        result.set(left, top, right, bottom);
        return result;
    }

    /**
     * 获取某个矩形的副本
     */
    public static RectF rectFTake(RectF rectF) {
        RectF result = mRectFPool.take();
        if (rectF != null) {
            result.set(rectF);
        }
        return result;
    }

    /**
     * 归还矩形对象
     */
    public static void rectFGiven(RectF rectF) {
        mRectFPool.given(rectF);
    }

    /**
     * 获取两点之间距离
     *
     * @param x1 点1
     * @param y1 点1
     * @param x2 点2
     * @param y2 点2
     * @return 距离
     */
    public static float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 获取两点的中点
     *
     * @param x1 点1
     * @param y1 点1
     * @param x2 点2
     * @param y2 点2
     * @return float[]{x, y}
     */
    public static float[] getCenterPoint(float x1, float y1, float x2, float y2) {
        return new float[]{(x1 + x2) / 2f, (y1 + y2) / 2f};
    }

    /**
     * 获取两点之间形成的区域
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static RectF getPointRect(float x1, float y1, float x2, float y2){
        float left,right,top,bottom;
        if (x1<x2){
            left=x1;
            right=x2;
        }else {
            left=x2;
            right=x1;
        }

        if (y1<y2){
            top=y1;
            bottom=y2;
        }else {
            top=y2;
            bottom=y1;
        }
        return new RectF(left,top,right,bottom);

    }

    /**
     * 获取矩阵的缩放值
     *
     * @param matrix 要计算的矩阵
     * @return float[]{scaleX, scaleY}
     */
    public static float[] getMatrixScale(Matrix matrix) {
        if (matrix != null) {
            float[] value = new float[9];
            matrix.getValues(value);
            return new float[]{value[0], value[4]};
        } else {
            return new float[2];
        }
    }
    /**
     * 获取矩阵的位移Y
     *
     * @param matrix 要计算的矩阵
     * @return float[]{scaleX, scaleY}
     */
    public static float getMatrixTrancY(Matrix matrix) {
        if (matrix != null) {
            float[] value = new float[9];
            matrix.getValues(value);
            return value[5];
        } else {
            return 0;
        }
    }
    /**
     * 获取矩阵的位移x
     *
     * @param matrix 要计算的矩阵
     * @return float[]{scaleX, scaleY}
     */
    public static float getMatrixTrancX(Matrix matrix) {
        if (matrix != null) {
            float[] value = new float[9];
            matrix.getValues(value);
            return value[2];
        } else {
            return 0;
        }
    }

    /**
     * 计算点除以矩阵的值
     *
     * matrix.mapPoints(unknownPoint) -> point
     * 已知point和matrix,求unknownPoint的值.
     *
     * @param point
     * @param matrix
     * @return unknownPoint
     */
    public static float[] inverseMatrixPoint(float[] point, Matrix matrix) {
        if (point != null && matrix != null) {
            float[] dst = new float[2];
            //计算matrix的逆矩阵
            Matrix inverse = matrixTake();
            matrix.invert(inverse);
            //用逆矩阵变换point到dst,dst就是结果
            inverse.mapPoints(dst, point);
            //清除临时变量
            matrixGiven(inverse);
            return dst;
        } else {
            return new float[2];
        }
    }

    /**
     * 计算两个矩形之间的变换矩阵
     *
     * unknownMatrix.mapRect(to, from)
     * 已知from矩形和to矩形,求unknownMatrix
     *
     * @param from
     * @param to
     * @param result unknownMatrix
     */
    public static void calculateRectTranslateMatrix(RectF from, RectF to, Matrix result) {
        if (from == null || to == null || result == null) {
            return;
        }
        if (from.width() == 0 || from.height() == 0) {
            return;
        }
        result.reset();
        result.postTranslate(-from.left, -from.top);
        result.postScale(to.width() / from.width(), to.height() / from.height());
        result.postTranslate(to.left, to.top);
    }

    /**
     * 计算图片在某个ImageView中的显示矩形
     *
     * @param container ImageView的Rect
     * @param srcWidth 图片的宽度
     * @param srcHeight 图片的高度
     * @param scaleType 图片在ImageView中的ScaleType
     * @param result 图片应该在ImageView中展示的矩形
     */
    public static void calculateScaledRectInContainer(RectF container, float srcWidth, float srcHeight, ImageView.ScaleType scaleType, RectF result) {
        if (container == null || result == null) {
            return;
        }
        if (srcWidth == 0 || srcHeight == 0) {
            return;
        }
        //默认scaleType为fit center
        if (scaleType == null) {
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }
        result.setEmpty();
        if (ImageView.ScaleType.FIT_XY.equals(scaleType)) {
            result.set(container);
        } else if (ImageView.ScaleType.CENTER.equals(scaleType)) {
            Matrix matrix = matrixTake();
            RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
            matrix.setTranslate((container.width() - srcWidth) * 0.5f, (container.height() - srcHeight) * 0.5f);
            matrix.mapRect(result, rect);
            rectFGiven(rect);
            matrixGiven(matrix);
            result.left += container.left;
            result.right += container.left;
            result.top += container.top;
            result.bottom += container.top;
        } else if (ImageView.ScaleType.CENTER_CROP.equals(scaleType)) {
            Matrix matrix = matrixTake();
            RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
            float scale;
            float dx = 0;
            float dy = 0;
            if (srcWidth * container.height() > container.width() * srcHeight) {
                scale = container.height() / srcHeight;
                dx = (container.width() - srcWidth * scale) * 0.5f;
            } else {
                scale = container.width() / srcWidth;
                dy = (container.height() - srcHeight * scale) * 0.5f;
            }
            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            matrix.mapRect(result, rect);
            rectFGiven(rect);
            matrixGiven(matrix);
            result.left += container.left;
            result.right += container.left;
            result.top += container.top;
            result.bottom += container.top;
        } else if (ImageView.ScaleType.CENTER_INSIDE.equals(scaleType)) {
            Matrix matrix = matrixTake();
            RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
            float scale;
            float dx;
            float dy;
            if (srcWidth <= container.width() && srcHeight <= container.height()) {
                scale = 1f;
            } else {
                scale = Math.min(container.width() / srcWidth, container.height() / srcHeight);
            }
            dx = (container.width() - srcWidth * scale) * 0.5f;
            dy = (container.height() - srcHeight * scale) * 0.5f;
            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            matrix.mapRect(result, rect);
            rectFGiven(rect);
            matrixGiven(matrix);
            result.left += container.left;
            result.right += container.left;
            result.top += container.top;
            result.bottom += container.top;
        } else if (ImageView.ScaleType.FIT_CENTER.equals(scaleType)) {
            Matrix matrix = matrixTake();
            RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
            RectF tempSrc = rectFTake(0, 0, srcWidth, srcHeight);
            RectF tempDst = rectFTake(0, 0, container.width(), container.height());
            matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
            matrix.mapRect(result, rect);
            rectFGiven(tempDst);
            rectFGiven(tempSrc);
            rectFGiven(rect);
            matrixGiven(matrix);
            result.left += container.left;
            result.right += container.left;
            result.top += container.top;
            result.bottom += container.top;
        } else if (ImageView.ScaleType.FIT_START.equals(scaleType)) {
            Matrix matrix = matrixTake();
            RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
            RectF tempSrc = rectFTake(0, 0, srcWidth, srcHeight);
            RectF tempDst = rectFTake(0, 0, container.width(), container.height());
            matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.START);
            matrix.mapRect(result, rect);
            rectFGiven(tempDst);
            rectFGiven(tempSrc);
            rectFGiven(rect);
            matrixGiven(matrix);
            result.left += container.left;
            result.right += container.left;
            result.top += container.top;
            result.bottom += container.top;
        } else if (ImageView.ScaleType.FIT_END.equals(scaleType)) {
            Matrix matrix = matrixTake();
            RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
            RectF tempSrc = rectFTake(0, 0, srcWidth, srcHeight);
            RectF tempDst = rectFTake(0, 0, container.width(), container.height());
            matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.END);
            matrix.mapRect(result, rect);
            rectFGiven(tempDst);
            rectFGiven(tempSrc);
            rectFGiven(rect);
            matrixGiven(matrix);
            result.left += container.left;
            result.right += container.left;
            result.top += container.top;
            result.bottom += container.top;
        } else {
            result.set(container);
        }
    }

    ////////////////////////////////防止内存抖动复用对象////////////////////////////////

    /**
     * 对象池
     *
     * 防止频繁new对象产生内存抖动.
     * 由于对象池最大长度限制,如果吞度量超过对象池容量,仍然会发生抖动.
     * 此时需要增大对象池容量,但是会占用更多内存.
     *
     * @param <T> 对象池容纳的对象类型
     */
    private static abstract class ObjectsPool<T> {

        /**
         * 对象池的最大容量
         */
        private final int mSize;

        /**
         * 对象池队列
         */
        private final Queue<T> mQueue;

        /**
         * 创建一个对象池
         *
         * @param size 对象池最大容量
         */
        public ObjectsPool(int size) {
            mSize = size;
            mQueue = new LinkedList<T>();
        }

        /**
         * 获取一个空闲的对象
         *
         * 如果对象池为空,则对象池自己会new一个返回.
         * 如果对象池内有对象,则取一个已存在的返回.
         * take出来的对象用完要记得调用given归还.
         * 如果不归还,让然会发生内存抖动,但不会引起泄漏.
         *
         * @return 可用的对象
         *
         * @see #given(Object)
         */
        public T take() {
            //如果池内为空就创建一个
            if (mQueue.size() == 0) {
                return newInstance();
            } else {
                //对象池里有就从顶端拿出来一个返回
                return resetInstance(mQueue.poll());
            }
        }

        /**
         * 归还对象池内申请的对象
         *
         * 如果归还的对象数量超过对象池容量,那么归还的对象就会被丢弃.
         *
         * @param obj 归还的对象
         *
         * @see #take()
         */
        public void given(T obj) {
            //如果对象池还有空位子就归还对象
            if (obj != null && mQueue.size() < mSize) {
                mQueue.offer(obj);
            }
        }

        /**
         * 实例化对象
         *
         * @return 创建的对象
         */
        abstract protected T newInstance();

        /**
         * 重置对象
         *
         * 把对象数据清空到就像刚创建的一样.
         *
         * @param obj 需要被重置的对象
         * @return 被重置之后的对象
         */
        abstract protected T resetInstance(T obj);
    }

    /**
     * 矩阵对象池
     */
    private static class MatrixPool extends ObjectsPool<Matrix> {

        public MatrixPool(int size) {
            super(size);
        }

        @Override
        protected Matrix newInstance() {
            return new Matrix();
        }

        @Override
        protected Matrix resetInstance(Matrix obj) {
            obj.reset();
            return obj;
        }
    }

    /**
     * 矩形对象池
     */
    private static class RectFPool extends ObjectsPool<RectF> {

        public RectFPool(int size) {
            super(size);
        }

        @Override
        protected RectF newInstance() {
            return new RectF();
        }

        @Override
        protected RectF resetInstance(RectF obj) {
            obj.setEmpty();
            return obj;
        }
    }
}
