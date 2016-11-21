package com.sunysan.headportrait.impl;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sunysan.headportrait.activity.ClipImageActivity;
import com.sunysan.headportrait.view.ClipImageLayout;

import java.io.File;
import java.io.FileNotFoundException;

import test.sunysan.com.headportraittools.R;

/**
 * 头像选择实现类
 * Created by SunySan on 2016/10/17.
 */
public class HeadPortraitImp {
    public static final int START_ALBUM_REQUESTCODE = 8888;
    public static final int CAMERA_WITH_DATA = START_ALBUM_REQUESTCODE + 1;
    public static final int CROP_RESULT_CODE = CAMERA_WITH_DATA + 1;
    public static final String TMP_PATH = "/clip_head.jpg";

    private PopupWindow popupWindow;
    private Context context;
    private ActivityResultInterface resultInterface;
    private TextView isCircularText;


    public HeadPortraitImp(Context context, ActivityResultInterface resultInterface) {
        this.context = context;
        this.resultInterface = resultInterface;

    }

    /**
     * 弹出框PopupWindow
     */
    public void showPopupWindow() {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.head_popupwindows, null);

        FrameLayout popuNull = (FrameLayout) rootView
                .findViewById(R.id.popu_null);
        Button popuPhotograph = (Button) rootView
                .findViewById(R.id.popu_photograph);
        Button popuAlbum = (Button) rootView.findViewById(R.id.popu_album);
        Button popuCancel = (Button) rootView.findViewById(R.id.popu_cancel);
        isCircularText = (TextView) rootView.findViewById(R.id.popu_circle);

        popuNull.setOnClickListener(onClick);
        popuPhotograph.setOnClickListener(onClick);
        popuAlbum.setOnClickListener(onClick);
        popuCancel.setOnClickListener(onClick);
        isCircularText.setOnClickListener(onClick);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(rootView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }

        // 获得焦点，否则无法点击
        popupWindow.setFocusable(true);
        // 点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 点击pop之外，pop可消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 绑定视图
        popupWindow.showAtLocation(rootView, Gravity.CENTER,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ClipImageLayout.isCcircle = false;
    }

    // 裁剪图片的Activity
    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity((Activity) context, path, CROP_RESULT_CODE);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.popu_null) {//按键外
                popupWindow.dismiss();
            } else if (id == R.id.popu_photograph) {//拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                        Environment.getExternalStorageDirectory(), TMP_PATH)));
                resultInterface.startActivityResult(intent, CAMERA_WITH_DATA);
                popupWindow.dismiss();
            } else if (id == R.id.popu_album) {//相册
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    resultInterface.startActivityResult(intent, START_ALBUM_REQUESTCODE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    try {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        resultInterface.startActivityResult(intent, START_ALBUM_REQUESTCODE);
                    } catch (Exception e2) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
                popupWindow.dismiss();
            } else if (id == R.id.popu_cancel) {// 取消
                popupWindow.dismiss();
            } else if (id == R.id.popu_circle) {
                if (!ClipImageLayout.isCcircle) {
                    isCircularText.setTextColor(context.getResources().getColor(R.color.use_circle));
                    ClipImageLayout.isCcircle = true;
                } else {
                    isCircularText.setTextColor(context.getResources().getColor(R.color.use_default));
                    ClipImageLayout.isCcircle = false;
                }
            }
        }
    };


    /**
     * 处理拍照和相册返回的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_RESULT_CODE:
                String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (resultInterface != null)
                    resultInterface.setBitmap(bitmap);//最后裁剪结果以bitmap的形式返回
//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                imageView.setImageBitmap(photo);
                break;
            case START_ALBUM_REQUESTCODE:
                startCropImageActivity(getFilePath(data.getData()));
                break;
            case CAMERA_WITH_DATA:
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                startCropImageActivity(Environment.getExternalStorageDirectory()
                        + TMP_PATH);
                break;

        }
    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
    public String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // 获取文件路径通过url
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        Cursor cursor = context.getContentResolver()
                .query(mUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }


    public interface ActivityResultInterface {
        void startActivityResult(Intent intent, int reqCode);

        void setBitmap(Bitmap mBitmap);
    }

}
