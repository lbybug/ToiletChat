package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.lee.toiletchat.R;

/**
 * Created by Lee on 2019/3/7.
 */


public class ScaleImage extends View {

    private static final String TAG = "ScaleImage";

    public Paint mPaint; //默认画笔

    public Drawable rightButton,bottomButton;
    public Bitmap basicBitmap; //需要展示的图片
    public Matrix bitmapMatrix = new Matrix();

    public float scaleX = DEFAULT_SCALE;  //初始化 X 倍数
    public float scaleY = DEFAULT_SCALE;  //初始化 Y 倍数

    public int bottomSize = 0;            //拉伸按钮的大小

    public static final int DRAG_STATUS = 0x01; //移动状态
    public static final int RIGHT_SCALE = 0x02; //右拉伸状态
    public static final int BOTTOM_SCALE = 0x03;//下拉伸状态
    public static final int INIT_STATUS = 0x11; //初始状态

    public static final int QR_CODE = 0x21; //二维码
    public static final int BARCODE = 0x22; //条码
    public static final int PDF_417 = 0x23; //PDF码

    public static final float DEFAULT_SCALE = 1.0f; //初始倍数
    public static final float MAX_SCALE = 3;   //最大倍数
    public static final float MIN_SCALE = 0.1f; //最小倍数

    public int currentStatus = INIT_STATUS;
    public int currentType = QR_CODE;

    public float[] currentXY = new float[]{0, 0}; //记录点击的x,y

    public void setBasicBitmap(Bitmap basicBitmap) {
        this.basicBitmap = basicBitmap;
        bottomSize = basicBitmap.getWidth() / 3;
        invalidate();
    }

    public ScaleImage(Context context) {
        super(context);
        init();
    }

    public void init(){
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        if (rightButton == null) {
            rightButton = getContext().getResources().getDrawable(R.mipmap.edit_right);
        }
        if (bottomButton == null) {
            bottomButton = getContext().getResources().getDrawable(R.mipmap.edit_bottom);
        }
        Log.d(TAG, "init: 初始化成功");
    }

    public ScaleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: 测量");
        setMeasuredDimension((int) ((basicBitmap.getWidth() + bottomSize / 2) * scaleX), (int) ((basicBitmap.getHeight() + bottomSize / 2) * scaleY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (basicBitmap != null) {
            int width = (int) (basicBitmap.getWidth() * scaleX);
            int height = (int) (basicBitmap.getHeight() * scaleY);
            bitmapMatrix.setScale(scaleX,scaleY);
            canvas.drawBitmap(basicBitmap,bitmapMatrix,mPaint);
            if (isShowScaleBottom()){
                canvas.save();
                canvas.translate(width - bottomSize / 2,(height - bottomSize) / 2);
                rightButton.setBounds(0,0,bottomSize,bottomSize);
                rightButton.draw(canvas);
                canvas.restore();
                canvas.translate((width - bottomSize) / 2,height - bottomSize / 2);
                bottomButton.setBounds(0,0,bottomSize,bottomSize);
                bottomButton.draw(canvas);
            }
        }
    }

    public boolean isShowScaleBottom = true;

    public void setShowScaleBottom(boolean showScaleBottom) {
        isShowScaleBottom = showScaleBottom;
    }

    public boolean isShowScaleBottom() {
        return isShowScaleBottom;
    }

    int move = 0;

    int allWidth = basicBitmap.getWidth();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setShowScaleBottom(true);
                int X = (int) event.getX();
                int Y = (int) event.getY();
                currentXY[0] = event.getRawX();
                currentXY[1] = event.getRawY();
                judgeStatus(X,Y);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                //拿到手指移动距离的大小
                int move_bigX = (int) (moveX - currentXY[0]);
                int move_bigY = (int) (moveY - currentXY[1]);

                if (currentStatus == DRAG_STATUS){
                    //拿到当前控件未移动的坐标
                    int left = getLeft() + move_bigX;
                    int right = getRight() + move_bigX;
                    int top = getTop() + move_bigY;
                    int bottom = getBottom() + move_bigY;

                    Log.d(TAG, "onTouchEvent: "+left);
                    Log.d(TAG, "onTouchEvent: "+top);
                    layout(left,top,right,bottom);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
//                    params.setMargins(left,top,right,bottom);
//                    postInvalidate();
                    currentXY[0] = moveX;
                    currentXY[1] = moveY;
                }else if (currentStatus == RIGHT_SCALE){
                    if (move != move_bigX) {
                        allWidth += move_bigX;
                        float width = basicBitmap.getWidth() * scaleX;
                        Log.d(TAG, "onTouchEvent: width = " + (width + move_bigX));
                        Log.d(TAG, "onTouchEvent: basic = " + basicBitmap.getWidth());
                        scaleX = (width + move_bigX) / basicBitmap.getWidth();
                        if (scaleX <= MIN_SCALE){
                            scaleX = MIN_SCALE;
                        }
                        if (scaleX >= MAX_SCALE){
                            scaleX = MAX_SCALE;
                        }
                        Log.d(TAG, "onTouchEvent: "+scaleX);
                        if (currentType == QR_CODE){
                            scaleY = scaleX;
                        }
                        layout(getLeft(),getTop(),(int) (getLeft() + (basicBitmap.getWidth() + bottomSize / 2) * scaleX), (int) (getTop() + (basicBitmap.getHeight() + bottomSize / 2) * scaleY));
                        postInvalidate();
                        move = move_bigX;
                    }
                }else if (currentStatus == BOTTOM_SCALE){


                }

                break;
            case MotionEvent.ACTION_UP:
                currentStatus = INIT_STATUS;
                break;
        }
        return true;
    }

    private void judgeStatus(int rawX, int rawY) {
        Log.d(TAG, "judgeStatus: x ="+rawX);
        Log.d(TAG, "judgeStatus: y = "+rawY);
        boolean flag = false;
        int width = (int) (basicBitmap.getWidth() * scaleX+ bottomSize);
        int height = (int) (basicBitmap.getHeight() * scaleY + bottomSize);
        Log.d(TAG, "judgeStatus: "+width);
        Log.d(TAG, "judgeStatus: "+height);
        if (!flag){
            Log.d(TAG, "judgeStatus: "+(width - bottomSize) + " < " + rawX + " < " + width);
            Log.d(TAG, "judgeStatus: "+((height - bottomSize) / 2) + " < " + rawY + " < " + (height + bottomSize) / 2);
            if (rawX > (width - bottomSize) &&
                    rawX < width &&
                    rawY > (height - bottomSize) / 2 &&
                    rawY < (height + bottomSize) / 2){
                flag = true;
                currentStatus = RIGHT_SCALE;
                Log.d(TAG, "judgeStatus: 当前模式为：右边拉伸");
            }
            if (!flag){
                Log.d(TAG, "judgeStatus: "+((width - bottomSize) / 2) + " < " + rawX + " < " + ((width + bottomSize) / 2));
                Log.d(TAG, "judgeStatus: "+(height - bottomSize) + " < " + rawY + " < " + height);
                if (rawX > (width - bottomSize) / 2 &&
                        rawX < (width + bottomSize) / 2 &&
                        rawY > (height - bottomSize) &&
                        rawY < height){
                    flag = true;
                    currentStatus = BOTTOM_SCALE;
                    Log.d(TAG, "judgeStatus: 当前模式为：下边拉伸");
                }
            }
            if (!flag){
                currentStatus = DRAG_STATUS;
                Log.d(TAG, "judgeStatus: 当前模式为：移动");
            }
        }
    }

    public void setScale(float scale){

    }

    public void clean(){
        if (basicBitmap != null && !basicBitmap.isRecycled()) {
            basicBitmap.recycle();
            basicBitmap = null;
        }
    } //回收图片

}
