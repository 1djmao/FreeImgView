package com.idjmao.freeimgview;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class FileUtils {

    /**
     * 创建文件
     * @param file
     */
    public static boolean createFile(File file){
        if (file.exists()){
            file.delete();
        }
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            Log.e("FileUtils", "createFile: ", e);
            return false;
        }
    }

    /**
     * 把字符串写入文件
     * @param s
     * @param file
     * @return
     */
    public static boolean writeString2File(String s,File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(s.getBytes());
            raf.close();
            return true;
        } catch (Exception e) {
            Log.e("FileUtils", "Error on write File:" + e);
            return false;
        }
    }

    /**
     * 读取文本文件成字符串
     * @param file
     * @return
     */
    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader
                            = new InputStreamReader(instream, "UTF-8");
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line = "";
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();//关闭输入流
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }

        }
        return content;
    }

    public static boolean copyFile(File fFile,File tFile){
        if (fFile==null||!fFile.exists()||!fFile.isFile()){
            return false;
        }
        createFile(tFile);

        try {
            FileInputStream fileInputStream = new FileInputStream(fFile);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(tFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 将文件从assets目录，考贝到 /data/data/包名/files/ 目录中。assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
     * @param fileName 文件名,如aaa.txt
     */
    public static void copyAssetsFile2Phone(Context context, String fileName, File file){
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            if(!file.exists() || file.length()==0) {
                FileOutputStream fos =new FileOutputStream(file);//如果文件不存在，FileOutputStream会自动创建文件
                int len=-1;
                byte[] buffer = new byte[1024];
                while ((len=inputStream.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                }
                fos.flush();//刷新缓存区
                inputStream.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "copyAssetsFile2Phone: ", e);
        }
    }

    /**
     * 删除文件夹和文件夹里面的文件
     */
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

}
