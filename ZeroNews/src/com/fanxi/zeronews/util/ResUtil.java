package com.fanxi.zeronews.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fanxi on 2016/6/4.
 */
public class ResUtil {
    public static final String SD_IMG_PATH="istore/images/";
    public static final String ROM_IMG_PATH="images/";
    public static final String SD_DATA_PATH="istore/data/";
    public static final String ROM_DATA_PATH="data/";

    public static final String PKG_PATH = "/com/linxcool/istore/";
    public static final String PKG_RES_PATH = PKG_PATH+"res/";

    /**
     * 获得包图片资源
     * @param picName
     * @return
     */
    public static Bitmap getBitmapFromPackage(String picName){
        return ResourceManager.getInstance().getBitmapFromPackage(
                ResUtil.class,PKG_RES_PATH, picName);
    }

    /**
     * 获得包.9.png图片资源
     * @param picName
     * @return
     */
    public static Drawable get9pngDrawableFromPackage(String picName){
        return ResourceManager.getInstance().get9pngDrawableFromPackage(
                ResUtil.class, PKG_RES_PATH, picName);
    }

    /**
     * 获得SD_APK_PATH路径下的APK文件
     * @return
     */
    public static List<File> getApkFiles(){
        File filesPath=new File(
                ResourceManager.getInstance().getSdcardPath()+"SDK apk path");
        if(!filesPath.exists())return null;
        File[] files=filesPath.listFiles();
        if(files==null || files.length==0)return null;

        List<File> list=new ArrayList<File>();
        for (File file : files) {
            if(file.isDirectory())continue;
            String fileName=file.getName().toLowerCase();
            if(fileName.contains(".apk"))
                list.add(file);
        }
        return list;
    }

    public static String getFolderFromPath(String path) {
        String[] slipArray = path.split(File.separator);
        if( slipArray.length <= 0 )
            return null;
        int pathLen = path.length();
        int fileNameLen = slipArray[slipArray.length-1].length();
        return path.substring(0, pathLen-fileNameLen);
    }

    public static String readStringFromPackage(String fileName){
        return ResourceManager.getInstance().readStringFromPackage(
                ResUtil.class,PKG_RES_PATH, fileName);
    }

    /**
     * 保存String数据
     * @param context
     * @param fileName
     * @param content
     */
    public static void saveStringData(Context context,String fileName,String content){
        if(ResourceManager.getInstance().isSdcardReady()){
            String filePath=SD_DATA_PATH+fileName;
            String folder=getFolderFromPath(filePath);
            ResourceManager.getInstance().mkSdcardFileDirs(folder);
            ResourceManager.getInstance().
                    saveStringFile2Sdcard(filePath, content);
        }else{
            String filePath=ROM_DATA_PATH+fileName;
            String folder=getFolderFromPath(filePath);
            ResourceManager.getInstance().mkRomCacheDirs(context, folder);
            ResourceManager.getInstance().
                    saveStringFile2RomCache(context, filePath, content);
        }
    }

    /**
     * 读取String数据
     * @param context
     * @param fileName
     * @return
     */
    public static String readStringData(Context context,String fileName){
        if(ResourceManager.getInstance().isSdcardFileExist(SD_DATA_PATH+fileName)){
            return ResourceManager.getInstance().
                    readStringFileFromSdcard(SD_DATA_PATH+fileName);
        }else if(ResourceManager.getInstance().isRomCacheFileExist(context, ROM_DATA_PATH+fileName)){
            return ResourceManager.getInstance().
                    readStringFileFromRomCache(context,ROM_DATA_PATH+fileName);
        }
        return null;
    }

    /**
     * 保存图片文件
     * @param context
     * @param bmp
     * @param fileName
     */
    public static void saveBitmap(Context context,Bitmap bmp, String fileName){
        if(ResourceManager.getInstance().isSdcardReady()){
            String filePath=SD_IMG_PATH+fileName;
            String folder=getFolderFromPath(filePath);
            ResourceManager.getInstance().mkSdcardFileDirs(folder);
            ResourceManager.getInstance().saveBitmap2Sdcard(bmp,filePath);
        }else{
            String filePath=ROM_IMG_PATH+fileName;
            String folder=getFolderFromPath(filePath);
            ResourceManager.getInstance().mkRomCacheDirs(context, folder);
            ResourceManager.getInstance().
                    saveBitmap2RomCache(context, bmp, filePath);
        }
    }

    /**
     * 获得图片文件
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(Context context, String fileName){
        if(ResourceManager.getInstance().isSdcardFileExist(SD_IMG_PATH+fileName)){
            return ResourceManager.getInstance().
                    getBitmapFromSdcard(SD_IMG_PATH+fileName);
        }else if(ResourceManager.getInstance().isRomCacheFileExist(context, ROM_IMG_PATH+fileName)){
            return ResourceManager.getInstance().
                    getBitmapFromRomCache(context,ROM_IMG_PATH+fileName);
        }
        return null;
    }
}
