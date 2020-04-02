package cn.qw.kit;

import com.jfinal.kit.PropKit;

import java.io.*;

public class UploadKit {

    public static String uploadFile(byte[] b, String uuidFileName) {
        String path = PropKit.get("file_path") + File.separator + "jfile" + File.separator + uuidFileName;
        File dir = new File(path);
        if (!dir.exists() && dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(path);
        FileOutputStream fis = null;
        BufferedOutputStream bos = null;
        try {
            //获取输入流
            fis = new FileOutputStream(file);
            //新的 byte 数组输出流，缓冲区容量1024byte
            bos = new BufferedOutputStream(fis);
            bos.write(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bos != null) {
                    bos.close();
                    bos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "api/file/show?p=" + uuidFileName;
    }
}
