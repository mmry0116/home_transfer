package com.mmry.until;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.zip.*;

/**
 * 压缩类
 */
public class ZipCompressor {

    static final int BUFFER = 8192;

    /**
     * 压缩的文件夹
     */
    private File zipFile;
    private static boolean cycle = false;

    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
    }


    /**
     * 遍历需要压缩文件集合
     *
     * @param pathName
     * @throws IOException
     */
    public void compress(String... pathName) throws IOException {
        ZipOutputStream out = null;
        FileOutputStream fileOutputStream = null;
        CheckedOutputStream cos = null;
        try {
            fileOutputStream = new FileOutputStream(zipFile);
            cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";
            for (int i = 0; i < pathName.length; i++) {
                compress(new File(pathName[i]), out, basedir);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (cos != null) {
                cos.close();
            }
        }
    }

    /**
     * 压缩
     *
     * @param file
     * @param out
     * @param basedir
     */
    private void compress(File file, ZipOutputStream out, String basedir) throws IOException {
        // 判断是目录还是文件
        if (file.isDirectory()) {
            this.compressDirectory(file, out, basedir);
        } else {
            this.compressFile(file, out, basedir);
        }
    }

    /**
     * 压缩一个目录
     */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) throws IOException {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 递归
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /**
     * 压缩一个文件
     */
    private void compressFile(File file, ZipOutputStream out, String basedir) throws IOException {
        if (!file.exists()) {
            return;
        }
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }
}
