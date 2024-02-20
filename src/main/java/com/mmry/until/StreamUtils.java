package com.mmry.until;

import java.io.*;

public class StreamUtils {
    /**
     * 将InputStream 转为byte[]
     */
    public static byte[] StreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int read = 0;
        while ((read = is.read(bytes)) != -1) {
            os.write(bytes, 0, read);
        }
        byte[] array = os.toByteArray();
        os.close();
        return array;
    }

    /**
     * 将InputStream 转为String
     */
    public static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuffer stringBuffer = new StringBuffer();
        String buf;
        while ((buf = bufferedReader.readLine()) == null) {
            stringBuffer.append(buf+"\n\t");
        }
        return stringBuffer.toString();
    }
}
