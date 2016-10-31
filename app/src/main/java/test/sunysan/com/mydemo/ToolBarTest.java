package test.sunysan.com.mydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import activity.BaseMenuToolBarActivity;
import view.HeadToolBar;

/**
 * ToolBar 使用
 * Created by SunySan on 2016/10/26.
 */
public class ToolBarTest extends BaseMenuToolBarActivity implements HeadToolBar.ToolBarPopInterface {

    HeadToolBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_layout);

        bar = (HeadToolBar) findViewById(R.id.head_toolbar);
        bar.setToolBarPopInterface(this);

//        bar.setNavigationIcon(R.drawable.head_back);
//        bar.setCenterTitle("我的一个主标题");
//        bar.setNavigationIconGone();
//        bar.setTitle("我的一个主标题");
        bar.setRight1(null, R.mipmap.ic_tr_point,onClick);
//        bar.setRight2(R.string.submit, null, onClick);
//        bar.setRight3(null, R.mipmap.ic_app, onClick);


//        bar.setRightImage(0).setVisibility(View.GONE);
//        bar.setRightImage(1).setVisibility(View.GONE);


//        bar.setRightImage(0,R.mipmap.announce).setVisibility(View.GONE);
//        bar.setRightImage(2,R.mipmap.announce).setVisibility(View.GONE);
//        bar.setRightTextColor(1,R.color.toolbar_white).setVisibility(View.GONE);
        bar.setTitle("我的一个主标题");
//        bar.setCenterSubtitle("附属标题");
//        bar.setSubtitleTextColor(getResources().getColor(R.color.toolbar_black));
//        bar.setCenterTitle("中间标题");
        //设置标题Logo
//        bar.setLogo(R.mipmap.ic_app);
        //设置标题颜色
//        bar.setTitleColor(R.color.toolbar_white);
        //设置toolbar背景颜色
//        bar.setBgColor(R.color.toolbar_theme);
//        设置导航按钮点击事件【注意：测试过，如果直接使用公用的onclick来作为点击事件操作的话，是不会响应的】
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "sss", Toast.LENGTH_LONG).show();
                finish();
            }
        });
//        bar.reSetHeadToolBarHeight(52);

        /**
         * 设置菜单menu的时候必须添加
         */
//        setSupportActionBar(bar);
//        bar.setOverflowIconChange(R.mipmap.video_search);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_pop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_r_img:
                bar.popShow(ToolBarTest.this, tests.sunysan.com.headtoolbar.R.layout.menu_popup_dialog);

                break;
            case R.id.action_scan:
                Toast.makeText(getBaseContext(), "点击了扫描", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_add:
                Toast.makeText(getBaseContext(), "点击了添加", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_item1:
                Toast.makeText(getBaseContext(), "点击了菜单1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_item2:
                Toast.makeText(getBaseContext(), "点击了菜单2", Toast.LENGTH_SHORT).show();
                break;

//            case R.id.toolbar_r_1:
//                Toast.makeText(getBaseContext(),"搜索",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.toolbar_r_2:
//                Toast.makeText(getBaseContext(),"收藏",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.toolbar_r_3:
//                Toast.makeText(getBaseContext(),"附近",Toast.LENGTH_SHORT).show();
//                break;

            case R.id.video_popupwindow_search:
                Toast.makeText(getBaseContext(), "搜索", Toast.LENGTH_SHORT).show();

                break;
            case R.id.video_popupwindow_collection:
                Toast.makeText(getBaseContext(), "收藏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_popupwindow_nearby:
                Toast.makeText(getBaseContext(), "附近", Toast.LENGTH_SHORT).show();
                break;


            default:
                break;
        }
        return true;

    }


    public View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.string.submit:
                    Toast.makeText(getBaseContext(), "提交", Toast.LENGTH_SHORT).show();
                    break;

                case R.mipmap.ic_tr_point:
                    bar.popShow(ToolBarTest.this, tests.sunysan.com.headtoolbar.R.layout.menu_popup_dialog);

//                    Toast.makeText(getBaseContext(), "删除", Toast.LENGTH_SHORT).show();
                    break;

                case R.mipmap.ic_app:
                    Toast.makeText(getBaseContext(), "图片", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.video_popupwindow_search:
                    Toast.makeText(getBaseContext(), "搜索onClick", Toast.LENGTH_SHORT).show();
                    bar.dimissPop();
                    break;
                case R.id.video_popupwindow_collection:
                    Toast.makeText(getBaseContext(), "收藏onClick", Toast.LENGTH_SHORT).show();
                    bar.dimissPop();
                    break;
                case R.id.video_popupwindow_nearby:
                    Toast.makeText(getBaseContext(), "附近onClick", Toast.LENGTH_SHORT).show();
                    bar.dimissPop();
                    break;

            }
        }
    };

    public void popShow() {

        /**
         * 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
         * 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
         * bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
         * 否则阴影的宽度为offset.
         */
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //        状态栏高度：frame.top
        int xOffset = frame.top + HeadToolBar.TOOLBAR_HEIGHT;//减去阴影宽度，适配UI.
        int yOffset = Dp2Px(this, 5f); //设置x方向offset为5dp
        View parentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        View popView = getLayoutInflater().inflate(
                R.layout.menu_popup_dialog, null);
        PopupWindow popWind = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);//popView即popupWindow的布局，ture设置focusAble.

        //必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效。这里在XML中定义背景，所以这里设置为null;
        popWind.setBackgroundDrawable(new BitmapDrawable(getResources(),
                (Bitmap) null));
        popWind.setOutsideTouchable(true); //点击外部关闭。
        popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
        //设置Gravity，让它显示在右上角。
        popWind.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP,
                yOffset, xOffset);

    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    public void toolBarInitView(View v) {
        LinearLayout searchLay, collectionLay, nearlyLay;
        searchLay = (LinearLayout) v.findViewById(R.id.video_popupwindow_search);
        collectionLay = (LinearLayout) v.findViewById(R.id.video_popupwindow_collection);
        nearlyLay = (LinearLayout) v.findViewById(R.id.video_popupwindow_nearby);

        searchLay.setOnClickListener(onClick);
        collectionLay.setOnClickListener(onClick);
        nearlyLay.setOnClickListener(onClick);
    }




}
