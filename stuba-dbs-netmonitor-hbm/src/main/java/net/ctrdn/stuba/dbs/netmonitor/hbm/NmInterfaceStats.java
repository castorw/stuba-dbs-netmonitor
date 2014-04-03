package net.ctrdn.stuba.dbs.netmonitor.hbm;
// Generated Mar 18, 2014 9:13:36 PM by Hibernate Tools 3.6.0


import java.util.Date;

/**
 * NmInterfaceStats generated by hbm2java
 */
public class NmInterfaceStats  implements java.io.Serializable {


     private Integer id;
     private NmInterface nmInterface;
     private int interfaceAdminStatus;
     private int interfaceOperationalStatus;
     private int interfaceRxBytes;
     private int interfaceTxBytes;
     private int interfaceRxPackets;
     private int interfaceTxPackets;
     private int interfaceRxDiscardsDrops;
     private int interfaceTxDiscardsDrops;
     private Date createDate;

    public NmInterfaceStats() {
    }

    public NmInterfaceStats(NmInterface nmInterface, int interfaceAdminStatus, int interfaceOperationalStatus, int interfaceRxBytes, int interfaceTxBytes, int interfaceRxPackets, int interfaceTxPackets, int interfaceRxDiscardsDrops, int interfaceTxDiscardsDrops, Date createDate) {
       this.nmInterface = nmInterface;
       this.interfaceAdminStatus = interfaceAdminStatus;
       this.interfaceOperationalStatus = interfaceOperationalStatus;
       this.interfaceRxBytes = interfaceRxBytes;
       this.interfaceTxBytes = interfaceTxBytes;
       this.interfaceRxPackets = interfaceRxPackets;
       this.interfaceTxPackets = interfaceTxPackets;
       this.interfaceRxDiscardsDrops = interfaceRxDiscardsDrops;
       this.interfaceTxDiscardsDrops = interfaceTxDiscardsDrops;
       this.createDate = createDate;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public NmInterface getNmInterface() {
        return this.nmInterface;
    }
    
    public void setNmInterface(NmInterface nmInterface) {
        this.nmInterface = nmInterface;
    }
    public int getInterfaceAdminStatus() {
        return this.interfaceAdminStatus;
    }
    
    public void setInterfaceAdminStatus(int interfaceAdminStatus) {
        this.interfaceAdminStatus = interfaceAdminStatus;
    }
    public int getInterfaceOperationalStatus() {
        return this.interfaceOperationalStatus;
    }
    
    public void setInterfaceOperationalStatus(int interfaceOperationalStatus) {
        this.interfaceOperationalStatus = interfaceOperationalStatus;
    }
    public int getInterfaceRxBytes() {
        return this.interfaceRxBytes;
    }
    
    public void setInterfaceRxBytes(int interfaceRxBytes) {
        this.interfaceRxBytes = interfaceRxBytes;
    }
    public int getInterfaceTxBytes() {
        return this.interfaceTxBytes;
    }
    
    public void setInterfaceTxBytes(int interfaceTxBytes) {
        this.interfaceTxBytes = interfaceTxBytes;
    }
    public int getInterfaceRxPackets() {
        return this.interfaceRxPackets;
    }
    
    public void setInterfaceRxPackets(int interfaceRxPackets) {
        this.interfaceRxPackets = interfaceRxPackets;
    }
    public int getInterfaceTxPackets() {
        return this.interfaceTxPackets;
    }
    
    public void setInterfaceTxPackets(int interfaceTxPackets) {
        this.interfaceTxPackets = interfaceTxPackets;
    }
    public int getInterfaceRxDiscardsDrops() {
        return this.interfaceRxDiscardsDrops;
    }
    
    public void setInterfaceRxDiscardsDrops(int interfaceRxDiscardsDrops) {
        this.interfaceRxDiscardsDrops = interfaceRxDiscardsDrops;
    }
    public int getInterfaceTxDiscardsDrops() {
        return this.interfaceTxDiscardsDrops;
    }
    
    public void setInterfaceTxDiscardsDrops(int interfaceTxDiscardsDrops) {
        this.interfaceTxDiscardsDrops = interfaceTxDiscardsDrops;
    }
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }




}


