package zdl.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;

public class Base64Util {
    private static final int CACHE_SIZE = 1024;

    public static byte[] decode(String base64)
            throws Exception {
        return Base64.decode(base64);
    }

    public static String encode(byte[] bytes)
            throws Exception {
        return new String(Base64.encode(bytes));
    }

    public static String encode(String str)
            throws Exception {
        return new String(Base64.encode(str.getBytes("UTF-8")));
    }

    public static String encodeFile(String filePath)
            throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    public static void decodeToFile(String filePath, String base64)
            throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    public static byte[] fileToByte(String filePath)
            throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    public static void byteArrayToFile(byte[] bytes, String filePath)
            throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[1024];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }
}