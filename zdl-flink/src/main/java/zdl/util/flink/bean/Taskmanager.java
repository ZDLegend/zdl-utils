package zdl.util.flink.bean;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
public class Taskmanager {

    private Long dataPort;
    private Long freeSlots;
    private String id;
    private String path;
    private Long slotsNumber;
    private Long timeSinceLastHeartbeat;
    private TotalResource totalResource;
    private FreeResource freeResource;
    private Hardware hardware;

    public Long getDataPort() {
        return dataPort;
    }

    public void setDataPort(Long dataPort) {
        this.dataPort = dataPort;
    }

    public FreeResource getFreeResource() {
        return freeResource;
    }

    public void setFreeResource(FreeResource freeResource) {
        this.freeResource = freeResource;
    }

    public Long getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(Long freeSlots) {
        this.freeSlots = freeSlots;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSlotsNumber() {
        return slotsNumber;
    }

    public void setSlotsNumber(Long slotsNumber) {
        this.slotsNumber = slotsNumber;
    }

    public Long getTimeSinceLastHeartbeat() {
        return timeSinceLastHeartbeat;
    }

    public void setTimeSinceLastHeartbeat(Long timeSinceLastHeartbeat) {
        this.timeSinceLastHeartbeat = timeSinceLastHeartbeat;
    }

    public TotalResource getTotalResource() {
        return totalResource;
    }

    public void setTotalResource(TotalResource totalResource) {
        this.totalResource = totalResource;
    }

}
