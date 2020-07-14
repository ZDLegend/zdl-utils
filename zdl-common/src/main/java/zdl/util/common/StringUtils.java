package zdl.util.common;

import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ZDLegend on 2016/8/16.
 * <p>
 * 字符转相关操作的函数在这里
 */
public final class StringUtils {

    /**
     * 编译后的正则表达式缓存
     */
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    private static final char CN_CHAR_START = '\u4e00';
    private static final char CN_CHAR_END = '\u9fa5';

    private StringUtils() {
    }

    /**
     * 把带'='号的String中，'='号两边的数据转为map
     *
     * @param queryString 被转换String
     * @param charset     中文编码
     * @return map
     */
    public static Map<String, String> queryStringToMap(String queryString, String charset) {
        try {
            Map<String, String> map = new HashMap<>();

            String[] decode = URLDecoder.decode(queryString, charset).split("&");
            for (String keyValue : decode) {
                String[] kv = keyValue.split("[=]", 2);
                map.put(kv[0], kv.length > 1 ? kv[1] : "");
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * byte[]数组拷贝
     */
    public static byte[] arrayCopy(byte[] dst, byte[] src) {
        int minLen = Math.min(dst.length, src.length);
        System.arraycopy(src, 0, dst, 0, minLen);
        return dst;
    }

    /**
     * 字符串转byte数组
     */
    public static void setSdkBytes(byte[] dst, String content) {
        byte[] srcBytes = content.getBytes(StandardCharsets.UTF_8);
        int size = Math.min(srcBytes.length, dst.length);
        System.arraycopy(srcBytes, 0, dst, 0, size == dst.length ? dst.length - 1 : size);
    }

    /**
     * JSONArray转byte[]
     * <p>
     * JSONArray转换为C代码中Char[x][y]
     * 既JSONArray -> byte[x*y] -> Char[x][y]
     */
    public static void byte2Copy(byte[] dst, JSONArray array, int x, int y) {

        if (array.size() < x) {
            x = array.size();
        }

        int length = x * y;
        int f = 0;
        StringBuilder sb = new StringBuilder();
        sb.setLength(length);
        for (int i = 0; i < x; i++) {
            sb.insert(f, array.getString(i));
            f = f + y;
        }
        StringUtils.setSdkBytes(dst, sb.toString());
    }

    /**
     * 生成人鱼长度的随机字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = new Random().nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * byte[]转String
     */
    public static String bytesToString(byte[] obj) {

        //trim()去掉末尾的 \u0020 也就是空格
        String str = new String(obj, StandardCharsets.UTF_8).trim();

        //去掉末尾的0
        if (str.indexOf('\u0000') > 0) {
            str = str.substring(0, str.indexOf('\u0000'));
        }

        return str;
    }


    /**
     * 去除驼峰结构中的前缀
     */
    public static String removeLowerHead(String word) {

        int index = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isUpperCase(c)) {
                index = i;
                break;
            }
        }

        word = word.replace(word.substring(0, index), "");
        return word;
    }

    /**
     * 把URL中的空格（%20）转化成普通的字符串空格
     */
    public static String spaceString(String url) {
        if (url.contains("%20")) {
            url = url.replace("%20", " ");
        }
        return url;
    }

    /**
     * 将字符串进行BASE64编码
     */
    public static String getBASE64(String s) {
        if (s == null) return null;
        return Arrays.toString(Base64.getEncoder().encode(s.getBytes()));
    }

    /**
     * 将字符串进行BASE64解码
     */
    public static String getFromBASE64(String s) {
        if (s == null) return null;
        try {
            byte[] b = Base64.getDecoder().decode(s);
            return new String(b, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 流转字节
     */
    public static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }

        return swapStream.toByteArray();
    }


    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String path) throws IOException {
        File file = new File(path);
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(bfile);
        }
    }

    /**
     * 编译一个正则表达式，并且进行缓存，如果换成已存在则使用缓存
     *
     * @param regex 表达式
     * @return 编译后的Pattern
     */
    public static Pattern compileRegex(String regex) {
        return PATTERN_CACHE.computeIfAbsent(regex, p -> {
            Pattern compile = Pattern.compile(p);
            PATTERN_CACHE.put(regex, compile);
            return compile;
        });
    }

    /**
     * 将字符串的第一位转为小写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String toLowerCaseFirstOne(String str) {
        if (Character.isLowerCase(str.charAt(0)))
            return str;
        else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
    }

    /**
     * 将字符串的第一位转为大写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String toUpperCaseFirstOne(String str) {
        if (Character.isUpperCase(str.charAt(0)))
            return str;
        else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
    }

    /**
     * 下划线命名转为驼峰命名
     *
     * @param str 下划线命名格式
     * @return 驼峰命名格式
     */
    public static String underScoreCase2CamelCase(String str) {
        if (!str.contains("_")) return str;
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        boolean hitUnderScore = false;
        sb.append(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                hitUnderScore = true;
            } else {
                if (hitUnderScore) {
                    sb.append(Character.toUpperCase(c));
                    hitUnderScore = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰命名法转为下划线命名
     *
     * @param str 驼峰命名格式
     * @return 下划线命名格式
     */
    public static String camelCase2UnderScoreCase(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将异常栈信息转为字符串
     *
     * @param e 字符串
     * @return 异常栈
     */
    public static String throwable2String(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /**
     * 字符串连接，将参数列表拼接为一个字符串
     *
     * @param more 追加
     * @return 返回拼接后的字符串
     */
    public static String concat(Object... more) {
        return concatSpiltWith("", more);
    }

    public static String concatSpiltWith(String split, Object... more) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < more.length; i++) {
            if (i != 0) buf.append(split);
            buf.append(more[i]);
        }
        return buf.toString();
    }

    /**
     * 将字符串转移为ASCII码
     *
     * @param str 字符串
     * @return 字符串ASCII码
     */
    public static String toASCII(String str) {
        StringBuilder strBuf = new StringBuilder();
        byte[] bGBK = str.getBytes();
        for (byte b : bGBK) {
            strBuf.append(String.format("%02X", b));
        }
        return strBuf.toString();
    }

    public static String toUnicode(String str) {
        StringBuilder strBuf = new StringBuilder();
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            strBuf.append("\\u").append(Integer.toHexString(aChar));
        }
        return strBuf.toString();
    }

    public static String toUnicodeString(char[] chars) {
        StringBuilder strBuf = new StringBuilder();
        for (char aChar : chars) {
            strBuf.append("\\u").append(Integer.toHexString(aChar));
        }
        return strBuf.toString();
    }

    /**
     * 是否包含中文字符
     *
     * @param str 要判断的字符串
     * @return 是否包含中文字符
     */
    public static boolean containsChineseChar(String str) {
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if (aChar >= CN_CHAR_START && aChar <= CN_CHAR_END) return true;
        }
        return false;
    }

    public static String matcherFirst(String patternStr, String text) {
        Pattern pattern = compileRegex(patternStr);
        Matcher matcher = pattern.matcher(text);
        String group = null;
        if (matcher.find()) {
            group = matcher.group();
        }
        return group;
    }

    /**
     * 分隔字符串,根据正则表达式分隔字符串,只分隔首个,剩下的的不进行分隔,如: 1,2,3,4 将分隔为 ['1','2,3,4']
     *
     * @param str   要分隔的字符串
     * @param regex 分隔表达式
     * @return 分隔后的数组
     */
    public static String[] splitFirst(String str, String regex) {
        return str.split(regex, 2);
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 提取字符串中所有数字
     *
     * @param content 字符串
     * @return 字符串中的所有数字数组
     */
    public static List<Integer> getDigit(String content) {
        String[] cs = Pattern.compile("[^0-9]+").split(content);
        return Stream.of(cs).distinct()
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .filter(s -> s.length() < 5)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
                chLength++;
            }
        }
        float result = count / chLength;
        return result > 0.4;
    }

    /**
     * 字符串模糊匹配
     * 模板字符串中'*'代表多位，'?'代表一位，例如：zd*ge?? 和 zdlegend 可以匹配
     *
     * @param regex    被匹配字符串
     * @param template 模板字符串
     * @return 是否匹配
     */
    public static boolean stringVagueMatch(String regex, String template) {

        if (org.apache.commons.lang3.StringUtils.isBlank(regex)
                || org.apache.commons.lang3.StringUtils.isBlank(template)) {
            return false;
        }

        //正则表达式转换
        String input = "^" + template.replace("*", "([\\S]*)")
                .replace("?", "([\\S])").trim() + "$";
        //正则表达式匹配
        return Pattern.compile(regex).matcher(input).find();
    }

    /**
     * 字符串含有sql查询中like模糊查询中的特殊含义字符转换
     */
    private static final List<Character> specialChars = Arrays.asList('_', '%', '[', ']', '`', '\\');

    public static String specialCharacters(String target) {
        char[] chars = target.trim().toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char character : chars) {
            if (specialChars.contains(character)) {
                sb.append("\\");
            }
            sb.append(character);
            if ('{' == character) {
                sb.append("}");
            }
        }
        return sb.toString();
    }
}
