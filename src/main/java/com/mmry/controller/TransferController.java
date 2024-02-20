package com.mmry.controller;

import com.alibaba.fastjson.JSON;
import com.mmry.entry.Msg;
import com.mmry.until.StreamUtils;
import com.mmry.until.ZipCompressor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;


@Slf4j
@Controller
public class TransferController {
    @Value("${uploadDir:#{null}}")
    private String uploadDir;//前端上传文件的存放的目录

    @Value("${filePaht_tempZipPack:#{null}}")
    private String tempPackPath;//前端下载多个文件时候创建的zip压缩包存放的路径

    public TransferController() {
    }

    @RequestMapping({"/hello.do"})
    public String hello() {
        System.out.println("hello...");
        return "setting/b";
    }

    @RequestMapping({"/upload.do"})
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

    @RequestMapping({"/uploadb.do"})
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
     * 此方法将文件全部转成 byte[] 然后再写入前端
     * 不适合大文件传输
     *
     * @param filePath
     * @param response
     */
    @RequestMapping("/download.do")
    public void download(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        try {
            File file = new File(filePath);

            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            //  log.info("文件后缀名：" + ext);
            log.info("文件名：" + filename);

            FileInputStream is = new FileInputStream(file);
            byte[] bytes = StreamUtils.StreamToByteArray(is);

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream bos = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");

            bos.write(bytes);
            bos.flush();
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
    @RequestMapping("/download2.do")
    public void download2(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        //获取路径
        Boolean isFile = true;
        try {
            File file = new File(filePath);
            //如果下载的是文件夹 则把文件夹压缩成zip包传输给前端
            if (file.isDirectory()) {
                //标记下载的是文件夹,这个标记用再后面判断将zip压缩包删除
                isFile = false;
                String zipFilePath;
                //处理零时存放zip压缩包的路径
                if (tempPackPath == null || tempPackPath.equals("")) {
                    tempPackPath = System.getProperty("user.home").toString() + "/upload";
                    File tempPack = new File(tempPackPath);
                    if (!tempPack.exists())
                        tempPack.mkdir();
                }
                zipFilePath = tempPackPath + "/" + file.getName() + "_" + System.currentTimeMillis() / 1000 + ".zip";
                ZipCompressor zipCompressor = new ZipCompressor(zipFilePath);
                zipCompressor.compress(file.toString());
                file = new File(zipFilePath);

            }
            // 获取文件名
            String filename = file.getName();
            /*// 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            log.info("文件后缀名：" + ext);*/
            log.info("下载文件名：" + filename);

            FileInputStream is = new FileInputStream(file);
            response.reset();
            response.setContentType("application/octet-stream");
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
            if (!isFile) {
                file.delete();
                log.info("delete: " + file);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
