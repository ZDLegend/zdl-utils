package zdl.util.flink.bean;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
public class Hardware {

    private Long cpuCores;
    private Long freeMemory;
    private Long managedMemory;
    private Long physicalMemory;

    public Long getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Long cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Long getManagedMemory() {
        return managedMemory;
    }

    public void setManagedMemory(Long managedMemory) {
        this.managedMemory = managedMemory;
    }

    public Long getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(Long physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

}
