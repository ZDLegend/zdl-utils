package zdl.util.encryption;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * HMAC编码
 * <p>
 * MHMAC 是密钥相关的哈希运算消息认证码（Hash-based Message Authentication Code），
 * HMAC 运算利用哈希算法 (MD5、SHA1 等)，以一个密钥和一个消息为输入，生成一个消息摘要作为输出。
 * <p>
 * HMAC 发送方和接收方都有的 key 进行计算，而没有这把 key 的第三方，则是无法计算出正确的散列值的，这样就可以防止数据被篡改。
 *
 * @author ZDLegend
 * @since 2020/07/14 17:23
 */
public class HMAC implements Encryption {

    private Mac mac;

    public HMAC(String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_MAC);
            mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
        } catch (Exception e) {
            throw new ZDLDigestException("HMAC解密失败", e);
        }
    }

    @Override
    public byte[] encrypt(byte[] content) {
        return mac.doFinal(content);
    }

    @Override
    public byte[] decrypt(byte[] secret) {
        throw new ZDLDigestException("HMAC无法解密");
    }

    @Override
    public boolean verify(byte[] signature, byte[] content) {
        try {
            byte[] result = mac.doFinal(content);
            return Arrays.equals(signature, result);
        } catch (Exception e) {
            throw new ZDLDigestException("HMAC验证失败", e);
        }
    }
}
