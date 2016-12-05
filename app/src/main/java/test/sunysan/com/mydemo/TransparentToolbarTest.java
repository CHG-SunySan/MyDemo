package test.sunysan.com.mydemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import interfaces.RightOnclick;
import utils.ToastUtils;
import utils.ToolBarStatusUtils;
import view.HeadToolBar;

/**
 * Created by SunySan on 2016/11/4.
 */
public class TransparentToolbarTest extends Activity {
    private Context mContext;
    HeadToolBar bar;
    private LinearLayout statusLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToolBarStatusUtils.setStatusBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transparent_toolbar);
        mContext = this;
        //初始化时设置高度
        ToolBarStatusUtils.statusHeight = ToolBarStatusUtils.getStatusBarH(this);
        initView();
    }

    private void initView(){
//        StatusBarUtil.setTranslucentDiff(this);
        statusLay = (LinearLayout) findViewById(R.id.status_height);
//        bar = (HeadToolBar) findViewById(R.id.head_toolbar);
//        bar.setBgColor(R.color.transparent);
//        bar.setRight1("提交", null, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getBaseContext(),"提交",Toast.LENGTH_SHORT).show();
//            }
//        });

//        (TextView) bar.getRightView(0).setTe;

//        bar.setRightView("提交", "取消", bar.TEXT,new RightOnclick() {
//            @Override
//            public void defaultOnClick() {
//                Toast.makeText(getBaseContext(),"提交",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void selectOnClick() {
//                Toast.makeText(getBaseContext(),"取消",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        bar.setRightView("提交", "取消", bar.TEXT,new RightOnclick() {
//            @Override
//            public void defaultOnClick() {
//                Toast.makeText(getBaseContext(),"提交",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void selectOnClick() {
//                Toast.makeText(getBaseContext(),"取消",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        bar.setRightChangeTextColor(HeadToolBar.POSITION_FIRST,R.color.toolbar_theme);
//        ToolBarStatusUtils.setStatusGone(statusLay,true,R.color.toolbar_theme);
        ToolBarStatusUtils.setColors(this,statusLay,R.color.toolbar_theme,true);

        TextView view = (TextView) findViewById(R.id.test_toast);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext,"显示有没有问题",Toast.LENGTH_SHORT).show();
                ToastUtils.show(mContext,"显示有没有问题");
            }
        });
    }
}
