package com.dht.editimage.editimage.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by panyi on 16/10/23.
 */
public class FileUtil {

    public FileUtil(String path) {
        try {
            //指定路径对象，如果这个对象不存在的话，就创建此路径下的所有文件夹
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkFileExist(final String path) {
        if (TextUtils.isEmpty(path))
            return false;

        File file = new File(path);
        return file.exists();
    }

    //用户信息文件名字
    private static final String CUSTOM_FILE_NAME = "userInfo";
    //用户头像
    public static final String USER_IMAGE_PATH = "image";

    // 将用户信息写入缓存
    public static void writeUserCache(Object obj, String myfile) {
       /*
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOGO_CACHE_PATH + myfile));
            oos.writeObject(obj);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    // 读取缓存信息
//    public static Userinfo readCache(String str) {
//        Userinfo custom = new Userinfo();
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LOGO_CACHE_PATH + CUSTOM_FILE_NAME + str + ".confg"));
//            custom = (Userinfo) ois.readObject();
//            ois.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return custom;
//    }

    // 删除缓存中用户信息
//    public static void delUserinfo(String str) {
//        File f = new File(LOGO_CACHE_PATH + CUSTOM_FILE_NAME + str + ".confg");
//        if (f.exists()) {
//            f.delete();
//        }
//    }

    /************************************ 用户信息操作缓存完毕 ***************************************************/


    /***************************************
     * 图片缓存操作
     **********************************************************/
    public static String WriteImageFile(byte[] bytes, String filePath, String fileName) {
        try {
            File destDir = new File(filePath);
            if (!destDir.exists()) {
                // 创建目的目录
                System.out.println("目的目录不存在，准备创建。。。");
                if (!destDir.mkdirs()) {
                    System.out.println("复制目录失败：创建目的目录失败！");
                    return null;
                }
            }
            destDir = new File(destDir, fileName);
            if (!destDir.exists()) {
                try {
                    destDir.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream outputStream = new FileOutputStream(destDir); //准备输出流
            outputStream.write(bytes);//开始写入sdk文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath + fileName;
    }


    /**
     * 计算目录大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        // 不是目录
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;

        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                // 递归调用
                dirSize += getDirSize(file);
            }

        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {

        if (fileS == 0) {
            return "0.00B";
        }

        DecimalFormat dFormat = new DecimalFormat("#.00");

        String fileSizeString = "";

        if (fileS < 1024) {
            fileSizeString = dFormat.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = dFormat.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = dFormat.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = dFormat.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 文件目录地址
     *
     * @return
     */
    public static String fileDirectory(String dirPath, String fileName) {
        String filePath = "";

        String storageState = Environment.getExternalStorageState();

        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + dirPath;

            File file = new File(filePath);

            if (!file.exists()) {
                // 建立一个新的目录
                file.mkdirs();
            }
            filePath = filePath + fileName;
        }

        return filePath;
    }

    /**
     * 获取文件目录
     *
     * @return
     */
    public static File getDirectoryFile(String dirPath) {

        String storageState = Environment.getExternalStorageState();

        File file = null;

        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + dirPath;

            file = new File(filePath);

            if (!file.exists()) {
                // 建立一个新的目录
                file.mkdirs();
            }
        }

        return file;
    }

    /**
     * 检查文件后缀
     *
     * @param checkItsEnd
     * @param fileEndings
     * @return
     */
    private static boolean checkEndsWithInStringArray(String checkItsEnd,
                                                      String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    /**
     * 根据不同的后缀imageView设置不同的值
     *
     * @param fileName
     */
//    public static void setImage(Context context, String fileName, ImageView imageView) {
//
//        if (checkEndsWithInStringArray(fileName, context.getResources()
//                .getStringArray(R.array.fileEndingText))) {
//            imageView.setImageResource(R.drawable.file_icon_txt);
//        } else {
//            imageView.setImageResource(R.drawable.file);
//            imageView.setVisibility(View.GONE);
//        }
//    }

    /**
     * 返回本地文件列表
     * <p/>
     * 本地文件夹路径
     */
    public static List<File> getFileListByPath(String path) {

        File dir = new File(path);

        List<File> folderList = new ArrayList<File>();

        List<File> fileList = new ArrayList<File>();

        // 获取指定盘符下的所有文件列表。（listFiles可以获得指定路径下的所有文件，以数组方式返回）
        File[] files = dir.listFiles();
        // 如果该目录下面为空，则该目录的此方法执行
        if (files == null) {
            return folderList;
        }// 通过循环将所遍历所有文件
        for (int i = 0; i < files.length; i++) {

            if (!files[i].isHidden()) {

                if (files[i].isDirectory()) {
                    folderList.add(files[i]);
                }
                if (files[i].isFile()) {
                    if (files[i].getName().endsWith(".txt")) {
                        fileList.add(files[i]);
                    }
                }
            }
        }
        folderList.addAll(fileList);

        return folderList;
    }

    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in;
            OutputStream out;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dest);

                byte[] buffer = new byte[1024];

                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * @param src  源文件路径
     * @param name 源文件名字
     * @param dest 目标目录
     */
    public static void copyFile(File src, String name, File dest) {
        File file = new File(dest, name);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(src);
            outputStream = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }

            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 创建目录或文件
     */
    public static void createDirorFile(String path, String name, Context context, int check) {
        File file = new File(path + File.separator + name);

        if (check == 0) {// 如果为文件
            try {
                file.createNewFile();
                Toast.makeText(context, "创建文件成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(context, "创建文件失败", Toast.LENGTH_SHORT).show();
            }
        } else if (check == 1) {
            // 创建目录
            if (file.mkdirs()) {
                Toast.makeText(context, "创建目录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "创建目录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 删除一个目录
     */
    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(file); // 递规的方式删除文件夹
        }
        dir.delete();
    }

    /**
     * @param fromDir  这个为源目录
     * @param fromPath 这个为源目录的上一级路径
     * @param toName   要修改的名字
     */
    public static boolean renameFile(File fromDir, String fromPath,
                                     String toName) {
        File tempFile = new File(fromPath + File.separator + toName);
        if (tempFile.exists()) {
            return false;
        } else {
            return fromDir.renameTo(tempFile);
        }
    }

}//end class
