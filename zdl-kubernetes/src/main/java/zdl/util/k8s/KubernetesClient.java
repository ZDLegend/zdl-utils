package zdl.util.k8s;

import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

import java.util.List;

/**
 * Kubernetes（k8s）是自动化容器操作的开源平台，这些操作包括部署，调度和节点集群间扩展。如果你曾经用过Docker容器技术部署容器，
 * 那么可以将Docker看成Kubernetes内部使用的低级别组件。Kubernetes不仅仅支持Docker，还支持Rocket，这是另一种容器技术。
 * 使用Kubernetes可以：
 * <li>1.自动化容器的部署和复制
 * <li>2.随时扩展或收缩容器规模
 * <li>3.将容器组织成组，并且提供容器间的负载均衡
 * <li>4.很容易地升级应用程序容器的新版本
 * <li>5.提供容器弹性，如果容器失效就替换它，等等...
 * <p>
 * <li>{@code 集群}:<p>
 * 集群是一组节点，这些节点可以是物理服务器或者虚拟机，之上安装了Kubernetes平台。<p>
 * <li>{@code Pod}:<p>
 * Pod安排在节点上，包含一组容器和卷。同一个Pod里的容器共享同一个网络命名空间，可以使用localhost互相通信。Pod是短暂的，不是持续性实体。<p>
 * <li>{@code Lable}:<p>
 * 一些Pod有Label。一个Label是attach到Pod的一对键/值对，用来传递用户定义的属性。
 * 比如，你可能创建了一个"tier"和“app”标签，通过Label（tier=frontend, app=myapp）来标记前端Pod容器，
 * 使用Label（tier=backend, app=myapp）标记后台Pod。然后可以使用Selectors选择带有特定Label的Pod，
 * 并且将Service或者Replication Controller应用到上面。<p>
 * <li>{@code Replication Controller}:<p>
 * RC确保任意时间都有指定数量的Pod“副本”在运行。
 * 如果为某个Pod创建了Replication Controller并且指定3个副本，它会创建3个Pod，并且持续监控它们。
 * 如果某个Pod不响应，那么Replication Controller会替换它，保持总数为3.
 * 如果之前不响应的Pod恢复了，现在就有4个Pod了，那么Replication Controller会将其中一个终止保持总数为3。
 * 如果在运行中将副本总数改为5，Replication Controller会立刻启动2个新Pod，
 * 保证总数为5。还可以按照这样的方式缩小Pod，这个特性在执行滚动升级时很有用。<p>
 * <li>{@code Service}:<p>
 * Service是定义一系列Pod以及访问这些Pod的策略的一层抽象。Service通过Label找到Pod组。
 * 现在，假定有2个后台Pod，并且定义后台Service的名称为‘backend-service’，lable选择器为（tier=backend, app=myapp）。
 * backend-service 的Service会完成如下两件重要的事情：
 * <p>1.会为Service创建一个本地集群的DNS入口，因此前端Pod只需要DNS查找主机名为 ‘backend-service’，就能够解析出前端应用程序可用的IP地址。</li>
 * <p>2.现在前端已经得到了后台服务的IP地址，但是它应该访问2个后台Pod的哪一个呢？
 * Service在这2个后台Pod之间提供透明的负载均衡，会将请求分发给其中的任意一个。
 * 通过每个Node上运行的代理（kube-proxy）完成。这里有更多技术细节。</li>
 * </li>
 * 有一个特别类型的Kubernetes Service，称为'LoadBalancer'，作为外部负载均衡器使用，在一定数量的Pod之间均衡流量。比如，对于负载均衡Web流量很有用。<p>
 * <li>{@code Node}:<p>
 * 节点（上图橘色方框）是物理或者虚拟机器，作为Kubernetes worker，通常称为Minion。每个节点都运行如下Kubernetes关键组件：
 * <p>Kubelet：是主节点代理。
 * <p>Kube-proxy：Service使用其将链接路由到Pod，如上文所述。
 * <p>Docker或Rocket：Kubernetes使用的容器技术来创建容器。
 * <p>
 * <li>{@code Kubernetes Master}:<p>
 * 集群拥有一个Kubernetes Master。
 * Kubernetes Master提供集群的独特视角，并且拥有一系列组件，比如Kubernetes API Server。
 * API Server提供可以用来和集群交互的REST端点。master节点包括用来创建和复制Pod的Replication Controller。<p>
 *
 * @author ZDLegend
 * @since 2019/12/25 17:27
 */
public class KubernetesClient extends DefaultKubernetesClient {

    private static final String DEFAULT_NAME_SPACE = "default";

    public KubernetesClient() {
        super();
    }

    public KubernetesClient(String masterUrl) {
        super(masterUrl);
    }

    public KubernetesClient(Config config) {
        super(config);
    }

    /**
     * 获取pod ip地址
     *
     * @param podName pod名
     * @return pod在服务中的ip地址
     */
    public String getIpForPod(String podName) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(podName)
                .get()
                .getStatus()
                .getPodIP();
    }

    /**
     * 重启某个pod
     *
     * @param podName pod名
     * @return 是否删除成功
     */
    public boolean deletePod(String podName) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(podName)
                .delete();
    }

    /**
     * 获取某个服务node数量
     *
     * @param serviceName 服务名
     * @return node数量
     */
    public int getNodeNum(String serviceName) {
        return this.apps()
                .statefulSets()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(serviceName)
                .get()
                .getSpec()
                .getReplicas();
    }

    public List<Volume> getVolumesMounts(String podName) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(podName)
                .get()
                .getSpec()
                .getVolumes();
    }

    public long getRunningPodNum(String serviceName) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withLabel("name", serviceName)
                .list()
                .getItems()
                .stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .count();
    }

    /**
     * 获取某个pod中某个参数最大值
     *
     * @param podName     pod名
     * @param serviceName 服务名
     * @param key         参数键值
     * @return 值
     */
    public String getPodLimitsByKey(String podName, String serviceName, String key) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(podName)
                .get()
                .getSpec()
                .getContainers()
                .stream()
                .filter(container -> container.getName().equals(serviceName))
                .findFirst()
                .orElseThrow()
                .getResources()
                .getLimits()
                .get(key)
                .getAmount();
    }
}
