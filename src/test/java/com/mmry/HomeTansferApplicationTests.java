package com.mmry;

import ch.qos.logback.core.util.FileUtil;
import com.mmry.controller.DownloadController;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

//@RunWith(SpringRunner.class)
//@SpringBootTest
class HomeTansferApplicationTests {

    @Test
    public void test11() throws NoSuchAlgorithmException {
//        7CEA62E3DD91E9FA8B86BC1B8AD38744
        String s = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.txt";
        String str = "hello world";
        int hash = str.hashCode();
        System.out.println("字符串的hash值为：" + hash);
//        System.out.println(md5(new File("G:\\tempZipPack\\m_1713757139.zip")));
//        System.out.println(md5(new File("G:\\tempZipPack\\m_1713756917.zip")));
        System.out.println(s.length());
        String name = new File("G:\\tempZipPack\\m_1713757139.zip").getName();
        System.out.println(name.substring(0,name.length()-4));
    }

    public String md5(File file){
        DigestInputStream din = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //第一个参数是一个输入流
            din = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), md5);

            byte[] b = new byte[1024];
            while (din.read(b) != -1);

            byte[] digest = md5.digest();

            StringBuilder result = new StringBuilder(file.getName());
            result.append(": ");
            result.append(DatatypeConverter.printHexBinary(digest));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (din != null) {
                    din.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Test
    public void avVoid() {
        File file = new File("G:/mm/WW私人版4.7");
//        File file = new File("G:\\Download\\kuake\\Grim Dawn");
        System.out.println(FileUtils.sizeOfDirectory(file));
//        System.out.println("calculateSize(file,0) = " + calculateSize(file));

    }

    @Test
    public void aVoid() {
        String str = "G:\\ff\\orange.webp";
        str = str.replaceAll("\\\\", "\\/");
        System.out.println(str);
        int ss = 111222;
        System.out.println((ss + "").length());

    }

    //    @Autowired
    DownloadController downloadController;

    @Test
    public void test1() throws IOException {
        File file = new File("G:\\Download\\0100110等2个文件\\GB\\小公主 走.ani");
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        System.out.println(attrs.size());
        System.out.println(attrs.creationTime().toInstant());
        Instant instant = attrs.creationTime().toInstant();
        System.out.println("instant = " + instant);
        System.out.println(attrs.lastModifiedTime());
        System.out.println(attrs.lastAccessTime());
        System.out.println("file.getPath() = " + file.getPath());
        System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
        System.out.println("file.getName() = " + file.getName());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        System.out.println("localDateTime = " + localDateTime);
//        downloadController.updatePath(null,);
        String s = "aabbcc";

        int last = s.lastIndexOf("b");
        System.out.println("s.substring(0,last+1) = " + s.substring(0, last + 1));
        String path1 = file.getAbsolutePath().replaceAll("\\\\", "/");
        int last1 = path1.lastIndexOf("/");
        System.out.println("path1.substring(0,last1+1) = " + path1.substring(0, last1 + 1));
        System.out.println("file.length() = " + file.length());

    }

    @Test
    public void deleteFolder(){
//        System.out.println(new File("G:/xx/新建文件夹").delete())
        long l = Long.parseLong("577591812581709746");
        System.out.println(Long.toBinaryString(l));
        String str = "1000100000000100000001001011000101111110000110110101011110110010";
        System.out.println(str.length());
    }


}
