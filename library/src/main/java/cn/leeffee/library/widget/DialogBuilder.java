package cn.leeffee.library.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import cn.leeffee.library.R;


/**
 * Created by lhfei on 2017/7/13.
 */

public class DialogBuilder extends AlertDialog.Builder {
    WindowManager.LayoutParams mParams;

    public DialogBuilder(@NonNull Context context) {
        this(context, R.style.MultiFuncDialog);
    }

    public DialogBuilder(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    void init() {
        mParams = new WindowManager.LayoutParams();
    }

    /**
     * 设置对话框宽高
     */
    public DialogBuilder setLayout(int width, int height) {
        mParams.width = width;
        mParams.height = height;
        return this;
    }

    /**
     * 设置新位置
     */
    public DialogBuilder setLocation(int x, int y) {
        mParams.x = x; // 新位置X坐标
        mParams.y = y; // 新位置Y坐标
        return this;
    }

    /**
     * 设置Gravity
     */
    public DialogBuilder setGravity(int gravity) {
        mParams.gravity = gravity;
        return this;
    }

    /**
     * 设置对话框透明度
     */
    public DialogBuilder setAlpha(float alpha) {
        mParams.alpha = alpha;
        return this;
    }

    /**
     * 设置对话框弹出动画
     */
    public DialogBuilder setWindowAnimations(@StyleRes int styleId) {
        mParams.windowAnimations = styleId;
        return this;
    }

    @Override
    public AlertDialog show() {
        final AlertDialog dialog = create();
        Window window = dialog.getWindow();
        window.setAttributes(mParams);
        dialog.show();
        return dialog;
    }
}
