package net.ctrdn.stuba.dbs.netmonitor.hbm;
// Generated Mar 18, 2014 9:13:36 PM by Hibernate Tools 3.6.0


import java.util.HashSet;
import java.util.Set;

/**
 * NmProbeType generated by hbm2java
 */
public class NmProbeType  implements java.io.Serializable {


     private Integer id;
     private String probeName;
     private String probeDescription;
     private String probeClasspath;
     private Set nmProbes = new HashSet(0);

    public NmProbeType() {
    }

	
    public NmProbeType(String probeName, String probeClasspath) {
        this.probeName = probeName;
        this.probeClasspath = probeClasspath;
    }
    public NmProbeType(String probeName, String probeDescription, String probeClasspath, Set nmProbes) {
       this.probeName = probeName;
       this.probeDescription = probeDescription;
       this.probeClasspath = probeClasspath;
       this.nmProbes = nmProbes;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getProbeName() {
        return this.probeName;
    }
    
    public void setProbeName(String probeName) {
        this.probeName = probeName;
    }
    public String getProbeDescription() {
        return this.probeDescription;
    }
    
    public void setProbeDescription(String probeDescription) {
        this.probeDescription = probeDescription;
    }
    public String getProbeClasspath() {
        return this.probeClasspath;
    }
    
    public void setProbeClasspath(String probeClasspath) {
        this.probeClasspath = probeClasspath;
    }
    public Set getNmProbes() {
        return this.nmProbes;
    }
    
    public void setNmProbes(Set nmProbes) {
        this.nmProbes = nmProbes;
    }




}


