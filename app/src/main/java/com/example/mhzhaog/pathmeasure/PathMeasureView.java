package com.example.mhzhaog.pathmeasure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PathMeasureView extends View {
    private Paint mPaint;
    Path path;
    PathMeasure measure1;
    PathMeasure measure;
    PathMeasure measure2;
    float mViewWidth ;
    float mViewHeight;

    private float currentValue = 0;     // 用于纪录当前的位置,取值范围[0,1]映射Path的整个长度

    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的tangent值,用于计算图片所需旋转的角度
    private Bitmap mBitmap;             // 箭头图片
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作

    public PathMeasureView(Context context) {
        super(context);
    }

    public PathMeasureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(60);
        path= new Path();

//        用这个构造函数是创建一个 PathMeasure 并关联一个 Path， 其实和创建一个空的 PathMeasure 后调用 setPath 进行关联效果是一样的，
// 同样，被关联的 Path 也必须是已经创建好的，如果关联之后 Path 内容进行了更改，则需要使用 setPath 方法重新关联。
//        该方法有两个参数，第一个参数自然就是被关联的 Path 了，第二个参数是用来确保 Path 闭合，
// 如果设置为 true， 则不论之前Path是否闭合，都会自动闭合该 Path(如果Path可以闭合的话)。
        measure1 = new PathMeasure();
        measure2 = new PathMeasure();
        measure = new PathMeasure();

        pos = new float[2];
        tan = new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;       // 缩放图片
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow, options);
        mMatrix = new Matrix();


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth  = w ;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.translate(mViewWidth/2,mViewHeight/2);
//
//        path.lineTo(0,200);
//        path.lineTo(200,200);
//        path.lineTo(200,0);
//
//        measure1.setPath(path,false);
//        measure2.setPath(path,true);
//
//        //E/TAG: forceClosed=false---->600.0
//        //E/TAG: forceClosed=true----->800.0
//        Log.e("TAG", "forceClosed=false---->"+measure1.getLength());
//        Log.e("TAG", "forceClosed=true----->"+measure2.getLength());
//
//        canvas.drawPath(path,mPaint);

//        canvas.translate(mViewWidth / 2, mViewHeight / 2);          // 平移坐标系
//
//        Path path = new Path();                                     // 创建Path并添加了一个矩形
//        path.addRect(-200, -200, 200, 200, Path.Direction.CW);
//
//        Path dst = new Path();                                      // 创建用于存储截取后内容的 Path
//        dst.lineTo(-300, -300);                                     // <--- 在 dst 中添加一条线段
//        PathMeasure measure = new PathMeasure(path, false);         // 将 Path 与 PathMeasure 关联
//
//// 截取一部分存入dst中，并使用 moveTo 保持截取得到的 Path 第一个点的位置不变
////        measure.getSegment(200, 600, dst, true);
//        measure.getSegment(200, 600, dst, false);
//
//        canvas.drawPath(dst, mPaint);                        // 绘制 dst

//        canvas.translate(mViewWidth / 2, mViewHeight / 2);      // 平移坐标系
//
////        Path path = new Path();
//
//        path.addRect(-100, -100, 100, 100, Path.Direction.CW);  // 添加小矩形
//        path.addRect(-200, -200, 200, 200, Path.Direction.CW);  // 添加大矩形
//
//        canvas.drawPath(path,mPaint);                    // 绘制 Path
//
//        measure.setPath(path,false);     // 将Path与PathMeasure关联
//
//        float len1 = measure.getLength();                       // 获得第一条路径的长度
//
//        measure.nextContour();                                  // 跳转到下一条路径
//
//        float len2 = measure.getLength();                       // 获得第二条路径的长度
//
//        Log.i("LEN","len1="+len1);                              // 输出两条路径的长度
//        Log.i("LEN","len2="+len2);

        canvas.translate(mViewWidth / 2, mViewHeight / 2);      // 平移坐标系

        Path path = new Path();                                 // 创建 Path

        path.addCircle(0, 0, 200, Path.Direction.CW);           // 添加一个圆形

        PathMeasure measure = new PathMeasure(path, false);     // 创建 PathMeasure

        currentValue += 0.005;                                  // 计算当前的位置在总长度上的比例[0,1]
        if (currentValue >= 1) {
            currentValue = 0;
        }

        measure.getPosTan(measure.getLength() * currentValue, pos, tan);        // 获取当前位置的坐标以及趋势

        mMatrix.reset();                                                        // 重置Matrix
        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI); // 计算图片旋转角度

        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);   // 旋转图片
        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合

        canvas.drawPath(path, mPaint);                                   // 绘制 Path
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);                     // 绘制箭头

        invalidate();                                                           // 重绘页面
    }
}
