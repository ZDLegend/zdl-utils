package zdl.util.common;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    public static byte[] decode(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String encode(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static String encode(String str) {
        return new String(Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)));
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
            try (FileInputStream in = new FileInputStream(file);
                 ByteArrayOutputStream out = new ByteArrayOutputStream(2048)) {
                byte[] cache = new byte[1024];
                int nRead;
                while ((nRead = in.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                    data = out.toByteArray();
                }
            }
        }
        return data;
    }

    public static void byteArrayToFile(byte[] bytes, String filePath)
            throws Exception {

        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        destFile.createNewFile();

        try (InputStream in = new ByteArrayInputStream(bytes);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] cache = new byte[1024];
            int nRead;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
        }
    }
}