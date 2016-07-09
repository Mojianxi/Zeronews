package com.fanxi.zeronews.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import com.fanxi.zeronews.application.BaseApplication;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class UiUtils {

	private static final String TAG ="UiUtils" ;
	private static List<Bitmap> bitmapCaches;
	public static String html2Text(String inputString) {
		  String htmlStr = inputString; // 含html标签的字符串
		  String textStr = "";
		  java.util.regex.Pattern p_script;
		  java.util.regex.Matcher m_script;
		  java.util.regex.Pattern p_style;
		  java.util.regex.Matcher m_style;
		  java.util.regex.Pattern p_html;
		  java.util.regex.Matcher m_html;
		  try {
		   String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script>]*?>[\s\S]*?<\/script>
		   // }
		   String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style>]*?>[\s\S]*?<\/style>
		   // }
		   String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		   p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		   m_script = p_script.matcher(htmlStr);
		   htmlStr = m_script.replaceAll(""); // 过滤script标签

		   p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		   m_style = p_style.matcher(htmlStr);
		   htmlStr = m_style.replaceAll(""); // 过滤style标签

		   p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		   m_html = p_html.matcher(htmlStr);
		   htmlStr = m_html.replaceAll(""); // 过滤html标签

		   textStr = htmlStr.trim();

		  } catch (Exception e) {
		   System.err.println("Html2Text: " + e.getMessage());
		  }
		  return textStr;
		}
	/**
	 * 读取assest文件
	 *
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String readAssest(Context context, String fileName) {
		String result = "";
		InputStream input = null;
		try {
			input = context.getAssets().open(fileName);
			int length = input.available();
			byte[] buffer = new byte[length];
			input.read(buffer);

			result = EncodingUtils.getString(buffer, "UTF-8");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	/**
	 * 返回圆形
	 * @param bitmap
	 * @return
     */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), 
		bitmap.getHeight(), Config.ARGB_8888); 
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint(); 
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
		final RectF rectF = new RectF(rect); 
		final float roundPx = bitmap.getWidth() / 2; 
		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bitmap, rect, rect, paint); 
		return output; 
		} 

	public static Context getContext(){
		return BaseApplication.getContext();
	}
	public static Resources getResources(){
		return getContext().getResources();
	}
	public static int dip2px(int dp) {
		//  dp和px的转换关系
		float density = getResources().getDisplayMetrics().density;
		// 2*1.5+0.5 2*0.75 = 1.5+0.5
		return (int) (dp * density + 0.5);
	}

	// px To dip
	public static int px2dip(int px) {
		float density = getResources().getDisplayMetrics().density;
		return (int) (px / density + 0.5);
	}

	public static ColorStateList getColorStateList(int mTabTextColorResId) {
		return getResources().getColorStateList(mTabTextColorResId);
	}

	public static Drawable getDrawable(int mTabBackgroundResId) {
		return getResources().getDrawable(mTabBackgroundResId);
	}
	/**
	 * 检查SD卡是否存在
	 * @return 存在返回true，否则返回false
	 */
	public static boolean isSdcardReady(){
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}
	/**
	 * 获得SD路径
	 * @return
	 */
	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().toString()+ File.separator;
	}
	/**
	 * 获取手机该应用的私有路径
	 * @param context
	 * @return
	 */
	public static String getRomCachePath(Context context){
		return context.getCacheDir() + File.separator;
	}
	/**
	 * 检查SD卡中是否存在该文件
	 * @param filePath 不包含SD卡目录的文件路径
	 * @return
	 */
	public static boolean isSdcardFileExist(String filePath){
		File file=new File(getSdcardPath()+filePath);
		return file.exists();
	}
	/**
	 *  检查手机内存中是否存在该文件
	 * @param context
	 * @param //fileName 不包含应用内存目录的文件路径
	 * @return
	 */
	public static boolean isRomCacheFileExist(Context context,String filePath){
		String cachePath = getRomCachePath(context);
		File file=new File(cachePath+filePath);
		return file.exists();
	} /**
	 * 构建SD目录
	 * @param path 不包含SD卡目录的文件全路径
	 * @return
	 */
	public static String mkSdcardFileDirs(String path) {
		String rsPath =getSdcardPath() + path;
		File file = new File(rsPath);
		if (!file.exists())file.mkdirs();
		return rsPath;
	}
	/**
	 * 构建手机存储文件路径
	 * @param context
	 * @param path 不包含应用内存目录的文件全路径
	 * @return
	 */
	public static String mkRomCacheDirs(Context context,String path) {
		String cachePath = getRomCachePath(context);
		String rsPath=cachePath+path;
		File file = new File(rsPath);
		if (!file.exists())file.mkdirs();
		return rsPath;
	}
	/**
	 * 保存图片文件
	 * @param bmp
	 * @param fullFilePath 保存的完整路径
	 * @return 返回true如果图片保存失败或图片已存在，否则返回false
	 */
	public static boolean saveBitmap(Bitmap bmp,String fullFilePath) {
		if(fullFilePath==null||fullFilePath.length()<1){
			Log.e(TAG, "saveBitmap error as filePath invalid");
			return false;
		}
		FileOutputStream fos = null;
		File file = new File(fullFilePath);
//		if(!file.exists()){
//			file.createNewFile();
//		}
		try {
			fos = new FileOutputStream(file);
			if (null != fos) {
				bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				Log.e(TAG, "saveBitmap fail as 成功");
				return true;
			}
		} catch (IOException e) {
			Log.e(TAG, "saveBitmap fail as "+e.getMessage());
		}
		Log.e(TAG, "saveBitmap fail as 失败");
		return false;
	}
	/**
	 * 保存图片到SD卡
	 * @param bmp 图片文件
	 * @param filePath
	 */
	public static boolean saveBitmap2Sdcard(Bitmap bmp,String filePath) {
		if(!isSdcardReady()){
			Log.e(TAG, "saveBitmap2Sdcard error as sdCard not exist");
			return false;
		}
		return saveBitmap(bmp, getSdcardPath()+filePath);
	}
	/**
	 * 获取图片资源
	 * @param fullFilePath
	 * @return
	 */
	public static Bitmap getBitmap(String fullFilePath){
		try {
			File file=new File(fullFilePath);
			if(file.exists()){
				Bitmap bitmap = BitmapFactory.decodeFile(fullFilePath);
				bitmapCaches.add(bitmap);
				return bitmap;
			}
		} catch (Exception e) {
			Log.e(TAG, "getBitmap fail as "+e.getMessage());
		}
		return null;
	}
	/**
	 * 从SD卡获取图片资源
	 * @param //fullFilePath
	 * @return
	 */
	public static Bitmap getBitmapFromSdcard(String filePath){
		return getBitmap(getSdcardPath()+filePath);
	}
	/**
	 * 释放引用的图片
	 */
	public static void recycleBitmaps(){
		for (int i = 0; i < bitmapCaches.size(); i++) {
			bitmapCaches.get(i).recycle();
		}
		bitmapCaches.clear();
	}

	/**
	 * 判断当前网络是否可用
	 * @param context
	 * @return
     */
	public static boolean isNetworkAvailable(Context context)
	{
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null)
		{
			return false;
		}
		else
		{
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0)
			{
				for (int i = 0; i < networkInfo.length; i++)
				{
					System.out.println(i + "===状态===" + networkInfo[i].getState());
					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	   /**
     * 把bitmap转成圆形
     * */
    public static Bitmap toRoundBitmap(Bitmap bitmap){
            int width=bitmap.getWidth();
            int height=bitmap.getHeight();
            int r=0;
            //取最短边做边长
            if(width<height){
                    r=width;
            }else{
                    r=height;
            }
            //构建一个bitmap
            Bitmap backgroundBm=Bitmap.createBitmap(width,height,Config.ARGB_8888);
            //new一个Canvas，在backgroundBmp上画图 
            Canvas canvas=new Canvas(backgroundBm);
            Paint p=new Paint();
            //设置边缘光滑，去掉锯齿 
            p.setAntiAlias(true);
            RectF rect=new RectF(0, 0, r, r);
            //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，  
            //且都等于r/2时，画出来的圆角矩形就是圆形 
            canvas.drawRoundRect(rect, r/2, r/2, p);
            //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
            p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            //canvas将bitmap画在backgroundBmp上
            canvas.drawBitmap(bitmap, null, rect, p);
            return backgroundBm;
    }
    /**
	 * 把一个流里面的内容 转化成一个字符串
	 * @param is 流里面的内容
	 * @return null解析失败
	 */
	public static String readStream(InputStream is){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = is.read(buffer))!=-1){
				baos.write(buffer, 0, len);
			}
			is.close();
			return new String(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	 /**      * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理      *       * @param directory      */  
	 private static void deleteFilesByDirectory(File directory) {      
	 if (directory != null && directory.exists() && directory.isDirectory()) {       
	 for (File item : directory.listFiles()) {             
	 item.delete();             
	 }         
	 }     
	 }  
	 /**      * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)      *       * @param context      */   
	 public static void cleanInternalCache(Context context) {      
	 deleteFilesByDirectory(context.getCacheDir());     
	 }   
	 int QR_HEIGHT=UiUtils.px2dip(200);
	 int QR_WIDTH=UiUtils.px2dip(200);
	 public void createQRImage(String url,ImageView sweepIV)
	    {
	        try
	        {
	            //判断URL合法性
	            if (url == null || "".equals(url) || url.length() < 1)
	            {
	                return;
	            }
	            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
	            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
	            //图像数据转换，使用了矩阵转换
	            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, UiUtils.px2dip(200), UiUtils.px2dip(200), hints);
	            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
	            //下面这里按照二维码的算法，逐个生成二维码的图片，
	            //两个for循环是图片横列扫描的结果
	            for (int y = 0; y < QR_HEIGHT; y++)
	            {
	                for (int x = 0; x < QR_WIDTH; x++)
	                {
	                    if (bitMatrix.get(x, y))
	                    {
	                        pixels[y * QR_WIDTH + x] = 0xff000000;
	                    }
	                    else
	                    {
	                        pixels[y * QR_WIDTH + x] = 0xffffffff;
	                    }
	                }
	            }
	            //生成二维码图片的格式，使用ARGB_8888
	            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
	            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
	            //显示到一个ImageView上面
	            sweepIV.setImageBitmap(bitmap);
	        }
	        catch (WriterException e)
	        {
	            e.printStackTrace();
	        }
	    }
}
