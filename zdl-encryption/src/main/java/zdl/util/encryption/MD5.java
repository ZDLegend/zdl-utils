package zdl.util.encryption;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 生成MD5编码
 * <p>
 * MD5用的是哈希函数，它的典型应用是对一段信息产生信息摘要，以防止被篡改。严格来说，MD5不是一种加密算法而是摘要算法。
 * 无论是多长的输入，MD5都会输出长度为128bits的一个串 (通常用16进制表示为32个字符)。
 *
 * @author ZDLegend
 * @since 2020/07/14 16:25
 */
public class MD5 {

    /**
     * 生成MD5摘要值
     */
    public static String digest(String ps) throws NoSuchAlgorithmException {
        if (null != ps) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(ps.getBytes());
            byte[] results = md.digest();
            return bytesToHex(results);
        } else {
            return null;
        }
    }

    public static byte[] digest(byte[] content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用commons-codec生成MD5摘要值
     */
    public static String digestForCodec(String ps) throws Exception {
        return DigestUtils.md5Hex(ps);
    }

    /**
     * MD5验证
     *
     * @param ps  验证值
     * @param md5 密文
     */
    public static boolean verify(String ps, String md5) throws Exception {
        String md5str = digest(ps);
        return md5str.equalsIgnoreCase(md5);
    }

    /**
     * 二进制转十六进制
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();

        /* 把数组每一字节换成16进制连成md5字符串 */
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toLowerCase();
    }
}
