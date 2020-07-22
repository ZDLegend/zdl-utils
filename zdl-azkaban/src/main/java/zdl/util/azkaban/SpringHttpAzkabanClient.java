package zdl.util.azkaban;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 使用Spring Web Client 调用 Azkaban rest api
 *
 * @author ZDLegend
 * @create 2020/07/17
 */
public class SpringHttpAzkabanClient implements AzkabanApi {

    private WebClient client;

    private String sessionId;

    private String url;

    public void initClient(String userName, String password, String url) {
        client = WebClient.builder()
                .baseUrl(url)
                .filter(logRequest())
                .filter(logResponse())
                .build();
        sessionId = login(userName, password);
        this.url = url;
    }

    @Override
    public String login(String userName, String password) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("username", userName);
        bodyBuilder.part("password", password);
        JSONObject response = client.post()
                .uri("?action=login")
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        responseErrorHandle(response, System.out::println);

        if (response != null && response.containsKey("session.id")) {
            return (String) response.get("session.id");
        } else {
            return null;
        }
    }

    @Override
    public void createProject(String projectName, String description) {
        JSONObject response = client.post()
                .uri(uriBuilder -> uriBuilder.path("/manager")
                        .queryParam("action", "create")
                        .queryParam("session.id", sessionId)
                        .queryParam("name", projectName)
                        .queryParam("description", description)
                        .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        responseErrorHandle(response, System.out::println);
    }

    @Override
    public void deleteProject(String projectName) {
        client.get()
                .uri(uriBuilder -> uriBuilder.path("/manager")
                        .queryParam("session.id", sessionId)
                        .queryParam("delete", "true")
                        .queryParam("project", projectName)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public JSONObject uploadZip(String projectName, File file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("session.id", sessionId);
        bodyBuilder.part("project", projectName);
        bodyBuilder.part("ajax", "upload");
        bodyBuilder.part("file", new FileSystemResource(file));
        JSONObject response = client.post()
                .uri(uriBuilder -> uriBuilder.path("/manager")
                        .build())
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        responseErrorHandle(response, System.out::println);
        return response;
    }

    @Override
    public String schedulePeriodBasedFlow(String projectId, String projectName, String flowName,
                                          String recurring, String period, Date date) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("session.id", sessionId);
        bodyBuilder.part("ajax", "scheduleFlow");
        bodyBuilder.part("projectName", projectName);
        bodyBuilder.part("projectId", projectId);
        bodyBuilder.part("flowName", flowName);

        // 是否循环
        bodyBuilder.part("is_recurring", recurring);

        // 循环周期 天 年 月等
        // 经测试，定时任务支持至少是60秒或其整数倍
        bodyBuilder.part("period", period);

        scheduleTimeInit(bodyBuilder, date);

        return client.post()
                .uri(uriBuilder -> uriBuilder.path("/schedule")
                        .build())
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void scheduleTimeInit(MultipartBodyBuilder bodyBuilder, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //The time to schedule the flow.
        // Example: 12,00,pm,PDT
        // (Unless UTC is specified, Azkaban will take current server's default timezone instead)
        bodyBuilder.part("scheduleTime", hour + "," + minute + (hour > 11 ? ",pm,PDT" : ",am,EDT"));

        //The date to schedule the flow. Example: 07/22/2014
        bodyBuilder.part("scheduleDate", month + "/" + day + "/" + year);
    }

    @Override
    public String scheduleFlowByCron(String projectName, String cron, String flow) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("session.id", sessionId);
        bodyBuilder.part("project", projectName);
        bodyBuilder.part("flow", flow);
        bodyBuilder.part("ajax", "scheduleCronFlow");
        bodyBuilder.part("cornExpression", cron);
        JSONObject response = client.post()
                .uri(uriBuilder -> uriBuilder.path("/schedule")
                        .build())
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        responseErrorHandle(response, System.out::println);
        return Objects.requireNonNull(response).getString("scheduleId");
    }

    @Override
    public void unScheduleFlow(String scheduleId) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("session.id", sessionId);
        bodyBuilder.part("action", "removeSched");
        bodyBuilder.part("scheduleId", scheduleId);
        JSONObject response = client.post()
                .uri(uriBuilder -> uriBuilder.path("/schedule")
                        .build())
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        responseErrorHandle(response, System.out::println);
    }

    @Override
    public void downLoadZip(String projectName, String zipPath) {
        File file = new File(zipPath);
        try (OutputStream output = new FileOutputStream(file);) {
            URL url = new URL(this.url + "/manager?session.id=" + sessionId + "&download=true&project="
                    + projectName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            InputStream inputStream = conn.getInputStream();
            inputStream.transferTo(output);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Mono<JSONObject> executeFlow(String projectName, String flow) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/executor")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "executeFlow")
                        .queryParam("project", projectName)
                        .queryParam("flow", flow)
                        //If a failure occurs, how should the execution behaves.
                        .queryParam("failureAction", "cancleTimmediately")
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class);
    }

    @Override
    public void cancelFlow(String execId) {
        JSONObject response = client.get()
                .uri(uriBuilder -> uriBuilder.path("/executor")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "cancelFlow")
                        .queryParam("execid", execId)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        responseErrorHandle(response, System.out::println);
    }

    @Override
    public JSONObject fetchFlowExecution(int execId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/executor")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "fetchexecflow")
                        .queryParam("execid", execId)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }

    @Override
    public JSONObject fetchFlowExecutions(String projectName, String flowId, int start, int length) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/manager")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "fetchFlowExecutions")
                        .queryParam("project", projectName)
                        .queryParam("flow", flowId)
                        .queryParam("start", start)
                        .queryParam("length", length)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }

    @Override
    public JSONObject fetchSchedule(int projectId, String flowId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/schedule")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "fetchSchedule")
                        .queryParam("flowId", flowId)
                        .queryParam("projectId", projectId)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }

    @Override
    public JSONArray fetchRunningExecOfFlow(String projectName, String flowId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/executor")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "getRunning")
                        .queryParam("project", projectName)
                        .queryParam("flow", flowId)
                        .build())
                .retrieve()
                .bodyToMono(JSONArray.class)
                .block();
    }

    @Override
    public JSONObject fetchExecJobLogs(String execId, String jobId, int offset, int length) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/executor")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "fetchExecJobLogs")
                        .queryParam("execid", execId)
                        .queryParam("jobId", jobId)
                        .queryParam("offset", offset)
                        .queryParam("length", length)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }

    @Override
    public JSONObject fetchExecFlowUpdate(String execId, long lastUpdateTime) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/executor")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "fetchexecflowupdate")
                        .queryParam("execid", execId)
                        .queryParam("lastUpdateTime", lastUpdateTime)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }

    @Override
    public JSONObject fetchProjectFlows(String projectName) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/manager")
                        .queryParam("session.id", sessionId)
                        .queryParam("ajax", "fetchprojectflows")
                        .queryParam("project", projectName)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }

    @Override
    public JSONObject fetchJobsOfFlow(String projectName, String flowId) {
        return null;
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

    private void responseErrorHandle(JSONObject response, Consumer<String> errorConsumer) {
        if (response != null) {
            String errorMsg = null;
            if (response.containsKey("error")) {
                errorMsg = response.getString("error");
            }

            if (response.containsKey("status")) {
                String status = response.getString("status");
                if (status.equals("error")) {
                    errorMsg = response.getString("message");
                } else if (status.equals("success")) {
                    return;
                } else {
                    errorMsg = response.getString("message");
                }
            }

            if (errorMsg != null) {
                errorConsumer.accept(errorMsg);
            }
        }
    }
}
