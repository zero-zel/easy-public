package main.utils;

import java.io.*;

public class FileUtil {

    /**
     * 将file转换为inputStream
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream fileToInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    /**
     * 将inputStream转化为file
     * @param is
     * @param file 要输出的文件目录
     */
    public static void inputStreamToFile(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[8192];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }

}
