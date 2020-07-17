package zdl.util.es;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

/**
 * @author ZDLegend
 * @create 2020/07/17
 */
public class ESHighClient {

    /**
     * 参考官方文档：
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html#java-rest-high-document-index-async
     */
    public static void index(String index, String id, String data) throws IOException {
        IndexRequest request = new IndexRequest(index);
        request.id(id)
                .source(data, XContentType.JSON)
                .timeout(TimeValue.timeValueSeconds(1))
                .setIfSeqNo(10L)
                .setIfPrimaryTerm(20)
                .versionType(VersionType.EXTERNAL)
                .opType(DocWriteRequest.OpType.CREATE);
        try {
            IndexResponse indexResponse = ESApplication.ES_HIGH_CLIENT.index(request, RequestOptions.DEFAULT);
            indexResponseHandle(indexResponse);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                //todo: The raised exception indicates that a version conflict error was returned.
                //The raised exception indicates that a version conflict error was returned
            }
        }
    }

    public static void indexAsync(String index, String id, String data) {
        IndexRequest request = new IndexRequest(index);
        request.id(id)
                .source(data, XContentType.JSON)
                .setIfSeqNo(10L)
                .setIfPrimaryTerm(20)
                .opType(DocWriteRequest.OpType.CREATE);
        /*
         * Executing a IndexRequest can also be done in an asynchronous fashion so that the client can return directly.
         * Users need to specify how the response or potential failures will be handled by passing the request and a
         * listener to the asynchronous index method
         */
        ESApplication.ES_HIGH_CLIENT.indexAsync(request, RequestOptions.DEFAULT,
                /*
                 * The asynchronous method does not block and returns immediately.
                 * Once it is completed the ActionListener is called back using the onResponse method if the execution
                 * successfully completed or using the onFailure method if it failed.
                 * Failure scenarios and expected exceptions are the same as in the synchronous execution case.
                 */
                new ActionListener<>() {
                    @Override
                    public void onResponse(IndexResponse indexResponse) {
                        indexResponseHandle(indexResponse);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        //todo: Called when the whole IndexRequest fails.
                    }
                });
    }

    /**
     * The returned IndexResponse allows to retrieve information about the executed operation as follows
     */
    public static void indexResponseHandle(IndexResponse indexResponse) {
        String index = indexResponse.getIndex();
        String id = indexResponse.getId();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            //todo: Handle (if needed) the case where the document was created for the first time
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            //todo: Handle (if needed) the case where the document was rewritten as it was already existing
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            //todo: Handle the situation where number of successful shards is less than total shards
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                //todo: Handle the potential failures
                String reason = failure.reason();
            }
        }
    }
}
