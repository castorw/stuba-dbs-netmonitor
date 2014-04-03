package net.ctrdn.stuba.dbs.netmonitor.server.probe.status;

import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeStats;
import net.ctrdn.stuba.dbs.netmonitor.server.Server;
import net.ctrdn.stuba.dbs.netmonitor.server.annotation.NetmonitorProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.ProbeException;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.SnmpException;
import net.ctrdn.stuba.dbs.netmonitor.server.probe.Probe;
import net.ctrdn.stuba.dbs.netmonitor.server.snmp.SnmpClient;
import org.hibernate.Session;
import org.snmp4j.smi.OID;

@NetmonitorProbe(
        author = "Lubomir Kaplan",
        name = "SNMP Status Probe",
        description = "Simple device basic response test",
        version = "1.0.2"
)
public class SnmpStatusProbe implements Probe {

    private Server server;
    private NmDevice deviceRecord;
    private NmProbe probeRecord;
    private SnmpClient snmpClient;

    public SnmpStatusProbe() {
    }

    @Override
    public void configure(Server server, NmProbe probeRecord, NmDevice deviceRecord) throws ProbeException {
        try {
            this.server = server;
            this.probeRecord = probeRecord;
            this.deviceRecord = deviceRecord;
            this.snmpClient = new SnmpClient(deviceRecord);
        } catch (SnmpException ex) {
            ProbeException finalEx = new ProbeException("Failed to initialize SNMP client");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    @Override
    public void poll() throws ProbeException {
        Session mysqlSession = this.server.getMysqlSessionFactory().openSession();
        try {
            int probeStatus = 0;
            SnmpException acquireException = null;
            String sysDescr = null, sysUptime = null, sysName = null;
            try {
                sysDescr = this.snmpClient.getSnmpDataAsString(new OID(".1.3.6.1.2.1.1.1.0"));
                sysUptime = this.snmpClient.getSnmpDataAsString(new OID(".1.3.6.1.2.1.1.3.0"));
                sysName = this.snmpClient.getSnmpDataAsString(new OID("1.3.6.1.2.1.1.5.0"));
                probeStatus = 1;
            } catch (SnmpException ex) {
                acquireException = ex;
            }
            mysqlSession.beginTransaction();
            if (probeStatus == 1) {
                this.updateStats(mysqlSession, "sysName", sysName);
                this.updateStats(mysqlSession, "sysDescr", sysDescr);
                this.updateStats(mysqlSession, "sysUptime", sysUptime);
            }
            mysqlSession.createSQLQuery("UPDATE `nm_probe` SET `probe_status` = '" + probeStatus + "', `last_update_date` = CURRENT_TIMESTAMP WHERE `id` = '" + this.probeRecord.getId() + "'").executeUpdate();
            mysqlSession.getTransaction().commit();
            if (acquireException != null) {
                throw acquireException;
            }
        } catch (SnmpException ex) {
            ProbeException finalEx = new ProbeException("SNMP failed for " + this.deviceRecord.getDeviceName() + " (" + this.deviceRecord.getIpv4Address() + ")");
            finalEx.addSuppressed(ex);
            throw finalEx;
        } finally {
            mysqlSession.disconnect();
        }
    }

    private void updateStats(Session mysqlSession, String attributeName, String value) {
        NmProbeStats statsRecord = (NmProbeStats) mysqlSession.createSQLQuery("SELECT * FROM `nm_probe_stats` WHERE `probe_identity_id` = '" + this.probeRecord.getId() + "' AND `attribute_name` = '" + attributeName + "'").addEntity(NmProbeStats.class).uniqueResult();
        if (statsRecord == null && value != null) {
            mysqlSession.createSQLQuery("INSERT INTO `nm_probe_stats` (`probe_identity_id`, `attribute_name`, `attribute_value`, `last_update_date`) VALUES ('" + this.probeRecord.getId() + "', '" + attributeName + "', '" + value + "', CURRENT_TIMESTAMP)").executeUpdate();
        } else if (statsRecord != null && value != null) {
            mysqlSession.createSQLQuery("UPDATE `nm_probe_stats` SET `attribute_value` = '" + value + "', `last_update_date` = CURRENT_TIMESTAMP WHERE `id` = '" + statsRecord.getId() + "'").executeUpdate();
        } else if (statsRecord != null && value == null) {
            mysqlSession.createSQLQuery("DELETE FROM `nm_probe_stats` WHERE `id` = '" + statsRecord.getId() + "'").executeUpdate();
        }
    }

}
