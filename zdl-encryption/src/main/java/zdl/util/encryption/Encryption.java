package zdl.util.encryption;

/**
 * 加密算法基类
 *
 * @author ZDLegend
 * @since 2020/07/14 15:09
 */
public interface Encryption {

    String AES = "AES";
    String AES_INSTANCE = "AES/CFB/NoPadding";

    String ECC_EC = "EC";
    String ECC_BC = "BC";
    String ECC_ECIES = "ECIES";

    String SHA1withECDSA = "SHA1withECDSA";

    /**
     * MAC算法可选以下多种算法
     * HmacMD5/HmacSHA1/HmacSHA256/HmacSHA384/HmacSHA512
     */
    String KEY_MAC = "HmacMD5";

    String KEY_MD5 = "MD5";

    String KEY_RSA = "RSA";
    String RSA_INSTANCE = "RSA/CFB/NoPadding";
    String RSA_PKCS1 = "RSA/ECB/PKCS1Padding";
    String SHA1WithRSA = "SHA1WithRSA";

    String KEY_SHA1 = "SHA1";

    /**
     * 加密
     */
    byte[] encrypt(byte[] data);

    /**
     * 解密
     */
    byte[] decrypt(byte[] secret);

    /**
     * 验证
     */
    boolean verify(byte[] sign, byte[] content);

}
