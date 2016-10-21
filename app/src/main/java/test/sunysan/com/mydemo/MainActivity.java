package test.sunysan.com.mydemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.sunysan.headportrait.impl.HeadPortraitImp;
import com.sunysan.headportrait.activity.ImageActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,HeadPortraitImp.ActivityResultInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_app);
        toolbar.setTitle("我的一个APP");
        toolbar.setSubtitle("副标题");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.announce);

        //显示邮件的图标，点击在底部显示一个snackbar；
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "自己添加自己所要的行为", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);


        //我的理解是退出当前activity就调用，测试了下，点击返回退出程序时
        //回调用onBackPressed()方法，弹出提示“drawer.closeDrawer（非）”
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //侧滑导航
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
    }

    ImageView image;
//    ImageView myIcon;
    HeadPortraitImp imp;

    private void initView() {
        image = (ImageView) findViewById(R.id.imagetest);
//        myIcon = (ImageView) findViewById(R.id.my_icon);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imp = new HeadPortraitImp(MainActivity.this, MainActivity.this);
                imp.showPopupWindow();
            }
        });


    }


//    HeadPortraitImp.ActivityResultInterface resultInterface = new HeadPortraitImp.ActivityResultInterface() {
        @Override
        public void startActivityResult(Intent intent, int reqCode) {
            startActivityForResult(intent, reqCode);
        }

        @Override
        public void setBitmap(Bitmap mBitmap) {
            if (mBitmap != null) {
                image.setImageBitmap(mBitmap);
            }
        }
//
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imp.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 退出时走“drawer.closeDrawer（非）”
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Toast.makeText(MainActivity.this, "drawer.closeDrawer", Toast.LENGTH_SHORT).show();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(MainActivity.this, "drawer.closeDrawer（非）", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //也就是菜单按钮有的话就在这里设置
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings2) {
            Toast.makeText(MainActivity.this, "菜单2", Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(MainActivity.this, ImageActivity.class);
            startActivity(i);
            Toast.makeText(MainActivity.this, "图集", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "视频", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(MainActivity.this, "工具/设置", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "分享", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(MainActivity.this, "发送", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
