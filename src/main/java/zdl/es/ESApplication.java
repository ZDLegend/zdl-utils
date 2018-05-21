package zdl.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

/**
 * @author zhangminghao5
 * @create 2018/5/21
 */

public class ESApplication {

    public static RestClient restClient;

    private static final String ip = "127.0.0.1";
    private static final String port = "9200";

    public void initClient(){
        //建立es客户端
        restClient = RestClient.builder(new HttpHost(ip, Integer.valueOf(port))).build();
    }
}
