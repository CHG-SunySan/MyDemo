package com.sunysan.headportrait.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.sunysan.headportrait.impl.HeadPortraitImp;
import com.sunysan.headportrait.tools.UserOperationUtil;
import com.sunysan.headportrait.view.ClipImageLayout;

import test.sunysan.com.headportraittools.R;

/**
 * 裁剪图片的Activity
 * Created by SunySan on 2016/10/16.
 */

public class ClipImageActivity extends Activity implements OnClickListener {
    public static final String RESULT_PATH = "crop_image";
    private static final String KEY = "path";
    private ClipImageLayout mClipImageLayout = null;

    private UserOperationUtil userOperationUtil;
    private int width;
    private int height;
    private int sampleSize = 1;
    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 384;
    private Bitmap bitmap;

    public static void startActivity(Activity activity, Uri path, int code) {
        Intent intent = new Intent(activity, ClipImageActivity.class);
//        intent.putExtra(KEY, path);
        intent.putExtra("uri",path);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_layout);

        userOperationUtil = new UserOperationUtil(this);
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
//        String path = getIntent().getStringExtra(KEY);
        Uri uri = getIntent().getParcelableExtra("uri");

        // 有的系统返回的图片是旋转了，有的没有旋转，所以处理
        int degreee = readBitmapDegree(userOperationUtil.getFilePath(uri));
//        bitmap = createBitmap(userOperationUtil.getFilePath(uri));
        boolean isBitmapRotate = false;
        if (bitmap != null) {
            if (degreee == 0) {
                mClipImageLayout.setImageBitmap(bitmap);
            } else {
                mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
            }
        } else {
//            String path = userOperationUtil.getFilePath(uri);
            // 判断图片是不是旋转了90度，是的话就进行纠正。
//            isBitmapRotate = userOperationUtil.isRotateImage(path);
            getBitmapSize(uri);
            getBitmap(uri);
            if (bitmap == null){
                finish();
            }else {
                if (degreee == 0) {
                    mClipImageLayout.setImageBitmap(bitmap);
                } else {
                    mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
                }
            }

        }


        findViewById(R.id.okBtn).setOnClickListener(this);
        findViewById(R.id.cancleBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.okBtn) {
            Bitmap bitmap;
            if (ClipImageLayout.isCcircle)
                bitmap = mClipImageLayout.clipCircle();
            else
                bitmap = mClipImageLayout.clip();

            String path = Environment.getExternalStorageDirectory()
                    + HeadPortraitImp.TMP_PATH;
            saveBitmap(bitmap, path);

//            Intent intent = new Intent();
//            intent.putExtra(RESULT_PATH, path);

            Intent intent = new Intent("inline-data");
            intent.putExtra(RESULT_PATH, path);
            setResult(RESULT_OK, intent);
            ClipImageLayout.isCcircle = false;
        }
        finish();
    }

    private void saveBitmap(Bitmap bitmap, String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fOut != null)
                    fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建图片
     *
     * @param path
     * @return
     */
    private Bitmap createBitmap(String path) {
        if (path == null) {
            return null;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(path);
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    // 读取图像的旋转度
    private int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    // 旋转图片
    private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return resizedBitmap;
    }




//    ????????????????????/????????????????????????????????????????????????????????????
    /**
     * 获取Bitmap分辨率，太大了就进行压缩
     *
     * @Title: getBitmapSize
     * @return void
     * @date 2012-12-14 上午8:32:13
     */
    private void getBitmapSize(Uri targetUri) {
        InputStream is = null;
        try {

            is =userOperationUtil.getInputStream(targetUri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);

            width = options.outWidth;
            height = options.outHeight;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 此处写方法描述
     *
     * @Title: getBitmap
     * @return void
     * @date 2012-12-13 下午8:22:23
     */
    private void getBitmap(Uri targetUri) {
        InputStream is = null;
        try {

            try {
                is = userOperationUtil.getInputStream(targetUri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while ((width / sampleSize > DEFAULT_WIDTH * 2)
                    || (height / sampleSize > DEFAULT_HEIGHT * 2)) {
                sampleSize *= 2;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;

            bitmap = BitmapFactory.decodeStream(is, null, options);

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }



}
