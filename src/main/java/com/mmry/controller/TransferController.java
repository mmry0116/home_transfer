package com.mmry.controller;

import com.alibaba.fastjson.JSON;
import com.mmry.entry.Msg;
import com.mmry.until.ZipCompressor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;


@Controller
public class TransferController {
    @Value("${uploadDir:#{null}}")
    private String uploadDir;//前端上传文件的存放的目录

    @Value("${filePaht_tempZipPack:#{null}}")
    private String tempPackPath;//前端下载多个文件时候创建的zip压缩包存放的路径

    //开发时 前后端分离导致浏览器访问不同源时 后端的url
    @Value("${cros.url:#{null}}")
    private String crosUrl;

    public TransferController() {
    }

//    @RequestMapping({"/upload.do"})
    public void upload(@RequestParam("files") MultipartFile[] files, HttpServletResponse response, HttpServletRequest request) {
        try {
            String path = request.getSession().getServletContext().getRealPath("/upload");
            System.out.println("path: " + path);
            Msg msg = new Msg();
            if (files.length == 0) {
                System.out.println("没有文件上传...");
                msg.setContent("没有文件上传...");
                response.getWriter().write(JSON.toJSONString(msg));
            } else {
                System.out.println("文件上传个数: " + files.length);

                for (int i = 0; i < files.length; ++i) {
                    MultipartFile file = files[i];
                    System.out.println("size: " + file.getSize());
                    String oldName = file.getOriginalFilename();
                    String destPath = System.getProperties().getProperty("user.home") + "/upload/" + oldName;
                    System.out.println("destPath: " + destPath);
                    File destFile = new File(destPath);
                    if (!destFile.getParentFile().exists()) {
                        destFile.getParentFile().mkdir();
                    }

                    file.transferTo(destFile);
                    msg.setContent(msg.getContent() + oldName + "\n");
                }

                response.getWriter().write(JSON.toJSONString(msg));
            }
        } catch (IOException var13) {
            throw new RuntimeException(var13);
        }
    }

//    @RequestMapping({"/uploadb.do"})
    public void upload2(@RequestParam("files") MultipartFile[] files, HttpServletResponse response, HttpServletRequest request) {
        try {
            Msg msg = new Msg();
            if (files.length == 0) {
                System.out.println("没有文件上传....");
                msg.setContent("没有文件上传...");
                response.getWriter().write(JSON.toJSONString(msg));
            } else {
                System.out.println("文件上传个数:  " + files.length);

                for (int i = 0; i < files.length; ++i) {
                    MultipartFile file = files[i];
                    System.out.println("size: " + file.getSize());
                    String oldName = file.getOriginalFilename();
                    String destPath = System.getProperties().getProperty("user.home") + "\\upload\\" + oldName;
                    //验证配置上传文件路径是否合法
                    if (uploadDir != null && !new File(uploadDir).isFile()) {
                        destPath = uploadDir + "\\" + oldName;
                    }
                    File destFile = new File(destPath);

                    System.out.println("destPath: " + destPath);
                    if (!destFile.getParentFile().exists()) {
                        destFile.getParentFile().mkdir();
                    }

                    file.transferTo(destFile);
                    msg.setContent(msg.getContent() + oldName + "\n");
                }

                response.getWriter().write(JSON.toJSONString(msg));
            }
        } catch (IOException var12) {
            throw new RuntimeException(var12);
        }
    }

    /**
     * 单个文件下载
     * @param filePath
     * @param response
     */
    @ResponseBody
    @RequestMapping("/download.do")
    public void download(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        System.out.println("filePath: " + filePath);
        System.out.println(crosUrl);
        //处理不同系统的斜杠
        if ("/".equals(File.separator)) {
            filePath = filePath.replaceAll("\\\\", "\\/");
        }else  filePath = filePath.replaceAll("\\/", "\\\\");

        try {
            File file = new File(filePath);
            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            //  log.info("文件后缀名：" + ext);
            System.out.println(filename);

            // 清空response
            response.reset();
            response.setHeader("Access-Control-Allow-Origin", crosUrl);
            response.setHeader("Access-Control-Allow-Credentials", "true");
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
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    /**
     * 此方法与客户端建立通到 将传输文件转成byte[] 一点一点写入前端
     * 适合大文件传输
     *
     * @param filePath
     * @param response
     */
    @ResponseBody
    @RequestMapping("/download2.do")
    public void download2(@RequestParam("filePath") String[] filePath, HttpServletResponse response) {
        System.out.println(crosUrl);
        if (filePath == null) return;
        String[] tempArr = new String[filePath.length];
        if ("/".equals(File.separator)) {
            for (int i = 0; i < filePath.length; i++) {
                tempArr[i] = filePath[i].replaceAll("\\\\", "\\/");
            }
        }else {
            for (int i = 0; i < filePath.length; i++) {
                tempArr[i] = filePath[i].replaceAll("\\/", "\\\\");
            }
        }
        filePath =  tempArr;

        //获取路径
        Boolean isCompressFile = false;
        System.out.println("filePath: " + filePath);
        try {
            File file;
            Long pathsSize = pathsSize(filePath);
            if (filePath.length == 1 && new File(filePath[0]).isFile()) {
                file = new File(filePath[0]);
            } else if ( pathsSize > 1 * 1024 * 1024 * 1024) {
                System.out.println("文件大小超过1G拒绝服务");
                file = new File("文件大小超过1G拒绝服务.txt");
                FileWriter fileWriter= new FileWriter(file);
                fileWriter.write("文件大小超过1G拒绝服务\n可以排除大文件进行下载！!\n大文件可以单独下载");
                fileWriter.flush();
            } else {
                String allPath="";
                isCompressFile = true;//标记下载的是文件夹,这个标记用再后面判断将zip压缩包删除
                for (String path : filePath) {
                    allPath+=path;
                }
                int fileHash = allPath.hashCode();
                //打包文件名字
                String fileName = filePath[0].substring(filePath[0].lastIndexOf(File.separator)).replaceAll("-","") +"-" + fileHash + "-" + pathsSize + ".zip";
                String zipFilePath;
                //处理零时存放zip压缩包的路径
                if (tempPackPath == null || tempPackPath.equals("")) {
                    tempPackPath = System.getProperty("user.home") + File.separator + "upload";
                    File tempPack = new File(tempPackPath);
                    if (!tempPack.exists())
                        tempPack.mkdir();
                }
                zipFilePath = tempPackPath  + fileName;
                //判断是否还有缓存的zip压缩包
                File zipPath = isContainZip(fileHash, pathsSize, tempPackPath);
                StopWatch watch = new StopWatch();
                watch.start();
                if (zipPath == null){
                    file = new File(zipFilePath);
                    ZipCompressor zipCompressor = new ZipCompressor(zipFilePath);
                    zipCompressor.compress(filePath);
                }else {
                    file = zipPath;
                }
                watch.stop();
                System.out.println(watch.prettyPrint());
            }
            // 获取文件名
            String filename = file.getName();
            System.out.println(filename);
            FileInputStream is = new FileInputStream(file);
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Access-Control-Allow-Origin", crosUrl);
            response.setHeader("Access-Control-Allow-Credentials", "true");

            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            ServletOutputStream os = response.getOutputStream();
            byte[] b = new byte[4096];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = is.read(b)) > 0) {
                os.write(b, 0, len);
            }
            os.close();
            is.close();
            //判断下载的是文件夹就将临时的zip压缩包删除
            if (isCompressFile) {
//                file.delete();
                System.out.println("delete: " + file);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //filePath[0].substring(3) +"-" + fileHash + "-" + pathsSize + ".zip"
    // 找到文件就返回文件路径 否则返回null
    public File isContainZip(int hash, Long size, String tempPackPath){
        File tempFolder = new File(tempPackPath);
        File[] files = tempFolder.listFiles();
        for (File file : files) {
            String[] split = file.getName().substring(0, file.getName().length() - 4).split("-");
            //如果是不是文件夹 而且
            if ( file.isFile() && split.length >= 3 &&
                    split[1].equals(String.valueOf(hash)) && Long.parseLong(split[2])==size) {
                return file;
            }
        }
        return null;
    }

    public boolean isSuperSize(String[] filePaths){
        return pathsSize(filePaths) > 1 * 1024 * 1024 * 1024;
    }

    public Long pathsSize(String[] filePaths){
        long size = 0L;
        for (String path : filePaths) {
            File f = new File(path);
            if (f.isFile()) {
                size+=f.length();
            }else size += FileUtils.sizeOfDirectory(f);
        }
        System.out.println("下载文件大小：" + size);
        return size;
    }

}
