package zdl.util.es;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

/**
 * @author ZDLegend
 * @create 2017/12/12
 */

public class ESUtil {

    /**
     * http请求方式
     */
    private static String POST = "POST";
    private static String PUT = "PUT";
    private static String GET = "GET";
    private static String DELETE = "DELETE";

    /**
     * ES索引TYPE
     */
    private static String TYPE = "zdl";

    /**
     * ES客户端
     */
    private static RestClient restClient = ESApplication.restClient;

    /**
     * 插入数据
     *
     * @param index 索引
     * @param type  类型
     * @param id    id
     * @param data  数据
     * @return ES返回消息的json格式信息
     * @throws IOException             in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     * @throws ResponseException       in case Elasticsearch responded with a status code that indicated an error
     */
    public static JSONObject insert(String index, String type, String id, String data) throws IOException {

        if (StringUtils.isEmpty(type)) {
            type = TYPE;
        }

        HttpEntity entity = new NStringEntity(data, ContentType.APPLICATION_JSON);

        String endpoint = "/" + index + "/" + type + "/" + id;

        Response response = restClient.performRequest(PUT, endpoint, Collections.emptyMap(), entity);

        String responseBody = consumeResponse(response);

        return JSONObject.parseObject(responseBody);
    }

    /**
     * 更新指定数据
     *
     * @return ES返回消息的json格式信息
     * @throws IOException             in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     * @throws ResponseException       in case Elasticsearch responded with a status code that indicated an error
     */
    public static JSONObject update(String index, String type, String id, String data) throws IOException {

        if (StringUtils.isEmpty(type)) {
            type = TYPE;
        }

        HttpEntity entity = new NStringEntity(data, ContentType.APPLICATION_JSON);

        String endpoint = "/" + index + "/" + type + "/" + id + "/_update";

        Response response = restClient.performRequest(POST, endpoint, Collections.emptyMap(), entity);

        String responseBody = consumeResponse(response);

        return JSONObject.parseObject(responseBody);
    }

    /**
     * 删除一条数据
     *
     * @param index 数据所在索引
     * @param type  数据所在type
     * @param id    数据ID
     * @return ES返回消息的json格式信息
     * @throws IOException             in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     * @throws ResponseException       in case Elasticsearch responded with a status code that indicated an error
     */
    public static JSONObject delete(String index, String type, String id) throws IOException {

        if (StringUtils.isEmpty(type)) {
            type = TYPE;
        }

        String endpoint;
        if (StringUtils.isEmpty(id)) {
            endpoint = "/" + index;
        } else {
            endpoint = "/" + index + "/" + type + "/" + id;
        }

        Response response = restClient.performRequest(DELETE, endpoint, Collections.emptyMap());

        String responseBody = consumeResponse(response);

        return JSONObject.parseObject(responseBody);
    }

    /**
     * 删除索引
     *
     * @param index 索引值
     * @return ES返回消息的json格式信息
     * @throws IOException             in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     * @throws ResponseException       in case Elasticsearch responded with a status code that indicated an error
     */
    public static JSONObject deleteIndex(String index) throws IOException {
        return delete(index, null, null);
    }

    //读取json 内容
    public static String consumeResponse(Response response) throws IOException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK
                && response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
            return null;
        }

        HttpEntity httpClientEntity = response.getEntity();
        String responseContent = EntityUtils.toString(httpClientEntity);
        EntityUtils.consume(httpClientEntity);

        return responseContent;
    }
}
