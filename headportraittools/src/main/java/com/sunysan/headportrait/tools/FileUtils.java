package com.sunysan.headportrait.tools;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 保存图片路径
 * Created by SunySan on 2016/7/15.
 */
public class FileUtils {
	public static File f;

	private String imgName = "clip_temp.jpg";
	public static String SDPATH = Environment.getExternalStorageDirectory()
			.getPath() + "/com.sunysan/";

	public static void saveBitmap(Bitmap bm, String picName) {
//		AlLog.E("SunySan", "保存图片");
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			 f = new File(SDPATH, picName + ".JPEG");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
//			AlLog.E("SunySan", "已经保存");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件路径
	 * @return
	 */
	public static String getFilePath(){
		return f.getPath();
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH );
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {//判断SD卡是否存在并且是否有可读写权限
//			if (!dir.exists()){
//				dir.mkdirs();
//			}
//			AlLog.E("SunySan:  createSDDir:" + dir.getAbsolutePath());
//			AlLog.E("SunySan:  createSDDir:" + dir.mkdirs());
		}
//		File file = new File(dir,dirName);
//		if (!file.exists()) {//保证有目录没有file这个文件再创建file这个文件
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		//以上注释的是可以实现的，但需要在你不需要操作的时候去删除保存在SDK上的图片
		// （由于暂时不想去删除存储的图片，所以没有提供方法，故这里注释掉）

		return dir;
	}

	public static File createFile(String dirName) throws IOException {
		File dir = new File(SDPATH );
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {//判断SD卡是否存在并且是否有可读写权限
			if (!dir.exists()){
				dir.mkdirs();
			}
		}
		File file = new File(dir,dirName);
		if (!file.exists()) {//保证有目录没有file这个文件再创建file这个文件
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dir;
	}



	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

}
