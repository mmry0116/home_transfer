package com.mmry.zip;

import com.mmry.until.ZipCompressor;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;


public class ZipDownload {

    public HttpServletResponse download(String fileName, HttpServletResponse response) {
        File file = new File(/*gitConfig.getDestPath() +*/ "/" + fileName);
        if (file.isDirectory()){
            return downDestroy(file, response);
        }else{
            return downFile(file,response);
        }

    }

    /**
     * 下载文件
     * @param file
     * @param response
     * @return
     */
    private HttpServletResponse downFile(File file, HttpServletResponse response) {
        InputStream fis = null;
        OutputStream toClient = null;
        try {
            // 以流的形式下载文件。
            fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            // 清空response
            response.reset();
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            //如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            toClient.write(buffer);
            toClient.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally{
            try {
                File f = new File(file.getPath());
                f.delete();
                if(fis!=null){
                    fis.close();
                }
                if(toClient!=null){
                    toClient.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * 下载文件夹
     * @param file
     * @param response
     * @return
     */
    private HttpServletResponse downDestroy(File file, HttpServletResponse response) {
        String zipFilePath = /*gitConfig.getDestPath()+"/"+*/file.getName()+"_"+System.currentTimeMillis()/1000+".zip";
        ZipCompressor zipCompressor = new ZipCompressor(zipFilePath);
        try {
            zipCompressor.compress(file.getPath());
            File zipFile = new File(zipFilePath);
            downFile(zipFile,response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
