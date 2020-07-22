package zdl.util.azkaban;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Date;

/**
 * Azkaban AJAX API
 * <p>Often there's a desire to interact with Azkaban without having to use the web UI.
 * Azkaban has some exposed ajax calls accessible through curl or some other HTTP request clients.
 * All API calls require a proper authentication first.
 * <p>AJAX API official documents: {@code https://azkaban.github.io/azkaban/docs/latest/#ajax-api}
 * <p>Azkaban核心概念：
 * <li>Project：一个project包含一个工作流；一个project对应多条flow(一条的分支一条flow)
 * <li>Flow：具体工作流，一条flow由一个或多个job组成
 * <li>Job：一个工作流中的具体节点，可以是各种类型的执行任务
 * <li>Execution: 一条Flow执行一次生成一个execution
 *
 * @author ZDLegend
 */
public interface AzkabanApi {

    /**
     * Authenticate
     * <p>This API helps authenticate a user and provides a session.id in response.
     * <p>Once a session.id has been returned, until the session expires,
     * this id can be used to do any API requests with a proper permission granted.
     * A session expires if you log out, change machines, browsers or locations,
     * if Azkaban is restarted, or if the session expires.
     * The default session timeout is 24 hours (one day).
     * You can re-login whether the session has expired or not.
     * For the same user, a new session will always override old one.
     * <p>{@code Importantly}, session.id should be provided for almost all API calls (other than authentication).
     * session.id can be simply appended as one of the request parameters,
     * or set via the cookie: azkaban.browser.session.id.
     * <li>Method: POST
     * <li>Request URL: /?action=login
     * <li>Parameter Location: Request Query String
     *
     * @param userName The Azkaban username.
     * @param password The corresponding password.
     * @return Return a session id if the login attempt succeeds,else return null.
     */
    String login(String userName, String password);

    /**
     * Create a Project
     * <p>The ajax API for creating a new project.
     * <li>Method: POST
     * <li>Request URL: /manager?action=create
     * <li>Parameter Location: Request Query
     *
     * @param projectName project名称
     * @param description project描述
     * @apiNote before uploading any project zip files, the project should be created first via this API.
     */
    void createProject(String projectName, String description);

    /**
     * Delete a Project
     * <p>The ajax API for deleting an existing project.
     * <li>Method: GET
     * <li>Request URL: /manager?delete=true
     * <li>Parameter Location: Request Query
     *
     * @param projectName project名称
     * @apiNote before uploading any project zip files, the project should be created first via this API.
     */
    void deleteProject(String projectName);

    /**
     * Upload a Project Zip
     * <p>The ajax call to upload a project zip file. The zip file structure should follows
     * the requirements described in Upload Projects.
     * <li>Method: POST
     * <li>Content-Type: multipart/mixed
     * <li>Request URL: /manager?ajax=upload
     * <li>Parameter Location: Request Body
     *
     * @param projectName The project name to be uploaded.
     * @param file        The project zip file. The type should be set as application/zip or application/x-zip-compressed.
     * @return projectId
     * @apiNote This API should be called after a project is successfully created.
     */
    JSONObject uploadZip(String projectName, File file);

    /**
     * Schedule a period-based Flow (Deprecated)
     * <p>This API call schedules a period-based flow.
     * <li>Method: POST
     * <li>Request URL: /schedule?ajax=scheduleFlow
     * <li>Parameter Location: Request Query String
     *
     * @param projectId   The id of the project. You can find this with {@link #fetchProjectFlows}.
     * @param projectName The name of the project.
     * @param flowName    The name of the flow.
     * @param recurring   Flags the schedule as a recurring schedule. on循环(optional)
     * @param period      Specifies the recursion period. Depends on the "is_recurring" flag being set. Example: 5w
     *                    循环频率： M Months，w Weeks，d Days，h Hours，m Minutes，s Seconds；如60s，支持分钟的倍数
     *                    (optional)
     * @param date        时间设置
     * @return 返回scheduleId
     */
    @Deprecated
    String schedulePeriodBasedFlow(String projectId, String projectName,
                                   String flowName, String recurring, String period, Date date);

    /**
     * Flexible scheduling using Cron
     * <p>This API call schedules a flow by a cron Expression.
     * Cron is a UNIX tool that has been widely used for a long time,
     * and we use Quartz library to parse cron Expression.
     * All cron schedules follow the timezone defined in azkaban web server
     * (the timezone ID is obtained by {@link java.util.TimeZone#getDefault()#getID()}).
     * <li>Method: POST
     * <li>Request URL: /schedule?ajax=scheduleCronFlow
     * <li>Parameter Location: Request Query String
     *
     * @param projectName The name of the project.
     * @param cron        A CRON expression is a string comprising 6 or 7 fields separated
     *                    by white space that represents a set of times.
     *                    In azkaban, we use Quartz Cron Format.
     * @param flow        The name of the flow.
     * @return return a scheduleId if succeeds
     */
    String scheduleFlowByCron(String projectName, String cron, String flow);

    /**
     * Unschedule a Flow
     * <p>This API call unschedules a flow.
     * <li>Method: POST
     * <li>Request URL: /schedule?action=removeSched
     * <li>Parameter Location: Request Query String
     *
     * @param scheduleId The id of the schedule. You can find this in the Azkaban UI on the /schedule page.
     */
    void unScheduleFlow(String scheduleId);

    /**
     * 下载Azkaban压缩文件
     *
     * @param projectName The project name
     * @param zipPath     下载到本地路径
     */
    void downLoadZip(String projectName, String zipPath);

    /**
     * Execute a Flow
     * <p>This API executes a flow via an ajax call, supporting a rich selection of different options.
     * Running an individual job can also be achieved via this API by disabling all other jobs in the same flow.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=executeFlow
     * <li>Parameter Location: Request Query String
     *
     * @param projectName The project name of the executing flow.
     * @param flow        The flow id to be executed.
     * @return A response sample
     * <pre>{@code
     * {
     *   message: "Execution submitted successfully with exec id 295",
     *   project: "foo-demo",
     *   flow: "test",
     *   execid: 295
     * }
     * }</pre>
     */
    Mono<JSONObject> executeFlow(String projectName, String flow);

    /**
     * Cancel a Flow Execution
     * <p>Given an execution id, this API call cancels a running flow.
     * If the flow is not running, it will return an error message.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=cancelFlow
     * <li>Parameter Location: Request Query String
     * <p>A response sample if succeeds:
     * <pre>
     * {@code
     * { }
     * }
     * </pre>
     * A response sample if succeeds:
     * <pre>
     * {@code
     * {
     *   "error" : "Execution 302 of flow test isn't running."
     * }
     * }
     *  </pre>
     *
     * @param execId The execution id.
     */
    void cancelFlow(String execId);

    /**
     * Pause a Flow Execution
     * <p>Given an execution id, this API pauses a running flow.
     * If an execution has already been paused, it will not return any error;
     * if an execution is not running, it will return an error message.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=pauseFlow
     * <li>Parameter Location: Request Query String
     *
     * @param execId The execution id.
     */
    void pauseFlow(String execId);

    /**
     * Resume a Flow Execution
     * <p>Given an execution id, this API resumes a paused running flow.
     * If an execution has already been resumed, it will not return any errors;
     * if an execution is not runnning, it will return an error message.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=resumeFlow
     * <li>Parameter Location: Request Query String
     *
     * @param execId The execution id.
     */
    void resumeFlow(String execId);

    /**
     * Fetch Flows of a Project(查询一个project下的所有flows)
     * <p>Given a project name, this API call fetches all flow ids of that project.
     * <li>Method: GET
     * <li>Request URL: /manager?ajax=fetchprojectflows
     * <li>Parameter Location: Request Query String
     *
     * @param projectName The project name to be fetched.
     * @return A response sample
     * <pre>{@code
     * {
     *   "project" : "test-azkaban",
     *   "projectId" : 192,
     *   "flows" : [ {
     *     "flowId" : "test"
     *   }, {
     *     "flowId" : "test2"
     *   } ]
     * }
     * }</pre>
     */
    JSONObject fetchProjectFlows(String projectName);

    /**
     * Fetch Jobs of a Flow
     * <p>For a given project and a flow id, this API call fetches all the jobs that belong to this flow.
     * It also returns the corresponding graph structure of those jobs.
     * <li>Method: GET
     * <li>Request URL: /manager?ajax=fetchflowgraph
     * <li>Parameter Location: Request Query String
     *
     * @param projectName The project name to be fetched.
     * @param flowId      The project id to be fetched.
     * @return A response sample
     * <pre>{@code
     * {
     *   "project" : "azkaban-test-project",
     *   "nodes" : [ {
     *     "id" : "test-final",
     *     "type" : "command",
     *     "in" : [ "test-job-3" ]
     *   }, {
     *     "id" : "test-job-start",
     *     "type" : "java"
     *   }, {
     *     "id" : "test-job-3",
     *     "type" : "java",
     *     "in" : [ "test-job-2" ]
     *   }, {
     *     "id" : "test-job-2",
     *     "type" : "java",
     *     "in" : [ "test-job-start" ]
     *   } ],
     *   "flow" : "test",
     *   "projectId" : 192
     * }
     * }</pre>
     */
    JSONObject fetchJobsOfFlow(String projectName, String flowId);

    /**
     * Fetch a Flow Execution(根据execId查询一条执行中的Flow信息)
     * <p>Given an execution id, this API call fetches all the detailed information of that execution,
     * including a list of all the job executions.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=fetchexecflow
     * <li>Parameter Location: Request Query String
     *
     * @param execId The execution id to be fetched.
     * @return It returns a detailed information about the execution (check the example below).
     * One thing to notice is that the field nodes[i].in actually indicates what are the dependencies of this node.
     * <p>A response sample
     * <pre>
     * {@code
     * {
     *   "attempt" : 0,
     *   "submitUser" : "1",
     *   "updateTime" : 1407779495095,
     *   "status" : "FAILED",
     *   "submitTime" : 1407779473318,
     *   "projectId" : 192,
     *   "flow" : "test",
     *   "endTime" : 1407779495093,
     *   "type" : null,
     *   "nestedId" : "test",
     *   "startTime" : 1407779473354,
     *   "id" : "test",
     *   "project" : "test-azkaban",
     *   "nodes" : [ {
     *     "attempt" : 0,
     *     "startTime" : 1407779495077,
     *     "id" : "test",
     *     "updateTime" : 1407779495077,
     *     "status" : "CANCELLED",
     *     "nestedId" : "test",
     *     "type" : "command",
     *     "endTime" : 1407779495077,
     *     "in" : [ "test-foo" ]
     *   }, {
     *     "attempt" : 0,
     *     "startTime" : 1407779473357,
     *     "id" : "test-bar",
     *     "updateTime" : 1407779484241,
     *     "status" : "SUCCEEDED",
     *     "nestedId" : "test-bar",
     *     "type" : "pig",
     *     "endTime" : 1407779484236
     *   }, {
     *     "attempt" : 0,
     *     "startTime" : 1407779484240,
     *     "id" : "test-foobar",
     *     "updateTime" : 1407779495073,
     *     "status" : "FAILED",
     *     "nestedId" : "test-foobar",
     *     "type" : "java",
     *     "endTime" : 1407779495068,
     *     "in" : [ "test-bar" ]
     *   }, {
     *     "attempt" : 0,
     *     "startTime" : 1407779495069,
     *     "id" : "test-foo",
     *     "updateTime" : 1407779495069,
     *     "status" : "CANCELLED",
     *     "nestedId" : "test-foo",
     *     "type" : "java",
     *     "endTime" : 1407779495069,
     *     "in" : [ "test-foobar" ]
     *   } ],
     *   "flowId" : "test",
     *   "execid" : 304
     * }
     * }
     * </pre>
     */
    JSONObject fetchFlowExecution(int execId);

    /**
     * Fetch Executions of a Flow(分页查询一条flow中所有的Executions)
     * <p>Given a project name, and a certain flow,
     * this API call provides a list of corresponding executions.
     * Those executions are sorted in descendent submit time order.
     * Also parameters are expected to specify the start index and the length of the list.
     * This is originally used to handle pagination.
     * <li>Method: GET
     * <li>Request URL: /manager?ajax=fetchFlowExecutions
     * <li>Parameter Location: Request Query String
     *
     * @param projectName The project name to be fetched.
     * @param flowId      The flow id to be fetched.
     * @param start       The start index(inclusive) of the returned list.
     * @param length      The max length of the returned list.
     *                    For example, if the start index is 2,
     *                    and the length is 10,
     *                    then the returned list will include executions of indices:
     *                    [2, 3, 4, 5, 6, 7, 8, 9, 10, 11].
     * @return An example success response
     * <pre>
     * {@code
     * {
     *   "executions" : [ {
     *     "startTime" : 1407779928865,
     *     "submitUser" : "1",
     *     "status" : "FAILED",
     *     "submitTime" : 1407779928829,
     *     "execId" : 306,
     *     "projectId" : 192,
     *     "endTime" : 1407779950602,
     *     "flowId" : "test"
     *   }, {
     *     "startTime" : 1407779877807,
     *     "submitUser" : "1",
     *     "status" : "FAILED",
     *     "submitTime" : 1407779877779,
     *     "execId" : 305,
     *     "projectId" : 192,
     *     "endTime" : 1407779899599,
     *     "flowId" : "test"
     *   }, {
     *     "startTime" : 1407779473354,
     *     "submitUser" : "1",
     *     "status" : "FAILED",
     *     "submitTime" : 1407779473318,
     *     "execId" : 304,
     *     "projectId" : 192,
     *     "endTime" : 1407779495093,
     *     "flowId" : "test"
     *   } ],
     *   "total" : 16,
     *   "project" : "azkaban-test-project",
     *   "length" : 3,
     *   "from" : 0,
     *   "flow" : "test",
     *   "projectId" : 192
     * }
     * }
     * </pre>
     */
    JSONObject fetchFlowExecutions(String projectName, String flowId, int start, int length);

    /**
     * Fetch a Schedule
     * <p>Given a project id and a flow id, this API call fetches the schedule.
     * <li>Method: GET
     * <li>Request URL: /schedule?ajax=fetchSchedule
     * <li>Parameter Location: Request Query String
     *
     * @param projectId The id of the project.
     * @param flowId    The name of the flow.
     * @return An example success response
     * <pre>
     * {@code
     * {
     *   "schedule" : {
     *     "cronExpression" : "0 * 9 ? * *",
     *     "nextExecTime" : "2017-04-01 09:00:00",
     *     "period" : "null",
     *     "submitUser" : "azkaban",
     *     "executionOptions" : {
     *       "notifyOnFirstFailure" : false,
     *       "notifyOnLastFailure" : false,
     *       "failureEmails" : [ ],
     *       "successEmails" : [ ],
     *       "pipelineLevel" : null,
     *       "queueLevel" : 0,
     *       "concurrentOption" : "skip",
     *       "mailCreator" : "default",
     *       "memoryCheck" : true,
     *       "flowParameters" : {
     *       },
     *       "failureAction" : "FINISH_CURRENTLY_RUNNING",
     *       "failureEmailsOverridden" : false,
     *       "successEmailsOverridden" : false,
     *       "pipelineExecutionId" : null,
     *       "disabledJobs" : [ ]
     *     },
     *     "scheduleId" : "3",
     *     "firstSchedTime" : "2017-03-31 11:45:21"
     *   }
     * }
     * }
     * </pre>
     */
    JSONObject fetchSchedule(int projectId, String flowId);

    /**
     * Fetch Running Executions of a Flow
     * <p>Given a project name and a flow id, this API call fetches only executions that are currently running.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=getRunning
     * <li>Parameter Location: Request Query String
     *
     * @param projectName The project name to be fetched.
     * @param flowId      The flow id to be fetched.
     * @return A list of execution ids fetched.Example values: [301, 302, 111, 999]
     */
    JSONArray fetchRunningExecOfFlow(String projectName, String flowId);

    /**
     * Fetch Execution Job Logs(分页)
     * <p>Given an execution id and a job id, this API call fetches the correponding job logs.
     * The log text can be quite large sometimes,
     * so this API call also expects the parameters offset and length to be specified.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=fetchExecJobLogs
     * <li>Parameter Location: Request Query String
     *
     * @param execId The unique id for an execution.
     * @param jobId  The unique id for the job to be fetched.
     * @param offset The offset for the log data.
     * @param length The length of the log data. For example,
     *               if the offset set is 10 and the length is 1000,
     *               the returned log will starts from the 10th character and has a length of 1000
     *               (less if the remaining log is less than 1000 long).
     * @return The text data of the logs.
     */
    JSONObject fetchExecJobLogs(String execId, String jobId, int offset, int length);

    /**
     * Fetch Flow Execution Updates
     * <p>This API call fetches the updated information for an execution.
     * It filters by lastUpdateTime which only returns job information updated afterwards.
     * <li>Method: GET
     * <li>Request URL: /executor?ajax=fetchexecflowupdate
     * <li>Parameter Location: Request Query String
     *
     * @param execId         The execution id.
     * @param lastUpdateTime The criteria to filter by last update time.
     *                       Set the value to be -1 if all job information are needed.
     * @return A response sample
     * <pre>{@code
     * {
     *   "id" : "test",
     *   "startTime" : 1407778382894,
     *   "attempt" : 0,
     *   "status" : "FAILED",
     *   "updateTime" : 1407778404708,
     *   "nodes" : [ {
     *     "attempt" : 0,
     *     "startTime" : 1407778404683,
     *     "id" : "test",
     *     "updateTime" : 1407778404683,
     *     "status" : "CANCELLED",
     *     "endTime" : 1407778404683
     *   }, {
     *     "attempt" : 0,
     *     "startTime" : 1407778382913,
     *     "id" : "test-job-1",
     *     "updateTime" : 1407778393850,
     *     "status" : "SUCCEEDED",
     *     "endTime" : 1407778393845
     *   }, {
     *     "attempt" : 0,
     *     "startTime" : 1407778393849,
     *     "id" : "test-job-2",
     *     "updateTime" : 1407778404679,
     *     "status" : "FAILED",
     *     "endTime" : 1407778404675
     *   }, {
     *     "attempt" : 0,
     *     "startTime" : 1407778404675,
     *     "id" : "test-job-3",
     *     "updateTime" : 1407778404675,
     *     "status" : "CANCELLED",
     *     "endTime" : 1407778404675
     *   } ],
     *   "flow" : "test",
     *   "endTime" : 1407778404705
     * }
     * }</pre>
     */
    JSONObject fetchExecFlowUpdate(String execId, long lastUpdateTime);
}
