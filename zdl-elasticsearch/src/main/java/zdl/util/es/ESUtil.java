package zdl.util.es;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;

import java.io.IOException;

/**
 * @author ZDLegend
 * @create 2017/12/12
 */

public class ESUtil {

    /**
     * http请求方式
     */
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";

    /**
     * ES索引TYPE
     */
    private static final String TYPE_CREATE = "_create";
    private static final String TYPE_DOC = "_doc";

    /**
     * 插入数据
     *
     * @param index 索引
     * @param id    id
     * @param data  数据
     * @return ES返回消息的json格式信息
     * @throws IOException             in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     * @throws ResponseException       in case Elasticsearch responded with a status code that indicated an error
     */
    public static JSONObject insert(String index, String id, String data) throws IOException {

        String endpoint = "/" + index + "/" + TYPE_CREATE + "/" + id;

        Request request = new Request(POST, endpoint);
        request.setJsonEntity(data);

        Response response = ESApplication.ES_CLIENT.performRequest(request);

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
    public static JSONObject update(String index, String id, String data) throws IOException {

        String endpoint = "/" + index + "/" + TYPE_CREATE + "/" + id + "/_update";

        Request request = new Request(PUT, endpoint);
        request.setJsonEntity(data);

        Response response = ESApplication.ES_CLIENT.performRequest(request);

        String responseBody = consumeResponse(response);

        return JSONObject.parseObject(responseBody);
    }

    /**
     * 删除一条数据
     *
     * @param index 数据所在索引
     * @param id    数据ID
     * @return ES返回消息的json格式信息
     * @throws IOException             in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     * @throws ResponseException       in case Elasticsearch responded with a status code that indicated an error
     */
    public static JSONObject delete(String index, String id) throws IOException {

        String endpoint;
        if (StringUtils.isEmpty(id)) {
            endpoint = "/" + index;
        } else {
            endpoint = "/" + index + "/" + TYPE_DOC + "/" + id;
        }

        Request request = new Request(DELETE, endpoint);

        Response response = ESApplication.ES_CLIENT.performRequest(request);

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
        return delete(index, null);
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
