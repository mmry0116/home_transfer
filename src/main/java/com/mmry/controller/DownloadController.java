package com.mmry.controller;

import com.alibaba.fastjson.JSON;
import com.mmry.entry.FileEntity;
import com.mmry.entry.Msg;
import com.mmry.until.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Controller
public class DownloadController {

    @Value("${downloadPath:#{null}}")
    private String downloadPath;//设定客户下载时允许浏览的路径

    public HashMap<String, String> toPathMap2(File drive) {
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

    public List<FileEntity> toPathMap(File drive) {
        if (!drive.isDirectory()) throw new RuntimeException("不是一个文件夹");
        List<FileEntity> pathList = new LinkedList<>();
        List<FileEntity> fileList = new LinkedList<>();
        Arrays.stream(drive.listFiles()).forEach(file -> {
            FileEntity fileEntity = new FileEntity();
            if (file.isDirectory()) {
                if (file.isHidden()) {
                    fileEntity.setType("hide_directory");
                } else
                    fileEntity.setType("directory");
                pathList.add(addInfoToFile(file, fileEntity));
            } else {
                if(isVideo(file)){
                    fileEntity.setType("video");
                } else if (isMusic(file)) {
                    fileEntity.setType("music");
                } else if (isImage(file)) {
                    fileEntity.setType("image");
                }else if (file.isHidden()) {
                    fileEntity.setType("hide_file");
                } else
                    fileEntity.setType("file");
                fileList.add(addInfoToFile(file, fileEntity));
            }
        });
        pathList.addAll(fileList);
        return pathList;
    }

    private boolean isVideo(File file) {
        //https://blog.csdn.net/ffffffff8/article/details/118671761
        String s1 = "-mp4-flv-f4v-webm-"; //常见在线流媒体格式
        String s2 = "m4v-mov-3gp-3g2-"; //移动设备格式
        String s3 = "rm-rmvb-"; //RealPlayer
        String s4 = "wmv-avi-asf-"; //微软格式
        String s5 = "mpg-mpeg-mpe-ts-"; //MPEG 视频
        String s6 = "div-dv-divx-"; //DV格式
        String s7 = "vob-dat-mkv-lavf-cpk-dirac-ram-qt-fli-flc-mod-";//其他格式
        String videoType = s1 + s2 + s3 + s4 + s5 + s6 + s7;
        String name = file.getName().toLowerCase();
        String fileType = name.substring(name.lastIndexOf('.')+1);
        return videoType.indexOf(fileType) > 0;
    }
    private boolean isMusic(File file) {
        //http://www.dayanzai.me/audio-file-format.html
        String musicType = "-PCM-WAV-AIFF-MP3-AAC-OGG-WMA-FLAC-ALAC-WMA-";
        String name = file.getName().toUpperCase();
        String fileType = name.substring(name.lastIndexOf('.')+1);
        return musicType.indexOf(fileType) > 0;
    }
    private boolean isImage(File file) {
        String imageType = "-WEBP-BMP-PCX-TIF-GIF-JPEG-JPG-TGA-EXIF-FPX-SVG-PSD-CDR-PCD-DXF-UFO-EPS-PNG-WEBP-";
        String name = file.getName().toUpperCase();
        String fileType = name.substring(name.lastIndexOf('.')+1);
        return imageType.indexOf(fileType) > 0;
    }

    //G:/TIM/All Users/QQ/Misc/com.tencent.audiovideo
    private FileEntity addInfoToFile(File file, FileEntity fileEntity) {
        fileEntity.setName(file.getName());
        String path = file.getAbsolutePath().replaceAll("\\\\", "/");
        int last = path.lastIndexOf("/");
        fileEntity.setPath(path.substring(0, last + 1));
        fileEntity.setSize(file.length() + "");
        BasicFileAttributes attrs;
        try {
            attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.of("Asia/Shanghai"))
                .format(formatter);
        String lastModifiedTime = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.of("Asia/Shanghai"))
                .format(formatter);
        String lastAccessTime = LocalDateTime.ofInstant(attrs.lastAccessTime().toInstant(), ZoneId.of("Asia/Shanghai"))
                .format(formatter);
        fileEntity.setCreationTime(createTime);
        fileEntity.setLastAccessTime(lastModifiedTime);
        fileEntity.setLastModifiedTime(lastAccessTime);
        if (file.isDirectory()) {
            fileEntity.setSortPriority((byte) 1);
        } else fileEntity.setSortPriority((byte) 2);
        return fileEntity;
    }

    private String toLegalPath(String path) {
        //如果配置下载路径为null 或者请求的路径包含下载的路径 那么是合法的
        if (downloadPath == null || ((path != null) && path.contains(downloadPath))) {
            return path;
        }
        //非法路径处理
        File dPath = new File(downloadPath);
        if (dPath.isDirectory()) {
            return downloadPath;
        }
        String parentPath = dPath.getParent();
        if (new File(parentPath).isDirectory()) {
            return parentPath;
        }
        throw new RuntimeException("配置的下载路径非法");
    }

    //用户进入downloadSpace 初始化的接口
    @RequestMapping("/transfer/download.do")
    @ResponseBody
    public void download( HttpServletResponse response) throws IOException {
        System.out.println("download....");
        File[] roots = File.listRoots();
        List<String> drives = new ArrayList<>();
        for (File root : roots) {
            String r = root.toString();
            drives.add(r.replaceAll("\\\\", "/").substring(0, r.length() - 1));
        }
        if (roots.length == 0) throw new RuntimeException("客户根目录拒绝");
        File drive = roots.length > 1 ? roots[1] : roots[0];
        // HashMap<String, String> pathMap = toPathMap(new File("M:\\Git\\usr\\bin\\core_perl\\"));
        List<FileEntity> pathMap = toPathMap(new File(drive.toString()));
        //如果配置了客户访问下载路径 则使用配置的路径
        if (downloadPath != null) {
            File downloadFile = new File(downloadPath);
            if (downloadFile.exists() && downloadFile.isDirectory()) {
                drives.clear();
                drives.add(downloadPath.replaceAll("\\\\", "/").split("/")[0]);
                pathMap = toPathMap(new File(downloadFile.toString()));
            }
        }
        Msg msg = new Msg();
        msg.setDrivers(drives);
        msg.setData(pathMap);
        response.addHeader("Access-Control-Max-Age", "1800");
        response.getWriter().write(JSON.toJSONString(msg));
    }

    //更新目录结构接口
    @RequestMapping("/transfer/updatePath.do")
    @ResponseBody
    public void updatePath(HttpServletResponse response, @RequestParam("path") String path) throws IOException {
        if (path == null) return;
        System.out.println("path: " + path);
        //将path合法化
        File file = new File(toLegalPath(path));
        List<FileEntity> map = new LinkedList<>();
        if (file.isDirectory()) {
            map = toPathMap(file);
        }

        Msg msg = new Msg();
        msg.setCode(StatusConstant.OK);
        msg.setData(map);
        response.addHeader("Access-Control-Max-Age", "1800");
        response.getWriter().write(JSON.toJSONString(msg));
//        response.getWriter().write(JSON.toJSONString(map));
    }

    //
    @RequestMapping("/transfer/updatePathOnlyFolder.do")
    @ResponseBody
    @SuppressWarnings("ConstantConditions")
    public void updatePathOnlyFolder(HttpServletResponse response, @RequestParam("path") String path) throws IOException {
        if (path == null) return;
        System.out.println("path: " + path);
        //将path合法化
        File file = new File(toLegalPath(path));
        List<String> list = new LinkedList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (!f.isDirectory()) continue;
                list.add(f.getName());
            }
        }

        Msg msg = new Msg();
        msg.setCode(StatusConstant.OK);
        msg.setFolderNames(list);
        response.getWriter().write(JSON.toJSONString(msg));
//        response.getWriter().write(JSON.toJSONString(map));
    }

    //创建一个文件夹
    @ResponseBody
    @RequestMapping("/transfer/createFolder.do")
    public void createFolder(@RequestParam("folderPath") String folderPath, HttpServletResponse response) throws IOException {
        if (folderPath == null) return;
        Msg msg = new Msg();
        msg.setCode(StatusConstant.OK);
        System.out.println(folderPath);
        Path path = Paths.get(folderPath);
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        } else {//如果文件夹已经存在也正常返回
            if (Files.isRegularFile(path)) { //如果传过来的是一个文件路径 就报错
                msg.setCode("400");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        }
        response.getWriter().write(JSON.toJSONString(msg));
    }

    @Autowired
    @SuppressWarnings("all")
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @ResponseBody
    @RequestMapping("/transfer/palyVideo.do")
    //播放视频文件 https://www.coderbbb.com/articles/39
    public void palyVideo(@RequestParam("path") String path, HttpServletRequest request, HttpServletResponse response)  {
        System.out.println("palyVideo.do: "+ path);
        try {
            File file = new File(path);
            if (file.exists()) {
                request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, path);
                nonStaticResourceHttpRequestHandler.handleRequest(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping("/transfer/displayImage.do")
    @SuppressWarnings("unused")
    //图片接口
    public void displayImage(@RequestParam("path") String path,  HttpServletResponse response)  {
        System.out.println("palyVideo.do: "+ path);
        try {
            File file = new File(path);
            if ( !file.exists() || !file.isFile()) {
                return;
            }
            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 清空response
            response.reset();
            response.setHeader("Access-Control-Allow-Origin", "*");
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());

            FileInputStream is = new FileInputStream(file);
            response.setContentType("application/octet-stream");
            ServletOutputStream os = response.getOutputStream();
            byte[] b = new byte[4096];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = is.read(b)) > 0) {
                os.write(b, 0, len);
            }
            os.close();
            is.close();
        } catch (FileNotFoundException e) {
           e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //删除文件接口
    @ResponseBody
    @RequestMapping({"/transfer/delete.do"})
    public void delete(@RequestParam("filePath") String[] filePath, HttpServletResponse response) throws IOException {
        //对路径合法验证 目录不能互相包含 不支持根目录删除 todo 前端已经限制了
        if (filePath == null || filePath.length == 0) {
            //返回
            return;
        }
        ArrayList<String> failPath = DeleteFile.deleteAllOfPaths(filePath);
        Msg msg = new Msg();
        if (failPath.size() == 0) {
            msg.setCode(StatusConstant.OK);
            response.getWriter().write(JSON.toJSONString(msg));
            return;
        }
        msg.setCode(StatusConstant.PART_SUCCESS);
        msg.setContent(JSON.toJSONString(failPath));
        response.getWriter().write(JSON.toJSONString(msg));
    }

    //文件夹 文件剪切操作
    @ResponseBody
    @RequestMapping({"/transfer/shear.do"})
    public void shear(@RequestParam("filePath") String[] filePath, @RequestParam("targetPath") String targetPath, @RequestParam("oper") String oper, HttpServletResponse response) throws IOException {
        if (filePath == null || filePath.length == 0 ||
                targetPath.equals("") ||
                oper.equals("")  ) {
            return;
        }
        //可以部分移动成功 移动时候需要备份 备份时候需要判断磁盘容量
        //如果移动失败 就把之前备份的复制回去复原 如果备份失败就取消移动
        ArrayList<String> failPath;
        if (oper.equals("shear")) {
            failPath = DeleteFile.shearAllOfPaths(filePath, targetPath);
        //如果目标不是目录 则直接返回
        } else if (!new File(targetPath).isDirectory()) {
            failPath = new ArrayList<>(Arrays.asList(filePath));
        } else {
            //判断容量是否足够
            long usableSpace = new File(targetPath).getUsableSpace();
            long totalSize = 0L;
            for (String path : filePath) {
                File file = new File(path);
                if (file.isDirectory()) {
                    totalSize += FileUtils.sizeOfDirectory(new File(path));
                } else {
                    totalSize += FileUtils.sizeOf(file);
                }
            }
            if (totalSize * 1.1 > usableSpace) {
                failPath = new ArrayList<>(Arrays.asList(filePath));
            } else {
                failPath = DeleteFile.copyAllOfPaths(filePath, targetPath);
            }
        }
        Msg msg = new Msg();
        if (failPath.size() != 0) {
            msg.setCode(StatusConstant.PART_SUCCESS);
            msg.setContent(JSON.toJSONString(failPath));
            response.getWriter().write(JSON.toJSONString(msg));
            return;
        }
        msg.setCode(StatusConstant.OK);
        response.getWriter().write(JSON.toJSONString(msg));
    }


}
