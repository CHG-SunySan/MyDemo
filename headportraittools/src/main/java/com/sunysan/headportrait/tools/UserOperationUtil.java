package com.sunysan.headportrait.tools;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 用户操作
 * Created by SunySan on 2016/12/3.
 */
public class UserOperationUtil {
    private Context context;

    public UserOperationUtil(Context mContext){
        context = mContext;
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
    private String getFilePathByUri(Uri uri) throws FileNotFoundException {
//        Cursor cursor = context.getContentResolver()
//                .query(mUri, null, null, null, null);
//        cursor.moveToFirst();
//        return cursor.getString(1);

        String imgPath = null;
        if (uri != null) {
            String uriString = uri.toString();
            // 小米手机的适配问题，小米手机的uri以file开头，其他的手机都以content开头
            // 以content开头的uri表明图片插入数据库中了，而以file开头表示没有插入数据库
            // 所以就不能通过query来查询，否则获取的cursor会为null。
            if (uriString.startsWith("file")) {
                // uri的格式为file:///mnt....,将前七个过滤掉获取路径
                imgPath = uriString.substring(7, uriString.length());

                return imgPath;
            }
            if (uriString.startsWith("content://media/external/images")) {
                Cursor cursor = context.getContentResolver().query(uri, null,
                        null, null, null);
                cursor.moveToFirst();
                imgPath = cursor.getString(1); // 图片文件路径
                Log.e("imgPath", imgPath);
            } else {
                imgPath = getAbsoluteImagePath(uri);
                Log.e("imgPath", imgPath);
            }
        }
        return imgPath;
    }

    /**
     * 此方法是根据uri返回正确的路径
     * */
    @SuppressWarnings("deprecation")
    private String getAbsoluteImagePath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = ((Activity) context).managedQuery(uri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    /**
     * 加载用户本地头像 绝对路径。/mnt/yy/png/203.png 这种格式
     *
     * @return
     */
    public Bitmap getImgSource(Context c, String pathString) {
        Bitmap bitmap = null;
        if (getSDCardStatus()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 1;
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString, opts);
                if (bitmap == null) {
                    return null;
                } else {
                    return bitmap;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 判断SD卡是否可用
     */
    public boolean getSDCardStatus() {
        boolean SDStatus = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (SDStatus) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取输入流
     *
     * @Title: getInputStream
     * @param mUri
     * @return
     * @return InputStream
     */
    public InputStream getInputStream(Uri mUri) throws IOException {
        try {
            if (mUri.getScheme().equals("file")) {
                return new java.io.FileInputStream(mUri.getPath());
            } else {
                return context.getContentResolver().openInputStream(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

}
