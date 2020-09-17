package zdl.util.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

/**
 * 异常相关处理
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/17/ 11:23
 */
public class ExceptionHandler {

    /**
     * 提取异常中的caused by
     *
     * @param e 被提取异常
     * @return 所有caused by信息列表
     */
    private List<String> causeObtain(Exception e) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos)) {
            e.printStackTrace(ps);
            String expetion = baos.toString();
            return StringUtils.getSubUtil(expetion, "Caused by:(.*).");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
