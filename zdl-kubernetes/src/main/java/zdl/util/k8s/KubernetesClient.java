package zdl.util.k8s;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

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

    public String getIpForPort(String podName) {
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

    public int getNodeNum(String name) {
        return this.apps()
                .statefulSets()
                .inNamespace(DEFAULT_NAME_SPACE)
                .withName(name)
                .get()
                .getSpec()
                .getReplicas();
    }
}
