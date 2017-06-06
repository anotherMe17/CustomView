package io.github.anotherme17.digit;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */

public class ADigit extends View implements Runnable {

    private final static int DOWN = 0;
    private final static int MIDDLE = 1;
    private final static int TOP = 2;

    private int state = DOWN;
    private boolean isUp = true;

    long mTime = -1;
    float mElapsedTime = 500.0f;

    private Tab mTopTab;

    private Tab mBottomTab;

    private Tab mMiddleTab;

    private List<Tab> tabs = new ArrayList<>(3);

    private Matrix mProjectionMatrix = new Matrix();

    private int mAlpha = 0;

    private int mCornerSize;

    private Paint mNumberPaint;

    private Paint mDividerPaint;

    private Paint mBackgroundPaint;

    private Rect mTextMeasured = new Rect();

    private int mPadding = 0;

    private char[] mChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public ADigit(Context context) {
        this(context, null);
    }

    public ADigit(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ADigit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ADigit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        initPaints();

        int padding = -1;
        int textSize = -1;
        int cornerSize = -1;
        int textColor = 1;
        int backgroundColor = 1;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ADigit, 0, 0);
        final int num = ta.getIndexCount();
        for (int i = 0; i < num; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.ADigit_textSize) {
                textSize = ta.getDimensionPixelSize(attr, -1);
            } else if (attr == R.styleable.ADigit_padding) {
                padding = ta.getDimensionPixelSize(attr, -1);
            } else if (attr == R.styleable.ADigit_cornerSize) {
                cornerSize = ta.getDimensionPixelSize(attr, -1);
            } else if (attr == R.styleable.ADigit_textColor) {
                textColor = ta.getColor(attr, 1);
            } else if (attr == R.styleable.ADigit_backgroundColor) {
                backgroundColor = ta.getColor(attr, 1);
            }
        }
        ta.recycle();

        if (padding > 0) {
            mPadding = padding;
        }

        if (textSize > 0) {
            mNumberPaint.setTextSize(textSize);
        }

        if (cornerSize > 0) {
            mCornerSize = cornerSize;
        }

        if (textColor < 1) {
            mNumberPaint.setColor(textColor);
        }

        if (backgroundColor < 1) {
            mBackgroundPaint.setColor(backgroundColor);
        }

        initTabs();
    }

    private void initPaints() {
        mNumberPaint = new Paint();
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNumberPaint.setColor(Color.WHITE);

        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDividerPaint.setColor(Color.WHITE);
        mDividerPaint.setStrokeWidth(4);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.BLACK);
    }

    private void initTabs() {
        // top Tab
        mTopTab = new Tab();
        mTopTab.rotate(180);
        tabs.add(mTopTab);

        // bottom Tab
        mBottomTab = new Tab();
        tabs.add(mBottomTab);

        // middle Tab
        mMiddleTab = new Tab();
        tabs.add(mMiddleTab);

        setInternalChar(0);
    }

    public void setChar(int index) {
        setInternalChar(index);
        invalidate();
    }

/*    public void setCharNext(int currentIndex) {
        setInternalChar(++currentIndex > mChars.length ? 0 : currentIndex);
        invalidate();
    }

    public void setCharPre(int currentIndex) {
        setInternalChar(--currentIndex < 0 ? mChars.length - 1 : currentIndex);
        invalidate();
    }*/

    private void setInternalChar(int index) {
        for (Tab tab : tabs) {
            tab.setChar(index);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*计算出所包含的边界*/
        calculateTextSize(mTextMeasured);

        int childWidth = mTextMeasured.width() + mPadding;
        int childHeight = mTextMeasured.height() + mPadding;

        /*计算出所有Tab的边界*/
        measureTabs(childWidth, childHeight);

        /*
        * 通过MiddleTab获取控件最大的边界
        */
        int maxChildWidth = mMiddleTab.maxWith();
        int maxChildHeight = 2 * mMiddleTab.maxHeight();

        int resolvedWidth = resolveSize(maxChildWidth, widthMeasureSpec);
        int resolvedHeight = resolveSize(maxChildHeight, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            setupProjectionMatrix();
        }
    }

    private void setupProjectionMatrix() {
        mProjectionMatrix.reset();
        int centerY = getHeight() / 2;
        int centerX = getWidth() / 2;
        MatrixHelper.translate(mProjectionMatrix, centerX, -centerY, 0);
    }

    private void measureTabs(int width, int height) {
        for (Tab tab : tabs) {
            tab.measure(width, height);
        }
    }

    private void drawTabs(Canvas canvas) {
        for (Tab tab : tabs) {
            tab.draw(canvas);
        }
    }

    private void drawDivider(Canvas canvas) {
        canvas.save();
        canvas.concat(mProjectionMatrix);
        canvas.drawLine(-canvas.getWidth() / 2, 0, canvas.getWidth() / 2, 0, mDividerPaint);
        canvas.restore();
    }


    private void calculateTextSize(Rect rect) {
        mNumberPaint.getTextBounds("8", 0, 1, rect);
    }

    /**
     * 设置文字大小
     * @param size
     */
    public void setTextSize(int size) {
        mNumberPaint.setTextSize(size);
        requestLayout();
    }

    public int getTextSize() {
        return (int) mNumberPaint.getTextSize();
    }

    /**
     * 设置字体
     *
     * @param font
     */
    public void setFont(Typeface font) {
        mNumberPaint.setTypeface(font);
        invalidate();
    }

    /**
     * 设置padding
     *
     * @param padding
     */
    public void setPadding(int padding) {
        mPadding = padding;
        requestLayout();
    }

    /**
     * 设置显示的字符,只允许单个字符
     *
     * @param chars
     */
    public void setChars(char[] chars) {
        mChars = chars;
    }

    public char[] getChars() {
        return mChars;
    }


    /**
     * 设置分割线的颜色
     *
     * @param color
     */
    public void setDividerColor(int color) {
        mDividerPaint.setColor(color);
    }

    /**
     * 设置分割线的宽度
     *
     * @param width
     */
    public void serDividerWidth(int width) {
        mDividerPaint.setStrokeWidth(width);
    }

    public int getPadding() {
        return mPadding;
    }

    /**
     * 设置字体颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mNumberPaint.setColor(color);
    }

    public int getTextColor() {
        return mNumberPaint.getColor();
    }

    /**
     * 设置背景弧度
     *
     * @param cornerSize
     */
    public void setCornerSize(int cornerSize) {
        mCornerSize = cornerSize;
        invalidate();
    }

    public int getCornerSize() {
        return mCornerSize;
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    public int getBackgroundColor() {
        return mBackgroundPaint.getColor();
    }

    /**
     * 下一个
     */
    public void next() {
        isUp = true;
        makeSureCycleIsClosed();
        mTime = System.currentTimeMillis();
        invalidate();
    }

    /**
     * 上一个
     */
    public void pre() {
        isUp = false;
        makeSureCycleIsClosed();
        mTime = System.currentTimeMillis();
        invalidate();
    }

    private void makeSureCycleIsClosed() {
        if (mTime == -1) {
            state = isUp ? DOWN : TOP;
            return;
        }

        if (isUp) {
            switch (state) {
                case DOWN: {
                    middle2Next();
                }
                case TOP: {
                    top2Next();
                }
            }
            mMiddleTab.rotate(180);
        } else {
            switch (state) {
                case TOP: {
                    middle2Next();
                }
                case DOWN: {
                    bottom2Next();
                }
            }
            mMiddleTab.rotate(0);
        }
    }

    private void bottom2Next() {
        if (isUp) {
            mBottomTab.next();
            state = MIDDLE;
        } else {
            mBottomTab.pre();
            state = TOP;
            mTime = -1;
        }
    }

    private void middle2Next() {
        if (isUp) {
            mMiddleTab.next();
            state = TOP;
        } else {
            mMiddleTab.pre();
            state = DOWN;
        }
    }

    private void top2Next() {
        if (isUp) {
            mTopTab.next();
            state = DOWN;
            mTime = -1;
        } else {
            mTopTab.pre();
            state = MIDDLE;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTabs(canvas);
        drawDivider(canvas);
        ViewCompat.postOnAnimationDelayed(this, this, 40);
    }

    @Override
    public void run() {
        if (mTime == -1) {
            return;
        }

        if (isUp) {
            switch (state) {
                case DOWN: {
                    bottom2Next();
                    break;
                }
                case MIDDLE: {
                    if (mAlpha > 90) {
                        middle2Next();
                    }
                    break;
                }
                case TOP: {
                    if (mAlpha >= 180) {
                        top2Next();
                    }
                    break;
                }
            }
        } else {
            switch (state) {
                case DOWN: {
                    if (mAlpha <= 0)
                        bottom2Next();
                    break;
                }
                case MIDDLE: {
                    if (mAlpha < 90) {
                        middle2Next();
                    }
                    break;
                }
                case TOP: {
                    top2Next();
                    break;
                }
            }
        }

        if (mTime != -1) {
            long delta = (System.currentTimeMillis() - mTime);
            mAlpha = isUp ? (int) (180 * (1 - (1 * mElapsedTime - delta) / (1 * mElapsedTime)))
                    : (int) (180 * ((1 * mElapsedTime - delta) / (1 * mElapsedTime)));
            mMiddleTab.rotate(mAlpha);
        }
        invalidate();
    }

    public void sync() {
        makeSureCycleIsClosed();
        invalidate();
    }


    public class Tab {

        private final Matrix mModelViewMatrix = new Matrix();

        private final Matrix mModelViewProjectionMatrix = new Matrix();

        private final Matrix mRotationModelViewMatrix = new Matrix();

        private final RectF mStartBounds = new RectF();

        private final RectF mEndBounds = new RectF();

        private int mCurrIndex = 0;

        private int mAlpha;

        private Matrix mMeasuredMatrixHeight = new Matrix();

        private Matrix mMeasuredMatrixWidth = new Matrix();

        public void measure(int width, int height) {
            Rect area = new Rect(-width / 2, 0, width / 2, height / 2);
            mStartBounds.set(area);
            mEndBounds.set(area);
            mEndBounds.offset(0, -height / 2);
        }

        public int maxWith() {
            RectF rect = new RectF(mStartBounds);
            Matrix projectionMatrix = new Matrix();
            /*根据mStartBounds 初始化矩阵,计算旋转90度后的宽度*/
            MatrixHelper.translate(projectionMatrix, mStartBounds.left, -mStartBounds.top, 0);
            mMeasuredMatrixWidth.reset();
            mMeasuredMatrixWidth.setConcat(projectionMatrix, MatrixHelper.ROTATE_X_90);
            mMeasuredMatrixWidth.mapRect(rect);
            return (int) rect.width();
        }

        public int maxHeight() {
            RectF rect = new RectF(mStartBounds);
            Matrix projectionMatrix = new Matrix();
            /*根据mStartBounds 计算旋转0度后的高度*/
            mMeasuredMatrixHeight.reset();
            mMeasuredMatrixHeight.setConcat(projectionMatrix, MatrixHelper.ROTATE_X_0);
            mMeasuredMatrixHeight.mapRect(rect);
            return (int) rect.height();
        }

        public void setChar(int index) {
            mCurrIndex = index > mChars.length ? 0 : index;
        }

        public int getChar() {
            return mCurrIndex;
        }

        public void pre() {
            mCurrIndex--;
            if (mCurrIndex < 0) {
                mCurrIndex = mChars.length - 1;
            }
        }

        public void next() {
            mCurrIndex++;
            if (mCurrIndex >= mChars.length) {
                mCurrIndex = 0;
            }
        }

        public void rotate(int alpha) {
            mAlpha = alpha;
            MatrixHelper.rotateX(mRotationModelViewMatrix, alpha);
        }

        public void draw(Canvas canvas) {
            drawBackground(canvas);
            drawText(canvas);
        }

        private void drawBackground(Canvas canvas) {
            canvas.save();
            mModelViewMatrix.set(mRotationModelViewMatrix);
            applyTransformation(canvas, mModelViewMatrix);
            canvas.drawRoundRect(mStartBounds, mCornerSize, mCornerSize, mBackgroundPaint);
            canvas.restore();
        }

        private void drawText(Canvas canvas) {
            canvas.save();
            mModelViewMatrix.set(mRotationModelViewMatrix);
            RectF clip = mStartBounds;
            if (mAlpha > 90) {
                mModelViewMatrix.setConcat(mModelViewMatrix, MatrixHelper.MIRROR_X);
                clip = mEndBounds;
            }
            applyTransformation(canvas, mModelViewMatrix);
            canvas.clipRect(clip);
            canvas.drawText(Character.toString(mChars[mCurrIndex]), 0, 1, -mTextMeasured.centerX(), -mTextMeasured.centerY(), mNumberPaint);
            canvas.restore();
        }

        private void applyTransformation(Canvas canvas, Matrix matrix) {
            mModelViewProjectionMatrix.reset();
            mModelViewProjectionMatrix.setConcat(mProjectionMatrix, matrix);
            canvas.concat(mModelViewProjectionMatrix);
        }
    }
}
