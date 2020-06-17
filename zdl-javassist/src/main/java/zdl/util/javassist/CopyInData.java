package zdl.util.javassist;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created by ZDLegend on 2020/06/17 10:30
 */
public interface CopyInData {

    String generateCopyString();

    default String transferNullOrEmptyData(Object value) {
        //使用java14 instanceof自动转换新特性
        if(value instanceof String s && StringUtils.isBlank(s)) {
            return getNullChar();
        }

        if(Objects.isNull(value)) {
            return getNullChar();
        }

        return value.toString();
    }

    default char getFieldDelimiter() {
        return '\033';
    }

    default char getLineDelimiter() {
        return '\036';
    }

    default String getNullChar() {
        return "\\N";
    }
}
