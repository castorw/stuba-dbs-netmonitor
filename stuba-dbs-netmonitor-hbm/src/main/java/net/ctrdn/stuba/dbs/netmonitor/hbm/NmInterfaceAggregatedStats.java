package net.ctrdn.stuba.dbs.netmonitor.hbm;
// Generated Mar 18, 2014 9:13:36 PM by Hibernate Tools 3.6.0

import java.util.Date;

/**
 * NmInterfaceAggregatedStats generated by hbm2java
 */
public class NmInterfaceAggregatedStats implements java.io.Serializable {

    private Integer id;
    private NmProbe nmProbe;
    private int interfaceCount;
    private long rxBytesSec;
    private long txBytesSec;
    private long rxPacketsSec;
    private long txPacketsSec;
    private Date createDate;

    public NmInterfaceAggregatedStats() {
    }

    public NmInterfaceAggregatedStats(NmProbe nmProbe, int interfaceCount, long rxBytesSec, long txBytesSec, long rxPacketsSec, long txPacketsSec, Date createDaet) {
        this.nmProbe = nmProbe;
        this.interfaceCount = interfaceCount;
        this.rxBytesSec = rxBytesSec;
        this.txBytesSec = txBytesSec;
        this.rxPacketsSec = rxPacketsSec;
        this.txPacketsSec = txPacketsSec;
        this.createDate = createDaet;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NmProbe getNmProbe() {
        return this.nmProbe;
    }

    public void setNmProbe(NmProbe nmProbe) {
        this.nmProbe = nmProbe;
    }

    public int getInterfaceCount() {
        return this.interfaceCount;
    }

    public void setInterfaceCount(int interfaceCount) {
        this.interfaceCount = interfaceCount;
    }

    public long getRxBytesSec() {
        return this.rxBytesSec;
    }

    public void setRxBytesSec(long rxBytesSec) {
        this.rxBytesSec = rxBytesSec;
    }

    public long getTxBytesSec() {
        return this.txBytesSec;
    }

    public void setTxBytesSec(long txBytesSec) {
        this.txBytesSec = txBytesSec;
    }

    public long getRxPacketsSec() {
        return this.rxPacketsSec;
    }

    public void setRxPacketsSec(long rxPacketsSec) {
        this.rxPacketsSec = rxPacketsSec;
    }

    public long getTxPacketsSec() {
        return this.txPacketsSec;
    }

    public void setTxPacketsSec(long txPacketsSec) {
        this.txPacketsSec = txPacketsSec;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDaet) {
        this.createDate = createDaet;
    }

}
