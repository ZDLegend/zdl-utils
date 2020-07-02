package zdl.util.common;

/**
 * 常用正则表达式
 * <p>
 * example:
 * //效验QQ号（要求：5-15位数字，0不能开头）
 * public static boolean checkQQ(String qq) {
 * return qq.matches(QQ_MATCHER.getRegex());
 * }
 *
 * @author ZDLegend
 * @create 2020/07/01
 */
public enum Regexs {

    /**
     * 数字类正则表达式
     */
    NUMBER_MATCHER("^[0-9]*$", "数字"),
    NUMBER_N_MATCHER("^\\d{n}$", "n位的数字"),
    NUMBER_AT_LEAST_N_MATCHER("^\\d{n,}$", "至少n位的数字"),
    NUMBER_M_TO_N_MATCHER("^\\d{m,n}$", "m-n位的数字"),

    NUMBER_0_MATCHER("^(0|[1-9][0-9]*)$", "零和非零开头的数字"),
    NUMBER_NO0_MATCHER("^([1-9][0-9]*)+(.[0-9]{1,2})?$", "非零开头的最多带两位小数的数字"),
    NUMBER_TWO_MATCHER("^(\\-)?\\d+(\\.\\d{1,2})?$", "带1-2位小数的正数或负数"),

    NUMBER_POSITIVE_NEGATIVE_DECIMAL_MATCHER("^(\\-|\\+)?\\d+(\\.\\d+)?$", "正数、负数、和小数"),
    NUMBER_TWO_DECIMAL_POSITIVE_MATCHER("^[0-9]+(.[0-9]{2})?$", "有两位小数的正实数"),
    NUMBER_THREE_DECIMAL_POSITIVE_MATCHER("^[0-9]+(.[0-9]{1,3})?$", "有1~3位小数的正实数"),
    NUMBER_POSITIVE_NONZERO_INTEGER_1_MATCHER("^[1-9]\\d*$", "非零的正整数"),
    NUMBER_POSITIVE_NONZERO_INTEGER_2_MATCHER("^([1-9][0-9]*){1,3}$", "非零的正整数"),
    NUMBER_POSITIVE_NONZERO_INTEGER_3_MATCHER("^\\+?[1-9][0-9]*$", "非零的正整数"),
    NUMBER_NEGATIVE_NONZERO_INTEGER_1_MATCHER("^-[1-9]\\d*$", "非零的负整数"),
    NUMBER_NEGATIVE_NONZERO_INTEGER_2_MATCHER("^\\-[1-9][]0-9\"*$", "非零的负整数"),
    NUMBER_NON_NEGATIVE_INTEGER_1_MATCHER("^\\d+$", "非负整数"),
    NUMBER_NON_NEGATIVE_INTEGER_2_MATCHER("^[1-9]\\d*|0$", "非负整数"),
    NUMBER_NON_POSITIVE_INTEGER_1_MATCHER("^-[1-9]\\d*|0$", "非正整数"),
    NUMBER_NON_POSITIVE_INTEGER_2_MATCHER("^((-\\d+)|(0+))$", "非正整数"),

    NUMBER_NON_NEGATIVE_FLOATING_1_MATCHER("^\\d+(\\.\\d+)?$", "非负浮点数"),
    NUMBER_NON_NEGATIVE_FLOATING_2_MATCHER("^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$", "非负浮点数"),
    NUMBER_NON_POSITIVE_FLOATING_1_MATCHER("^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$", "非正浮点数"),
    NUMBER_NON_POSITIVE_FLOATING_2_MATCHER("^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$", "非正浮点数"),
    NUMBER_POSITIVE_FLOATING_1_MATCHER("^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$", "正浮点数"),
    NUMBER_POSITIVE_FLOATING_2_MATCHER("^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$", "正浮点数"),
    NUMBER_NEGATIVE_FLOATING_1_MATCHER("^-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*)$", "负浮点数"),
    NUMBER_NEGATIVE_FLOATING_2_MATCHER("^(-(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))$", "负浮点数"),
    NUMBER_FLOATING_MATCHER("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$", "浮点数"),
    NUMBER_FLOATING_1_MATCHER("^(-?\\d+)(\\.\\d+)?$", "浮点数"),

    /**
     * 校验字符的表达式
     */
    STRING_CHINESE_MATCHER("^[\\u4e00-\\u9fa5]{0,}$", "汉字"),
    STRING_ENG_NUM_MATCHER("^[A-Za-z0-9]+$", "英文和数字"),
    STRING_ENG_NUM_2_MATCHER("^[A-Za-z0-9]{4,40}$", "英文和数字"),
    STRING_LEN_MATCHER("^.{3,20}$", "长度为3-20的所有字符"),
    STRING_ENG_MATCHER("^[A-Za-z]+$", "由26个英文字母组成的字符串"),
    STRING_CAPITAL_ENG_MATCHER("^[A-Z]+$", "由26个大写英文字母组成的字符串"),
    STRING_LOWER_ENG_MATCHER("^[a-z]+$", "由26个小写英文字母组成的字符串"),
    STRING_ENG_NUM__MATCHER("^\\w+$", "由数字、26个英文字母或者下划线组成的字符串"),
    STRING_ENG_NUM__2_MATCHER("^\\w{3,20}$", "由数字、26个英文字母或者下划线组成的字符串"),
    STRING_CN_ENG_NUM___MATCHER("^[\\u4E00-\\u9FA5A-Za-z0-9_]+$", "中文、英文、数字包括下划线"),
    STRING_CN_ENG_NUM_MATCHER("^[\\u4E00-\\u9FA5A-Za-z0-9]+$", "中文、英文、数字但不包括下划线等符号"),
    STRING_CN_ENG_NUM_2_MATCHER("^[\\u4E00-\\u9FA5A-Za-z0-9]{2,20}$", "中文、英文、数字但不包括下划线等符号"),
    STRING_SPECIAL_MATCHER("[^%&',;=?$\\x22]+", "可以输入含有^%&',;=?$\\\"等字符"),
    STRING_NO_SPECIAL_MATCHER("[^~\\x22]+", "禁止输入含有~的字符"),
    STRING_FULL_WIDTH_MATCHER("[^uFF00-uFFFF]", "全角字符"),

    /**
     * 特殊需求表达式
     */
    EMAIL_MATCHER("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", "Email地址"),
    DOMAIN_MATCHER("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(/.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+/.?", "域名"),
    WEB_MATCHER("[a-zA-z]+://[^\\s]*", "InternetURL"),
    HTTP_MATCHER("^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", "InternetURL"),
    PHONE_NUM_MATCHER("^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$", "手机号码"),
    TEL_NUM_MATCHER("^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$", "电话号码"),
    TEL_NUM_CN_MATCHER("\\d{3}-\\d{8}|\\d{4}-\\d{7}", "国内电话号码"),
    ID_NUM_MATCHER("^\\d{15}|\\d{18}$", "身份证号"),
    ID_NUM_SHORT_MATCHER("^([0-9]){7,18}(x|X)?", "短身份证号码"),
    ACCOUNT_NUM_MATCHER("^[a-zA-Z][a-zA-Z0-9_]{4,15}$", "帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)"),
    PASSWORD_MATCHER("^[a-zA-Z]\\w{5,17}$", "密码(以字母开头，长度在6~18之间，只能包含字母、数字和下划线)"),
    PASSWORD_STR_MATCHER("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$", "强密码(必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间)"),
    DATE_MATCHER("^\\d{4}-\\d{1,2}-\\d{1,2}", "日期格式"),
    MOUTH_MATCHER("^(0?[1-9]|1[0-2])$", "一年的12个月(01～09和1～12)"),
    DAY_MATCHER("^((0?[1-9])|((1|2)[0-9])|30|31)$", "一个月的31天(01～09和1～31)"),
    XML_MATCHER("^([a-zA-Z]+-?)+[a-zA-Z0-9]+\\\\.[x|X][m|M][l|L]$", "xml文件"),
    SPACE_MATCHER("\\n\\s*\\r", "空白行的正则表达式(可以用来删除空白行)"),
    HTML_MATCHER("<(\\S*?)[^>]*>.*?</\\1>|<.*? />", "HTML标记的正则表达式"),

    //可以用来删除行首行尾的空白字符(包括空格、制表符、换页符等等)
    SPACE_HEAD_TAIL_MATCHER("(^\\s*)|(\\s*$)", "首尾空白字符的正则表达式"),
    SPACE_HEAD_TAIL_2_MATCHER("^\\s*|\\s*$", "首尾空白字符的正则表达式"),

    //提取IP地址时有用
    IP_MATCHER("\\d+\\.\\d+\\.\\d+\\.\\d+", "IP地址"),
    IP_1_MATCHER("((?:(?:25[0-5]|2[0-4]\\\\d|[01]?\\\\d?\\\\d)\\\\.){3}(?:25[0-5]|2[0-4]\\\\d|[01]?\\\\d?\\\\d))", "IP地址"),

    //中国邮政编码为6位数字
    MAIL_MATCHER("[1-9]\\d{5}(?!\\d)", "中国邮政编码"),
    QQ_MATCHER("[1-9][0-9]{4,14}", "QQ号（0不能开头）");

    String regex;
    String message;

    Regexs(String regex, String message) {
        this.regex = regex;
        this.message = message;
    }

    public String getRegex() {
        return regex;
    }

    public String getMessage() {
        return message;
    }
}
