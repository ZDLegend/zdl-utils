package zdl.util.flink.bean;

import com.alibaba.fastjson.JSONObject;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
public class TotalResource {

    private Double cpuCores;
    private JSONObject extendedResources;
    private Long managedMemory;
    private Long networkMemory;
    private Long taskHeapMemory;
    private Long taskOffHeapMemory;

    public Double getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Double cpuCores) {
        this.cpuCores = cpuCores;
    }

    public JSONObject getExtendedResources() {
        return extendedResources;
    }

    public void setExtendedResources(JSONObject extendedResources) {
        this.extendedResources = extendedResources;
    }

    public Long getManagedMemory() {
        return managedMemory;
    }

    public void setManagedMemory(Long managedMemory) {
        this.managedMemory = managedMemory;
    }

    public Long getNetworkMemory() {
        return networkMemory;
    }

    public void setNetworkMemory(Long networkMemory) {
        this.networkMemory = networkMemory;
    }

    public Long getTaskHeapMemory() {
        return taskHeapMemory;
    }

    public void setTaskHeapMemory(Long taskHeapMemory) {
        this.taskHeapMemory = taskHeapMemory;
    }

    public Long getTaskOffHeapMemory() {
        return taskOffHeapMemory;
    }

    public void setTaskOffHeapMemory(Long taskOffHeapMemory) {
        this.taskOffHeapMemory = taskOffHeapMemory;
    }

}
