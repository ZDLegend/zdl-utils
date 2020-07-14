package zdl.util.encryption;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 生成SHA1编码
 * <p>
 * SHA1 是和 MD5 一样流行的消息摘要算法，然而 SHA1 比 MD5 的 安全性更强。
 * 对于长度小于 2 ^ 64 位的消息，SHA1 会产生一个 160 位的 消息摘要。
 * 基于 MD5、SHA1 的信息摘要特性以及不可逆 (一般而言)，可以被应用在检查文件完整性以及数字签名等场景。
 *
 * @author ZDLegend
 * @since 2020/07/14 17:09
 */
public class SHA1 {
    public static String digest(String ps) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        sha1.update(ps.getBytes());
        byte[] results = sha1.digest();
        return new String(results);
    }

    public static byte[] digest(byte[] content) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            return sha1.digest(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String digestForCodec(String ps) throws Exception {
        byte[] results = DigestUtils.getSha1Digest().digest(ps.getBytes());
        return new String(results);
    }
}
