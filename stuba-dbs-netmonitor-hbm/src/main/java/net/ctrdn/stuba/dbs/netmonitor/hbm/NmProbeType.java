package net.ctrdn.stuba.dbs.netmonitor.hbm;
// Generated Apr 5, 2014 4:21:51 PM by Hibernate Tools 3.6.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * NmProbeType generated by hbm2java
 */
@Entity
@Table(name = "nm_probe_type", catalog = "stuba_netmonitor"
)
public class NmProbeType implements java.io.Serializable {

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

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "probe_name", nullable = false)
    public String getProbeName() {
        return this.probeName;
    }

    public void setProbeName(String probeName) {
        this.probeName = probeName;
    }

    @Column(name = "probe_description", length = 65535)
    public String getProbeDescription() {
        return this.probeDescription;
    }

    public void setProbeDescription(String probeDescription) {
        this.probeDescription = probeDescription;
    }

    @Column(name = "probe_classpath", nullable = false)
    public String getProbeClasspath() {
        return this.probeClasspath;
    }

    public void setProbeClasspath(String probeClasspath) {
        this.probeClasspath = probeClasspath;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nmProbeType", cascade = javax.persistence.CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    public Set getNmProbes() {
        return this.nmProbes;
    }

    public void setNmProbes(Set nmProbes) {
        this.nmProbes = nmProbes;
    }

}
