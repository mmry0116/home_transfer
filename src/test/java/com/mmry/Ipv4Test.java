package com.mmry;

import org.junit.Test;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

public class Ipv4Test {
    public HashMap<String, String> toPathMap(File drive) {
        if (!drive.isDirectory()) throw new RuntimeException("不是一个文件夹");
        HashMap<String, String> pathMap = new HashMap<>();
        Arrays.stream(drive.listFiles()).forEach(file -> {
            String key;
            if (file.isDirectory()) {
                if (file.isHidden()) {
                    key = "hide_directory";
                } else
                    key = "directory";
            } else {
                if (file.isHidden()) {
                    key = "hide_file";
                } else
                    key = "file";
            }
            String path = file.toString().replaceAll("\\\\", "/");
            System.out.println(path);
            pathMap.put(path,key );
        });
        return pathMap;
    }

    @Test
    public void contextLoads() {
        File f = new File("G:");
        File[] files1 = f.listFiles();
        for (File file : files1) {
            System.out.println(file);
        }
        /*HashMap<String, String> map = toPathMap(f);
        File[] files = File.listRoots();
        for (File file : files) {
            System.out.println(file.toString());
            System.out.println(file.isDirectory());
        }
        System.out.println("C:\\".subSequence(0, 1));*/
    }

    @Test
    public void getHostIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":") == -1) {
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        // return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return null;
    }
}
