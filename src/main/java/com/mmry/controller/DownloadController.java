package com.mmry.controller;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;


@Controller
public class DownloadController {
    @Value("${downloadPath:#{null}}")
    private String downloadPath;//设定客户下载时允许浏览的路径

    public HashMap<String, String> toPathMap(File drive) {
        if (!drive.isDirectory()) throw new RuntimeException("不是一个文件夹");
        HashMap<String, String> pathMap = new LinkedHashMap<>();
        HashMap<String, String> fileMap = new LinkedHashMap<>();
        Arrays.stream(drive.listFiles()).forEach(file -> {
            String key, path;
            if (file.isDirectory()) {
                if (file.isHidden()) {
                    key = "hide_directory";
                } else
                    key = "directory";
                path = file.toString().replaceAll("\\\\", "/");
                pathMap.put(path, key);
            } else {
                if (file.isHidden()) {
                    key = "hide_file";
                } else
                    key = "file";
                path = file.toString().replaceAll("\\\\", "/");
                fileMap.put(path, key);
            }
        });
        pathMap.putAll(fileMap);
        return pathMap;
    }

    @RequestMapping("/transfer/download.do")
    public String download(HttpServletRequest request) {

        File[] roots = File.listRoots();
        List<String> drives = new ArrayList<>();
        for (File root : roots) {
            drives.add(root.toString().replaceAll("\\\\", "/"));
        }
        if (roots.length == 0) throw new RuntimeException("客户根目录拒绝");
        File drive = roots.length > 1 ? roots[1] : roots[0];
        // HashMap<String, String> pathMap = toPathMap(new File("M:\\Git\\usr\\bin\\core_perl\\"));
        HashMap<String, String> pathMap = toPathMap(new File(drive.toString()));
        //如果配置了客户访问下载路径 则使用配置的路径
        if (downloadPath != null) {
            File downloadFile = new File(downloadPath);
            if (downloadFile.exists() && downloadFile.isDirectory()) {
                drives = null;
                pathMap = toPathMap(new File(downloadFile.toString()));
            }
        }
        request.getSession().setAttribute("drives", JSON.toJSONString(drives));
        request.getSession().setAttribute("pathMap", JSON.toJSONString(pathMap));
        return "/transfer/download";
    }

    @SneakyThrows
    @RequestMapping("/transfer/updatePath.do")
    public void updatePath(HttpServletResponse response, String path) {
        System.out.println("path: " + path);
        //将path合法化
        File file = new File(toLegalPath(path));
        HashMap<String, String> map = new HashMap<>();
        if (file.isDirectory()) {
            map = toPathMap(file);
        }
        response.getWriter().write(JSON.toJSONString(map));
    }

    private String toLegalPath(String path) {
        //如果配置下载路径为null 或者请求的路径包含下载的路径 那么是合法的
        if (downloadPath == null || path.contains(downloadPath)) {
            return path;
        }
        //非法路径处理
        File dPath = new File(downloadPath);
        if (dPath.isDirectory()) {
            return downloadPath;
        }
        String parentPath = dPath.getParent().toString();
        if (new File(parentPath).isDirectory()) {
            return parentPath;
        }
        throw new RuntimeException("配置的下载路径非法");
    }
}
