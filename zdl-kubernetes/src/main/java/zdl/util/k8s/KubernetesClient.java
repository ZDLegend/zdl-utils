package zdl.util.k8s;

import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

import java.util.List;

/**
 * Created by ZDLegend on 2019/12/25 17:27
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

    public String getIpForPod(String podName) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(podName)
                .get()
                .getStatus()
                .getPodIP();
    }

    public boolean deletePod(String podName) {
        return this.pods()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(podName)
                .delete();
    }

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
