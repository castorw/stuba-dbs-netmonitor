package net.ctrdn.stuba.dbs.netmonitor.server.probe.iface;

public class SnmpInterface extends Object {

    private final int index;
    private final String name;
    private final String type;
    private final int adminStatus;
    private final int operationalStatus;
    private final long rxBytes;
    private final long txBytes;
    private final long rxPackets;
    private final long txPackets;
    private final long rxDiscardsDrops;
    private final long txDiscardsDrops;

    protected SnmpInterface(int index, String name, String type, int adminStatus, int operationalStatus, long rxBytes, long txBytes, long rxPackets, long txPackets, long rxDiscardsDrops, long txDiscardsDrops) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.adminStatus = adminStatus;
        this.operationalStatus = operationalStatus;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.rxPackets = rxPackets;
        this.txPackets = txPackets;
        this.rxDiscardsDrops = rxDiscardsDrops;
        this.txDiscardsDrops = txDiscardsDrops;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getAdminStatus() {
        return adminStatus;
    }

    public int getOperationalStatus() {
        return operationalStatus;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public long getRxPackets() {
        return rxPackets;
    }

    public long getTxPackets() {
        return txPackets;
    }

    public long getRxDiscardsDrops() {
        return rxDiscardsDrops;
    }

    public long getTxDiscardsDrops() {
        return txDiscardsDrops;
    }

}
