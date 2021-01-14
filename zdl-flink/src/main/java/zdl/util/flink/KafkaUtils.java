package zdl.util.flink;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/01/13/ 17:13
 */
public class KafkaUtils {
    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);

    /**
     * 创建topic
     *
     * @param client      kafka client
     * @param topic       topic
     * @param partition   分区
     * @param replication 备份
     */
    public static void createTopic(AdminClient client, String topic, Integer partition, Short replication) {
        boolean existTopic = isExistTopic(client, topic);
        if (!existTopic) {
            NewTopic newTopic = new NewTopic(topic, partition, replication);
            ArrayList<NewTopic> topicList = new ArrayList<>();
            topicList.add(newTopic);
            CreateTopicsResult topicsResult = client.createTopics(topicList);
            try {
                topicsResult.all().get();
                logger.info("create the " + topic + " success!");
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        } else {
            logger.warn("the " + topic + " has exist!");
        }
    }

    /**
     * 删除topic
     *
     * @param client client
     * @param topic  topic
     */
    public static void deleteTopic(AdminClient client, String topic) {
        boolean existTopic = isExistTopic(client, topic);
        if (existTopic) {
            ArrayList<String> topicList = new ArrayList<>();
            topicList.add(topic);
            try {
                client.deleteTopics(topicList).all().get();
                logger.info("delete " + topic + " success!");
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断topic 是否存在
     *
     * @param client client
     * @param topic  topic
     * @return boolean
     */
    public static boolean isExistTopic(AdminClient client, String topic) {
        ListTopicsResult listTopics = client.listTopics();
        try {
            Set<String> topicSet = listTopics.names().get();
            if (topicSet.contains(topic)) {
                logger.info("the " + topic + " exist !");
                return true;
            } else {
                logger.info("the " + topic + " not exist !");
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
