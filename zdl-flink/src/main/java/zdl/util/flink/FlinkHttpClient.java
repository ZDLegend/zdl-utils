package zdl.util.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FlinkHttpClient {
    private static final Logger log = LoggerFactory.getLogger(FlinkHttpClient.class);

    private WebClient client;

    private String url;

    public FlinkHttpClient init(String url) {
        this.url = url;
        client = WebClient.builder()
                .baseUrl(url)
                .filter(logRequest())
                .filter(logResponse())
                .build();
        return this;
    }

    /**
     * 获取已上传flink文件列表
     *
     * @return Returns a list of all jars previously uploaded via '/jars/upload'.
     */
    public JSONArray getJarsNameStr() {
        JSONObject json = client.get()
                .uri(uriBuilder -> uriBuilder.path("jars").build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        if (json != null && json.containsKey("files")) {
            return JSONArray.parseArray(json.get("files").toString());
        } else {
            return new JSONArray();
        }
    }

    /**
     * 获取已上传flink文件列表
     *
     * @return <jar_id, 文件名>
     */
    public JSONObject getJarsName() {
        JSONObject jarsList = new JSONObject();
        getJarsNameStr()
                .stream()
                .map(o -> (JSONObject) o)
                .forEach(j -> {
                    if (j.containsKey("name") && j.containsKey("id")) {
                        jarsList.put(j.get("id").toString(), j.get("name").toString());
                    }
                });
        return jarsList;
    }

    /**
     * 获取jar包的EntryClass
     *
     * @return entryClass
     */
    public String getEntryClass(String jarID) {
        return getJarsNameStr()
                .stream()
                .map(o -> (JSONObject) o)
                .filter(value -> value.containsKey("id") && value.containsKey("entry") && value.get("id").toString().equals(jarID))
                .map(j -> j.getJSONArray("entry"))
                .filter(arrayEntry -> arrayEntry != null && arrayEntry.size() > 0)
                .findAny()
                .orElse(new JSONArray())
                .getJSONObject(0)
                .getString("name");
    }

    /**
     * 上传jar包
     *
     * @param jarPath 文件路径
     * @param jarName 文件名
     * @return flink的jar包ID
     */
    public String flinkJarUpload(String jarPath, String jarName) {

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("jarfile", new FileSystemResource(jarPath));

        log.info("request url: " + url + "/jars/upload");
        String jar_id = null;

        try {
            File jarFile = new File(jarPath + File.separator + jarName);
            if (jarFile.exists()) {
                JSONObject json = client.post()
                        .uri(uriBuilder -> uriBuilder.path("/jars/upload").build())
                        .bodyValue(bodyBuilder.build())
                        .retrieve()
                        .bodyToMono(JSONObject.class)
                        .block();
                assert json != null;
                if (json.containsKey("status") && json.get("status").toString().equals("success")) {
                    jar_id = json.get("filename").toString();
                    if (jar_id.contains("/")) {
                        jar_id = jar_id.substring(jar_id.lastIndexOf("/") + 1);
                    }
                } else {
                    throw new RuntimeException("上传jar包失败！");
                }
            }
        } catch (Exception e) {
            log.error("上传jar包失败：" + e.getMessage());
            throw new RuntimeException("上传jar包失败：" + e.getMessage());
        }
        return jar_id;
    }

    /**
     * 返回所有job的信息
     *
     * @return Returns an overview over all jobs.
     */
    public Map<String, JSONObject> getAllJobs() {
        log.info("request url: " + url + "jobs/overview");
        try {
            JSONObject value = client.get()
                    .uri(uriBuilder -> uriBuilder.path("jobs/overview").build())
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
            assert value != null;
            if (value.containsKey("jobs")) {
                JSONArray array = value.getJSONArray("jobs");
                if (array != null) {
                    return array.stream()
                            .map(o -> (JSONObject) o)
                            .filter(j -> j.containsKey("jid"))
                            .collect(Collectors.toMap(j -> j.getString("jid"), j -> j));
                }
            }
        } catch (Exception e) {
            log.error("返回所有job信息报错：" + e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * 获取所有运行job
     *
     * @return Returns an overview over running jobs.
     */
    public Map<String, Map<String, Object>> getRunningJobs() {
        log.info("request url: " + url + "jobs/overview");
        Map<String, JSONObject> value = getAllJobs();
        return value.values().stream()
                .filter(m -> m.get("state").equals("RUNNING"))
                .collect(Collectors.toMap(j -> j.getString("jid"), j -> j));
    }

    /**
     * 返回单个job的信息
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     * @return details of a job.
     */
    public JSONObject getJobMessage(String jobId) {
        log.info("request url: " + url + "jobs/" + jobId);
        try {
            return client.get()
                    .uri(uriBuilder -> uriBuilder.path("jobs/" + jobId).build())
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
        } catch (Exception e) {
            log.error("返回止job[" + jobId + "]信息保存：" + e.getMessage());
        }
        return new JSONObject();
    }

    /**
     * 取消job
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    public boolean cancelJob(String jobId) {
        log.info("request url: " + url + "jobs/" + jobId + "/yarn-cancel");

        try {
            String result = client.get()
                    .uri(uriBuilder -> uriBuilder.path("jobs/" + jobId + "/yarn-cancel").build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("停止job[" + jobId + "]结果：" + result);
            return true;
        } catch (Exception e) {
            log.error("取消作业[" + jobId + "]报错：" + e.getMessage());
            return false;
        }
    }

    /**
     * 运行job 版本V2
     *
     * @param jarID :  String value that identifies a jar.
     *              When uploading the jar a path is returned,
     *              where the filename is the ID.
     *              This value is equivalent to the `id` field in the list of uploaded jars (/jars).
     * @return jobID    : 作业ID
     */
    public String flinkJarRun(String jarID) {
        log.info("request url: " + "/jars/" + jarID + "/run");

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        String jobID = null;

        //Boolean value that specifies whether the job submission should be rejected
        // if the savepoint contains state that cannot be mapped back to the job.
        bodyBuilder.part("allowNonRestoredState", false);
        //Positive integer value that specifies the desired parallelism for the job.
        bodyBuilder.part("parallelism", 1);
        // String value that specifies the path of the savepoint to restore the job from.
        bodyBuilder.part("savepointPath", "/home/flink/save");
        //Deprecated, please use 'programArg' instead.
        // String value that specifies the arguments for the program or plan
        bodyBuilder.part("programArg", null);
        //String value that specifies the fully qualified name of the entry point class.
        // Overrides the class defined in the jar file manifest.
        bodyBuilder.part("entry-class", getEntryClass(jarID));

        try {
            log.info("启动flink job作业：jarId = {}", jarID);
            JSONObject json = client.post()
                    .uri(uriBuilder -> uriBuilder.path("/jars/" + jarID + "/run").build())
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
            log.info("运行job结果：" + json);
            assert json != null;
            if (json.containsKey("jobid")) {
                jobID = json.get("jobid").toString();
            } else {
                throw new Exception("未获取到作业ID，运行结果：" + json);
            }
        } catch (Exception e) {
            log.error("运行jar[" + jarID + "]报错：" + e.getMessage());
        }
        return jobID;
    }

    /**
     * 解析array 变成list
     *
     * @param jsonArray JSONArray in
     * @param arrayList ArrayList out
     */
    public static void addList(JSONArray jsonArray, ArrayList<String> arrayList) {
        if (jsonArray != null) {
            for (int d = 0; d < jsonArray.size(); d++) {
                String value = jsonArray.getString(d);
                if (value != null && value.trim().length() > 0) {
                    arrayList.add(value.trim());
                }
            }
        }
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(Mono::just);
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.headers().contentType().isEmpty()) {
                return Mono.just(ClientResponse.from(clientResponse)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(clientResponse.body(BodyExtractors.toDataBuffers()))
                        .build());
            }
            return Mono.just(clientResponse);
        });
    }
}
