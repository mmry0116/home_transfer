package com.mmry.until;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author : 明明如月
 * @date : 2024/4/9 9:46
 */
public class FileUtil {

    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    //??? 有问题
    public static Long calculateSize(File file,long size){
        if (file.isFile()) {
            return  file.length() + size;
        }
        if (file.isDirectory()){
            Long temp = 0L;
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile())
                    temp += f.length();
                temp =  temp + calculateSize( file ,0);
            }

            return size + temp;
        }
        return 0L;
    }

}
