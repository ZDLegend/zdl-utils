package zdl.util.azkaban;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Azkaban HttpApi
 *
 * @author ZDLegend
 */
public interface AzkabanApi {

    /**
     * Azkaban登录接口，返回sessionId
     */
    String login(String userName, String password) throws Exception;

    /**
     * Azkaban创建project
     *
     * @param projectName project名称
     * @param description project描述
     */
    void createProject(String projectName, String description);

    /**
     * Azkaban删除project
     *
     * @param projectName project名称
     */
    void deleteProject(String projectName);

    /**
     * Azkaban上传zip文件
     *
     * @param projectName project名称
     * @param file        上传文件
     * @return projectId
     */
    String uploadZip(String projectName, File file);

    /**
     * 根据时间 创建调度任务
     *
     * @param projectId
     * @param projectName
     * @param flow
     * @param flowName
     * @param recurring   是否循环，on循环
     * @param period      循环频率： M Months，w Weeks，d Days，h Hours，m Minutes，s Seconds；如60s，支持分钟的倍数
     * @param date        开始时间
     * @return 返回scheduleId
     */
    String scheduleEXEaFlow(String projectId, String projectName, String flow,
                            String flowName, String recurring, String period, Date date);

    /**
     * 根据cron表达式 创建调度任务
     *
     * @param projectName
     * @param cron
     * @param flow
     * @param flowName
     * @return 返回scheduleId
     */
    String scheduleByCronEXEaFlow(String projectName, String cron, String flow, String flowName);

    /**
     * 根据scheduleId取消一个流的调度
     *
     * @param scheduleId
     */
    void cancelScheduleFlow(String scheduleId);

    /**
     * 下载Azkaban压缩文件
     *
     * @param projectName
     * @param zipPath
     */
    void downLoadZip(String projectName, String zipPath);

    /**
     * 执行project
     *
     * @param projectName project名
     * @return
     */
    List<String> executeProject(String projectName);

    String executeFlow(String projectName, String flow);

    String cancelFlow(String execId);

    Map<String, Object> getFlowExecution(int execId);

    String fetchSchedule(int projectId, String flowId);

    Map<String, Object> getProject(String projectName);
}
