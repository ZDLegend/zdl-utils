package zdl.util.milvus;

import com.google.gson.JsonObject;
import io.milvus.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Milvus
 * <p> 官方网址：https://milvus.io/cn/
 * <p> Milvus 是一款开源的向量数据库，支持针对 TB 级向量的增删改操作和近实时查询，
 * 具有高度灵活、稳定可靠以及高速查询等特点。
 * Milvus 集成了 Faiss、NMSLIB、Annoy 等广泛应用的向量索引库，
 * 提供了一整套简单直观的 API，让你可以针对不同场景选择不同的索引类型。
 * 此外，Milvus 还可以对标量数据进行过滤，进一步提高了召回率，增强了搜索的灵活性。
 * Milvus 服务器采用主从式架构 (Client-server model)。
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/25/ 09:48
 */
public class MilvusUtils {

    private static final int MAX_PER_SEARCH = 100000;
    private static final int MAX_DATA_SIZE_PER_INSERT = 256 * 1024 * 1024;
    private static final Object lock = new Object();

    private static MilvusClient client = null;

    public static MilvusClient getClient(MilvusConfig config) {
        if (client == null) {
            synchronized (lock) {
                ConnectParam connectParam = new ConnectParam.Builder()
                        .withHost(config.getHost())
                        .withPort(config.getPort())
                        .withConnectTimeout(10, TimeUnit.SECONDS)
                        .withKeepAliveTime(Long.MAX_VALUE, TimeUnit.MICROSECONDS)
                        .withKeepAliveTimeout(20, TimeUnit.SECONDS)
                        .keepAliveWithoutCalls(false)
                        .withIdleTimeout(24, TimeUnit.HOURS)
                        .build();
                client = new MilvusGrpcClient(connectParam);
            }
        }
        return client;
    }

    public static void createCollectionDropOnExist(int dimension, String collectionName, int indexFileSize) {
        HasCollectionResponse response = client.hasCollection(collectionName);
        if (response.hasCollection()) {
            client.dropCollection(collectionName);
        }
        createCollection(dimension, collectionName, indexFileSize);
    }

    public static void createCollectionIfNotExist(int dimension, String collectionName, int indexFileSize) {
        HasCollectionResponse response = client.hasCollection(collectionName);
        if (!response.hasCollection()) {
            createCollection(dimension, collectionName, indexFileSize);
        }
    }

    /**
     * @param dimension      dimension of each vector
     * @param collectionName collection name
     * @param indexFileSize  maximum size (in MB) of each index file
     */
    private static void createCollection(int dimension, String collectionName, int indexFileSize) {
        final MetricType metricType = MetricType.IP; // we choose IP (Inner Product) as our metric type
        CollectionMapping collectionMapping =
                new CollectionMapping.Builder(collectionName, dimension)
                        .withIndexFileSize(indexFileSize)
                        .withMetricType(metricType)
                        .build();
        Response response = client.createCollection(collectionMapping);
        if (!response.ok()) {
            String message = response.getMessage();
        }
    }

    public static void batchInsert(List<Long> vectorIds, List<List<Float>> vectors,
                                   String collectionName, int dimension) {
        int piece = (vectorIds.size() * dimension * 4 / MAX_DATA_SIZE_PER_INSERT) + 1;
        int duration = vectorIds.size() / piece;
        for (int i = 0; i < piece + 1; i++) {
            int fromIndex = i * duration;
            int toIndex = fromIndex + duration;
            if (toIndex > vectorIds.size()) {
                toIndex = vectorIds.size();
            }

            if (fromIndex >= vectorIds.size()) {
                break;
            }

            List<Long> tmpVectorIds = vectorIds.subList(fromIndex, toIndex);
            List<List<Float>> tmpVectors = vectors.subList(fromIndex, toIndex);
            InsertParam insertParam =
                    new InsertParam.Builder(collectionName)
                            .withFloatVectors(tmpVectors)
                            .withVectorIds(tmpVectorIds)
                            .build();
            InsertResponse insertResponse = client.insert(insertParam);
            if (insertResponse.ok()) {

            }
        }
    }

    public static List<List<SearchResponse.QueryResult>> batchSearch(List<List<Float>> vectors,
                                                                     String collectionName,
                                                                     long topK) {
        List<List<SearchResponse.QueryResult>> finalResult = new ArrayList<>();
        int searchSize = vectors.size();
        int fromIndex = 0;
        int toIndex = Math.min(searchSize, MAX_PER_SEARCH);

        while (true) {
            List<List<Float>> tmpVectors = vectors.subList(fromIndex, toIndex);
            JsonObject searchParamsJson = new JsonObject();
            searchParamsJson.addProperty("nprobe", 20);
            SearchParam searchParam =
                    new SearchParam.Builder(collectionName)
                            .withFloatVectors(tmpVectors)
                            .withTopK(topK)
                            .withParamsInJson(searchParamsJson.toString())
                            .build();
            SearchResponse searchResponse = client.search(searchParam);
            if (searchResponse.ok()) {
                finalResult.addAll(searchResponse.getQueryResultsList());
            } else {

            }
            if (toIndex == searchSize) {
                break;
            }

            fromIndex = toIndex;
            toIndex += MAX_PER_SEARCH;
            if (toIndex > searchSize) {
                toIndex = searchSize;
            }
        }
        return finalResult;
    }

    public static void flush(String collectionName) {
        Response response = client.flush(collectionName);
        if (!response.ok()) {

        }
    }

    public static void dropCollection(String collectionName) {
        HasCollectionResponse response = client.hasCollection(collectionName);
        if (response.hasCollection()) {
            client.dropCollection(collectionName);
        }
    }

    static List<Float> normalizeVector(List<Float> vector) {
        float squareSum = vector.stream().map(x -> x * x).reduce((float) 0, Float::sum);
        final float norm = (float) Math.sqrt(squareSum);
        vector = vector.stream().map(x -> x / norm).collect(Collectors.toList());
        return vector;
    }
}
