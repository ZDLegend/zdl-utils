package zdl.util.flink.demo;

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

public class FlinkHttpClient {
    private static final Logger log = LoggerFactory.getLogger(FlinkHttpClient.class);

    private WebClient client;

    private String url;

    public void init() {
        client = WebClient.builder()
                .baseUrl(url)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * 获取已上传flink文件列表
     *
     * @param url
     * @return <jar_id, 文件名>
     */
    public JSONObject getJarsName(String url) {
        JSONObject jarsList = new JSONObject();
        String result = getJarsNameStr(url);
        JSONObject value = JSONObject.parseObject(result);
        if (value.containsKey("files")) {
            JSONArray array = JSONArray.parseArray(value.get("files").toString());
            if (array != null) {
                //循环参数数组
                for (Object o : array) {
                    value = JSONObject.parseObject(o.toString());
                    if (value.containsKey("name") && value.containsKey("id")) {
                        jarsList.put(value.get("id").toString(), value.get("name").toString());
                    }
                }
            }
        }

        return jarsList;
    }

    /**
     * 获取上传jar包的详细信息
     *
     * @param url
     * @return
     */
    public JSONArray getJarsInfo(String url) {
        String result = getJarsNameStr(url);
        JSONArray arrayList = new JSONArray();
        if (result != null) {
            JSONObject value = JSONObject.parseObject(result);
            if (value.containsKey("files")) {
                return JSONArray.parseArray(value.get("files").toString());
            }
        }
        return arrayList;
    }

    /**
     * 获取已上传flink文件列表
     *
     * @param url
     * @return <jar_id, 文件名>
     */
    public String getJarsNameStr(String url) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("jars").build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

    /**
     * 获取jar包的entyClass
     *
     * @param jarID
     * @return entryClass
     */
    public String getEntryClass(String jarID) {
        String result = client.get()
                .uri(uriBuilder -> uriBuilder.path("jars").build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String entyClass = null;

        try {
            JSONObject value = JSONObject.parseObject(result);
            if (value.containsKey("files")) {
                JSONArray array = JSONArray.parseArray(value.get("files").toString());
                if (array != null) {
                    //循环参数数组
                    for (Object o : array) {
                        value = JSONObject.parseObject(o.toString());
                        if (value.containsKey("id") && value.containsKey("entry") && value.get("id").toString().equals(jarID)) {
                            JSONArray arrayEntry = JSONObject.parseArray(value.get("entry").toString());
                            if (arrayEntry != null && arrayEntry.size() > 0) {
                                JSONObject childEntry = JSONObject.parseObject(arrayEntry.get(0).toString());
                                entyClass = childEntry.getString("name");
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取flink的entry class失败：" + e.getMessage());
        }
        return entyClass;
    }

    /**
     * 上传jar包
     *
     * @param jarPath
     * @param jarName
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
                    throw new Exception("上传jar包失败！");
                }
            }
        } catch (Exception e) {
            log.error("上传jar包失败：" + e.getMessage());
        }
        return jar_id;
    }

    /**
     * 返回所有job的信息
     *
     * @param url
     * @return
     */
    public Map<String, Map<String, Object>> getAllJobs(String url) {
        log.info("request url: " + url + "jobs/overview");
        Map<String, Map<String, Object>> jobInfo = new HashMap<>();

        try {
            JSONObject value = client.get()
                    .uri(uriBuilder -> uriBuilder.path("jobs/overview").build())
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
            assert value != null;
            if (value.containsKey("jobs")) {
                JSONArray array = JSONArray.parseArray(value.get("jobs").toString());
                if (array != null) {
                    //循环参数数组
                    for (Object o : array) {
                        value = JSONObject.parseObject(o.toString());
                        if (value.containsKey("jid")) {
                            jobInfo.put(value.get("jid").toString(), value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("返回所有job信息报错：" + e.getMessage());
        }
        return jobInfo;
    }

    /**
     * 获取所有运行job
     *
     * @param url
     * @return
     */
    public Map<String, Map<String, Object>> getRunningJobs(String url) {
        log.info("request url: " + url + "jobs/overview");
        Map<String, Map<String, Object>> jobInfo = new HashMap<>();
        String result;

        try {
            JSONObject value = client.get()
                    .uri(uriBuilder -> uriBuilder.path("jobs/overview").build())
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
            assert value != null;
            if (value.containsKey("jobs")) {
                JSONArray array = JSONArray.parseArray(value.get("jobs").toString());
                if (array != null) {
                    //循环参数数组
                    for (Object o : array) {
                        value = JSONObject.parseObject(o.toString());
                        if (value.containsKey("jid") && value.containsKey("state") && value.get("state").equals("RUNNING")) {
                            jobInfo.put(value.get("jid").toString(), value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取运行job信息报错：" + e.getMessage());
        }
        return jobInfo;
    }

    /**
     * 返回单个job的信息
     *
     * @param jobId
     * @return
     */
    public String getJobMessage(String jobId) {

        log.info("request url: " + url + "jobs/" + jobId);
        String result = "";
        try {
            result = client.get()
                    .uri(uriBuilder -> uriBuilder.path("jobs/" + jobId).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("返回止job[" + jobId + "]信息保存：" + e.getMessage());
        }
        return result;
    }

    /**
     * 取消job
     *
     * @param jobId
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
     * @param jarID : jar包ID
     * @return jobID    : 作业ID
     */
    public String flinkJarRun(String jarID) {
        log.info("request url: " + "/jars/" + jarID + "/run");

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        String jobID = null;

//        lastParams.part("allowNonRestoredState", null);
//        lastParams.part("parallelism", null);
//        lastParams.part("savepointPath", null);
//        lastParams.part("programArgs", null);
        bodyBuilder.part("entryClass", getEntryClass(jarID));

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
