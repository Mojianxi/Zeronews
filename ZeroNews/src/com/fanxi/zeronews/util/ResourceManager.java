package com.fanxi.zeronews.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fanxi on 2016/6/4.
 */
public class ResourceManager {
    private static final String TAG="ResourceManager";
    private static ResourceManager resourceManager=new ResourceManager();
    private List<Bitmap> bitmapCaches;

    private ResourceManager(){
        bitmapCaches=new ArrayList<Bitmap>();
    }
    public static ResourceManager getInstance(){
        return resourceManager;
    }
    /**
     * 检查SD卡是否存在
     * @return 存在返回true，否则返回false
     */
    public boolean isSdcardReady(){
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        return sdCardExist;
    }
    /**
     * 获得SD路径
     * @return
     */
    public String getSdcardPath() {
        return Environment.getExternalStorageDirectory().toString()+ File.separator;
    }

    /**
     * 获取手机该应用的私有路径
     * @param context
     * @return
     */
    public String getRomCachePath(Context context){
        return context.getCacheDir() + File.separator;
    }

    /**
     * 检查SD卡中是否存在该文件
     * @param filePath 不包含SD卡目录的文件路径
     * @return
     */
    public boolean isSdcardFileExist(String filePath){
        File file=new File(getSdcardPath()+filePath);
        return file.exists();
    }

    /**
     *  检查手机内存中是否存在该文件
     * @param context
     * @param fileName 不包含应用内存目录的文件路径
     * @return
     */
    public boolean isRomCacheFileExist(Context context,String filePath){
        String cachePath = getRomCachePath(context);
        File file=new File(cachePath+filePath);
        return file.exists();
    }

    /**
     * 构建SD目录
     * @param path 不包含SD卡目录的文件全路径
     * @return
     */
    public String mkSdcardFileDirs(String path) {
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
    public String mkRomCacheDirs(Context context,String path) {
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
    public boolean saveBitmap(Bitmap bmp,String fullFilePath) {
        if(fullFilePath==null||fullFilePath.length()<1){
            Log.e(TAG, "saveBitmap error as filePath invalid");
            return false;
        }
        FileOutputStream fos = null;
        File file = new File(fullFilePath);
        if(file.exists())return false;
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "saveBitmap fail as "+e.getMessage());
        }
        return false;
    }

    /**
     * 保存图片到SD卡
     * @param bmp 图片文件
     * @param filePath
     */
    public boolean saveBitmap2Sdcard(Bitmap bmp,String filePath) {
        if(!isSdcardReady()){
            Log.e(TAG, "saveBitmap2Sdcard error as sdCard not exist");
            return false;
        }
        Log.e(TAG, "保存成功");
        return saveBitmap(bmp, getSdcardPath()+filePath);
    }

    /**
     * 保存图片到手机内存
     * @param context
     * @param bmp 图片文件
     * @param filePath
     * @return
     */
    public boolean saveBitmap2RomCache(Context context,Bitmap bmp,String filePath){
        return saveBitmap(bmp, getRomCachePath(context)+filePath);
    }

    /**
     * 获取图片资源
     * @param fullFilePath
     * @return
     */
    public Bitmap getBitmap(String fullFilePath){
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
     * @param fullFilePath
     * @return
     */
    public Bitmap getBitmapFromSdcard(String filePath){
        return getBitmap(getSdcardPath()+filePath);
    }

    /**
     * 从手机内存获取图片资源
     * @param fullFilePath
     * @return
     */
    public Bitmap getBitmapFromRomCache(Context context,String filePath){
        return getBitmap(getRomCachePath(context)+filePath);
    }

    /**
     * 获得包路径下的图片文件
     * @param pkgPath
     * @param fileName
     * @return
     */
    public Bitmap getBitmapFromPackage(Class<?> clazz,String pkgPath,String fileName) {
        InputStream is = clazz.getResourceAsStream(pkgPath+fileName);
        try {
            if (null == is || is.available() <= 0)return null;
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
            try {
                if (is != null)is.close();
            }catch (Exception e) {}
            return null;
        }
        try {
            Bitmap bitmap=BitmapFactory.decodeStream(is);
            bitmapCaches.add(bitmap);
            return bitmap;
        }catch (Exception e) {
            Log.e(TAG, "getBitmapFromPackage fail as "+e.getMessage());
        }finally {
            try {
                if (is != null)is.close();
            }catch (Exception e) {}
        }
        return null;
    }

    /**
     *  获得包路径下的.9.png格式图片
     * @param clazz
     * @param pkgPath
     * @param picName
     * @return
     */
    public Drawable get9pngDrawableFromPackage(
            Class<?> clazz,String pkgPath, String picName) {
        InputStream inputStream = clazz.getResourceAsStream(pkgPath + picName);
        try {
            if (null == inputStream || inputStream.available() <= 0)
                return null;
        }
        catch (IOException e1) {
            try {
                if (inputStream != null)
                    inputStream.close();
            }
            catch (Exception e) {}
            return null;
        }
        Bitmap bitmap = null;
        NinePatchDrawable patchy = null;
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmapCaches.add(bitmap);
            byte[] chunk = bitmap.getNinePatchChunk();
            boolean result = NinePatch.isNinePatchChunk(chunk);
            if (result) patchy = new NinePatchDrawable(bitmap, chunk, new Rect(), null);
        }
        catch (Exception e) {}
        finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            }
            catch (Exception e) {}
        }
        return patchy;
    }

    /**
     * 释放引用的图片
     */
    public void recycleBitmaps(){
        for (int i = 0; i < bitmapCaches.size(); i++) {
            bitmapCaches.get(i).recycle();
        }
        bitmapCaches.clear();
    }

    /**
     * 从文件中读取字符串
     * @param file
     * @return
     */
    public String readStringFile(File file){
        try {
            InputStream is = new FileInputStream(file);
            return readString(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从文件中读取字符串
     * @param file
     * @return
     */
    public String readStringFromPackage(Class<?> clazz,String pkgPath,String fileName){
        try {
            InputStream is=clazz.getResourceAsStream(pkgPath+fileName);
            return readString(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readString(InputStream is){
        ByteArrayOutputStream bos = null;
        try{
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while( (length = is.read(buffer)) != -1){
                bos.write(buffer,0,length);
            }
            return bos.toString();
        } catch(Exception e){
            Log.e(TAG, "readStringFromPackage fail as "+e.getMessage());
        }finally{
            try {bos.close();
            } catch (IOException e) {}
            try {is.close();
            } catch (IOException e) {}
        }
        return null;
    }

    /**
     * 从SD卡读取字符文件
     * @param filePath
     * @return
     */
    public String readStringFileFromSdcard(String filePath) {
        if(!isSdcardReady()){
            Log.e(TAG, "saveBitmap2Sdcard error as sdCard not exist");
            return null;
        }
        File file=new File(getSdcardPath()+filePath);
        return readStringFile(file);
    }

    /**
     * 从手机内存读取字符文件
     * @param filePath
     * @return
     */
    public String readStringFileFromRomCache(Context context,String filePath) {
        File file=new File(getRomCachePath(context)+filePath);
        return readStringFile(file);
    }

    /**
     * 存储字符文件
     * @param fullFilePath 完整路径
     * @param content 字符内容
     * @return
     */
    public boolean saveStringFile(String fullFilePath,String content){
        File file = new File(fullFilePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "saveData fail as "+e.getMessage());
        }finally{
            try {
                if(fos!=null) fos.close();
            } catch (IOException e) {}
        }
        return false;
    }

    /**
     *  存储字符文件到手机内存
     * @param context
     * @param fileName
     * @param content
     * @return
     */
    public boolean saveStringFile2RomCache(Context context, String filePath, String content){
        String cachePath = getRomCachePath(context);
        return saveStringFile(cachePath+filePath, content);
    }

    /**
     * 存储字符文件到SD卡
     * @param filePath
     * @param content
     * @return
     */
    public boolean saveStringFile2Sdcard(String filePath,String content) {
        if(!isSdcardReady()){
            Log.e(TAG, "saveStringFile2Sdcard error as sdCard not exist");
            return false;
        }
        return saveStringFile(getSdcardPath()+filePath,content);
    }
}
