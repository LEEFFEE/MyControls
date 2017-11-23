package cn.leeffee.mycontrols;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import cn.leeffee.library.widget.BubbleWindow;
import cn.leeffee.library.widget.CircleProgressButton;
import cn.leeffee.library.widget.DialogBuilder;

public class MainActivity extends AppCompatActivity {

    FrameLayout mLayout;
    Button mButton;
    CircleProgressButton mCircleProgressButton;
    public static final int PROGRESS_CIRCLE_STARTING = 0x110;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS_CIRCLE_STARTING:
                    int progress = mCircleProgressButton.getProgress();
                    mCircleProgressButton.setProgress(++progress);
                    if (progress >= 100) {
                        handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                        mCircleProgressButton.setProgress(0);
                        mCircleProgressButton.setStatus(CircleProgressButton.Status.FINISH);//修改显示状态为完成
                    } else {
                        //延迟100ms后继续发消息，实现循环，直到progress=100
                        handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, 500);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mButton = (Button) findViewById(R.id.btnPopup);
        mCircleProgressButton = (CircleProgressButton) findViewById(R.id.circleProgressButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*自定义弹窗*/
                // openBubble();
        /*测试对话框*/
                //  openDialog();

                // rxJava2();
                //  hook();
            }
        });
        mCircleProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCircleProgressButton.getStatus() == CircleProgressButton.Status.WAIT) {
                    //点击则变成关闭暂停状态
                    mCircleProgressButton.setStatus(CircleProgressButton.Status.RUN);
                    Message message = Message.obtain();
                    message.what = PROGRESS_CIRCLE_STARTING;
                    handler.sendMessage(message);
                } else if (mCircleProgressButton.getStatus() == CircleProgressButton.Status.RUN) {//如果是开始状态
                    //点击则变成关闭暂停状态
                    mCircleProgressButton.setStatus(CircleProgressButton.Status.PAUSE);
                    //注意，当我们暂停时，同时还要移除消息，不然的话进度不会被停止
                    handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                } else {
                    //点击则变成开启状态
                    mCircleProgressButton.setStatus(CircleProgressButton.Status.RUN);
                    Message message = Message.obtain();
                    message.what = PROGRESS_CIRCLE_STARTING;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void rxJava2() {
        //        Observable.create(new ObservableOnSubscribe<String>() {
        //
        //            @Override
        //            public void subscribe(ObservableEmitter<String> e) throws Exception {
        //                e.onNext("gfd");
        //            }
        //        }).subscribeOn(Schedulers.io())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .doOnNext(new Consumer<String>() {
        //                    @Override
        //                    public void accept(@NonNull String s) throws Exception {
        //                        LogUtil.e(".doOnNext(", s);
        //                    }
        //                })
        //                .subscribe(new Consumer<String>() {
        //                    @Override
        //                    public void accept(@NonNull String s) throws Exception {
        //                        LogUtil.e(".subscribe", s);
        //                    }
        //                });
    }

    private void openDialog() {
        DialogBuilder mBuilder = new DialogBuilder(this);
        mBuilder.setTitle("提示");
        mBuilder.setLayout(300, WindowManager.LayoutParams.WRAP_CONTENT);
        mBuilder.setGravity(Gravity.BOTTOM | Gravity.END);
        mBuilder.setAlpha(0.6f);
        mBuilder.setItems(new String[]{"sdgfdg", "dfgfdg"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mBuilder.show();
    }

    private void openBubble() {
        BubbleWindow window = new BubbleWindow(this);
        View bubbleView = LayoutInflater.from(this).inflate(R.layout.popup_share_window, null);
        window.setBubbleView(bubbleView); // 设置气泡内容
        window.show(mButton, Gravity.TOP, 100); //显示弹窗
    }
}
