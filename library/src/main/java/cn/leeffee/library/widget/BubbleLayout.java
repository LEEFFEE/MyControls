package cn.leeffee.library.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.leeffee.library.R;
import cn.leeffee.library.utils.DensityUtil;

/**
 * Created by lhfei on 2017/7/12.
 * 气泡布局
 */

public class BubbleLayout extends RelativeLayout {
    //    Constructor->
    //    onFinishInflate->
    //    onMeasure..->
    //    onSizeChanged->
    //    onLayout->
    //    addOnGlobalLayoutListener->
    //    onWindowFocusChanged->
    //    onMeasure->
    //    onLayout
    //    由上可知,onMeasure和onLayout会被多次调用.


    /**
     * 气泡(腿)尖角方向
     */
    public enum BubbleLegOrientation {
        TOP, LEFT, RIGHT, BOTTOM, NONE
    }

    public int PADDING = DensityUtil.dp2px(getContext(), 10);
    public int LEG_HALF_BASE = DensityUtil.dp2px(getContext(), 10);
    public int STROKE_WIDTH = DensityUtil.dp2px(getContext(), 1);
    public int CORNER_RADIUS = DensityUtil.dp2px(getContext(), 2);
    public int SHADOW_COLOR = Color.argb(100, 0, 0, 0);
    public int MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;

    private Paint mFillPaint = null;
    private final Path mPath = new Path();
    private final Path mBubbleLegPrototype = new Path();
    private RectF mRectF = new RectF();
    private final Paint mPaint = new Paint(Paint.DITHER_FLAG);

    private int mBubbleLegOffset = DensityUtil.dp2px(getContext(), 10);
    private BubbleLegOrientation mBubbleOrientation = BubbleLegOrientation.LEFT;

    public BubbleLayout(Context context) {
        this(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayout);
            try {
                SHADOW_COLOR = a.getInt(R.styleable.BubbleLayout_shadowColor, SHADOW_COLOR);
                PADDING = a.getDimensionPixelSize(R.styleable.BubbleLayout_padding, PADDING);
                LEG_HALF_BASE = a.getDimensionPixelSize(R.styleable.BubbleLayout_halfBaseOfLeg, LEG_HALF_BASE);
                MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;
                STROKE_WIDTH = a.getDimensionPixelSize(R.styleable.BubbleLayout_strokeWidth, STROKE_WIDTH);
                CORNER_RADIUS = a.getDimensionPixelSize(R.styleable.BubbleLayout_cornerRadius, CORNER_RADIUS);

                int bubbleLegOrientation = a.getInt(R.styleable.BubbleLayout_bubbleLegOrientation, 0);
                switch (bubbleLegOrientation) {
                    case 1://上
                        mBubbleOrientation = BubbleLegOrientation.TOP;
                        break;
                    case 2://下
                        mBubbleOrientation = BubbleLegOrientation.BOTTOM;
                        break;
                    case 3://左
                        mBubbleOrientation = BubbleLegOrientation.LEFT;
                        break;
                    case 4://右
                        mBubbleOrientation = BubbleLegOrientation.RIGHT;
                        break;
                }
                mBubbleLegOffset = a.getDimensionPixelSize(R.styleable.BubbleLayout_bubbleLegOffset, mBubbleLegOffset);
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }

        mPaint.setColor(SHADOW_COLOR);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setPathEffect(new CornerPathEffect(STROKE_WIDTH));

        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        }

        mFillPaint = new Paint(mPaint);
        mFillPaint.setColor(Color.WHITE);
        mFillPaint.setShader(new LinearGradient(
                DensityUtil.dp2px(getContext(), 100), DensityUtil.dp2px(getContext(), 0), DensityUtil.dp2px(getContext(), 100), DensityUtil.dp2px(getContext(), 200),
                Color.WHITE, Color.WHITE, Shader.TileMode.CLAMP));

        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, mFillPaint);
        }
        mPaint.setShadowLayer(DensityUtil.dp2px(getContext(), 2), DensityUtil.dp2px(getContext(), 2), DensityUtil.dp2px(getContext(), 3), SHADOW_COLOR);
        renderBubbleLegPrototype();
        setPadding(PADDING, PADDING, PADDING, PADDING);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 尖角path
     */
    private void renderBubbleLegPrototype() {
        mBubbleLegPrototype.moveTo(0, 0);
        mBubbleLegPrototype.lineTo(PADDING * 1.5f, -PADDING / 1.5f);
        mBubbleLegPrototype.lineTo(PADDING * 1.5f, PADDING / 1.5f);
        mBubbleLegPrototype.close();
    }

    public void setBubbleParams(BubbleLegOrientation bubbleOrientation, int bubbleOffset) {
        mBubbleLegOffset = bubbleOffset;
        mBubbleOrientation = bubbleOrientation;
    }

    /**
     * 根据显示方向，获取尖角位置矩阵
     */
    private Matrix renderBubbleLegMatrix(float width, float height) {

        float offset = Math.max(mBubbleLegOffset, MIN_LEG_DISTANCE);
        float dstX = 0;
        float dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
        final Matrix matrix = new Matrix();

        switch (mBubbleOrientation) {
            case TOP:
                dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
                dstY = 0;
                matrix.postRotate(90);
                break;

            case RIGHT:
                dstX = width;
                dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
                matrix.postRotate(180);
                break;

            case BOTTOM:
                dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
                dstY = height;
                matrix.postRotate(270);
                break;
        }

        matrix.postTranslate(dstX, dstY);
        return matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        //        reset()，清空path路径中的信息。
        //        rewind()，rewind当前path，清除掉所有直线，曲线，但是保留它内部的数据结构，以便更好的重新使用
        mPath.rewind();
        mRectF.set(PADDING, PADDING, width - PADDING, height - PADDING);
        mPath.addRoundRect(mRectF, CORNER_RADIUS, CORNER_RADIUS, Path.Direction.CW);
        mPath.addPath(mBubbleLegPrototype, renderBubbleLegMatrix(width, height));

        canvas.drawPath(mPath, mPaint);
        canvas.scale((width - STROKE_WIDTH) / width, (height - STROKE_WIDTH) / height, width / 2f, height / 2f);

        canvas.drawPath(mPath, mFillPaint);
    }
}
