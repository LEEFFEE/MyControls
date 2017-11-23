package cn.leeffee.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by lhfei on 2017/7/12.
 * 气泡窗体
 */

public class BubbleWindow extends PopupWindow {

    private BubbleLayout bubbleView;
    private Context context;

    public BubbleWindow(Context context) {
        this.context = context;
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(false);
        setClippingEnabled(false);

        ColorDrawable dw = new ColorDrawable(0);
        setBackgroundDrawable(dw);
    }

    public void setBubbleView(View view) {
        bubbleView = new BubbleLayout(context);
        bubbleView.setBackgroundColor(Color.TRANSPARENT);
        bubbleView.addView(view);
        setContentView(bubbleView);
    }

    public void setParam(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void show(View parent) {
        show(parent, Gravity.TOP, getMeasuredWidth() / 2);
    }

    public void show(View parent, int gravity) {
        show(parent, gravity, getMeasuredWidth() / 2);
    }

    /**
     * 显示弹窗
     *
     * @param parent
     * @param gravity
     * @param bubbleOffset 气泡尖角位置偏移量。默认位于中间
     */
    public void show(View parent, int gravity, int bubbleOffset) {
        BubbleLayout.BubbleLegOrientation orientation = BubbleLayout.BubbleLegOrientation.LEFT;
        if (!this.isShowing()) {
            switch (gravity) {
                case Gravity.BOTTOM:
                    orientation = BubbleLayout.BubbleLegOrientation.TOP;
                    break;
                case Gravity.TOP:
                    orientation = BubbleLayout.BubbleLegOrientation.BOTTOM;
                    break;
                case Gravity.RIGHT:
                    orientation = BubbleLayout.BubbleLegOrientation.LEFT;
                    break;
                case Gravity.LEFT:
                    orientation = BubbleLayout.BubbleLegOrientation.RIGHT;
                    break;
                default:
                    break;
            }
            bubbleView.setBubbleParams(orientation, bubbleOffset); // 设置气泡布局方向及尖角偏移

            int[] location = new int[2];
            parent.getLocationOnScreen(location);

            switch (gravity) {
                case Gravity.BOTTOM:
                    showAsDropDown(parent);
                    break;
                case Gravity.TOP:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - getMeasureHeight());
                    break;
                case Gravity.RIGHT:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth(), location[1] - (parent.getHeight() / 2));
                    break;
                case Gravity.LEFT:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - getMeasuredWidth(), location[1] - (parent.getHeight() / 2));
                    break;
                default:
                    break;
            }
        } else {
            this.dismiss();
        }
    }

    /**
     * 测量高度
     * @return
     */
    public int getMeasureHeight() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popHeight = getContentView().getMeasuredHeight();
        return popHeight;
    }

    /**
     * 测量宽度
     * @return
     */
    public int getMeasuredWidth() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popWidth = getContentView().getMeasuredWidth();
        return popWidth;
    }
}
