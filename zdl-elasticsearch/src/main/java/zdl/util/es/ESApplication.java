package zdl.util.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author ZDLegend
 * @create 2018/5/21
 */
public class ESApplication {

    /**
     * Java Low Level REST Client：elasticsearch client 低级别客户端。
     * 它允许通过http请求与Elasticsearch集群进行通信。
     * API本身不负责数据的编码解码，由用户去编码解码。
     * 它与所有的ElasticSearch版本兼容。
     */
    public static RestClient ES_CLIENT;

    /**
     * Java High Level REST Client：Elasticsearch client官方高级客户端。
     * 基于低级客户端，它定义的API，已经对请求与响应数据包进行编码解码。
     */
    public static RestHighLevelClient ES_HIGH_CLIENT;

    private static final String ip = "127.0.0.1";
    private static final String port = "9200";

    public void initClient() {
        //建立es客户端
        ES_CLIENT = RestClient.builder(new HttpHost(ip, Integer.parseInt(port))).build();
        ES_HIGH_CLIENT = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http"))
        );
    }
}
