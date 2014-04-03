package net.ctrdn.stuba.dbs.netmonitor.server.probe.iface;

import java.util.List;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterface;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.Server;
import net.ctrdn.stuba.dbs.netmonitor.server.annotation.NetmonitorProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.ProbeException;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.SnmpException;
import net.ctrdn.stuba.dbs.netmonitor.server.logging.LogSeverity;
import net.ctrdn.stuba.dbs.netmonitor.server.probe.Probe;
import net.ctrdn.stuba.dbs.netmonitor.server.snmp.SnmpClient;
import org.hibernate.Session;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

@NetmonitorProbe(
        author = "Lubomir Kaplan",
        name = "SNMP Interface Probe",
        description = "Remote device interface stats probe",
        version = "1.0.0"
)
public class SnmpInterfaceProbe implements Probe {

    private Server server;
    private NmProbe probeRecord;
    private NmDevice deviceRecord;
    private SnmpClient snmpClient;

    @Override
    public void configure(Server server, NmProbe probeRecord, NmDevice deviceRecord) throws ProbeException {
        try {
            this.server = server;
            this.deviceRecord = deviceRecord;
            this.probeRecord = probeRecord;
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
            SnmpInterfaceList<SnmpInterface> interfaceList = null;
            SnmpException acquireException = null;
            int probeStatus = 0;
            try {
                List<VariableBinding> allBindings = this.snmpClient.getSnmpWalk(new OID(".1.3.6.1.2.1.2.2.1"));
                interfaceList = SnmpInterfaceList.buildList(allBindings);
                probeStatus = 1;
            } catch (SnmpException ex) {
                acquireException = ex;
            }
            mysqlSession.beginTransaction();
            if (probeStatus == 1 && interfaceList != null) {
                int ifsAdded = 0, ifsUpdated = 0, ifsRemoved = 0;

                List<NmInterface> currentInterfaceList = mysqlSession.createSQLQuery("SELECT * FROM `nm_interface` WHERE `probe_id` = '" + this.probeRecord.getId() + "'").addEntity(NmInterface.class).list();
                for (NmInterface cifRecord : currentInterfaceList) {
                    if (interfaceList.getInterfaceByIfIndex(cifRecord.getInterfaceIndex()) == null) {
                        mysqlSession.createSQLQuery("DELETE FROM `nm_interface_stats` WHERE `interface_id` = '" + cifRecord.getId() + "'").executeUpdate();
                        mysqlSession.createSQLQuery("DELETE FROM `nm_interface` WHERE `id` = '" + cifRecord.getId() + "'").executeUpdate();
                        ifsRemoved++;
                        this.logMessage(LogSeverity.NOTICE, "Interface " + cifRecord.getInterfaceName() + " has been removed because it had disappeared from remote device");
                    }
                }
                for (SnmpInterface snmpInterface : interfaceList) {
                    NmInterface ifaceRecord = (NmInterface) mysqlSession.createSQLQuery("SELECT * FROM `nm_interface` WHERE `probe_id` = '" + this.probeRecord.getId() + "' AND `interface_index` = '" + snmpInterface.getIndex() + "'").addEntity(NmInterface.class).uniqueResult();
                    if (ifaceRecord == null) {
                        mysqlSession.createSQLQuery("INSERT INTO `nm_interface` (`probe_id`, `interface_index`, `interface_name`, `interface_type`, `last_update_date`) VALUES ('" + this.probeRecord.getId() + "', '" + snmpInterface.getIndex() + "', \"" + SnmpInterfaceProbe.addSlashes(snmpInterface.getName()) + "\", '" + snmpInterface.getType() + "', CURRENT_TIMESTAMP)").executeUpdate();
                        ifsAdded++;
                    } else {
                        mysqlSession.createSQLQuery("UPDATE `nm_interface` SET `interface_name` = \"" + SnmpInterfaceProbe.addSlashes(snmpInterface.getName()) + "\", `interface_type` = '" + snmpInterface.getType() + "', `last_update_date` = CURRENT_TIMESTAMP WHERE `id` = '" + ifaceRecord.getId() + "'").executeUpdate();
                        ifsUpdated++;
                    }
                }
                this.logMessage(LogSeverity.DEBUG, "Interface enumeration finished; created=" + ifsAdded + ", removed=" + ifsRemoved + ", updated=" + ifsUpdated);

                int ifStatsCount = 0;

                for (SnmpInterface snmpInterface : interfaceList) {
                    NmInterface ifaceRecord = (NmInterface) mysqlSession.createSQLQuery("SELECT * FROM `nm_interface` WHERE `probe_id` = '" + this.probeRecord.getId() + "' AND `interface_index` = '" + snmpInterface.getIndex() + "'").addEntity(NmInterface.class).uniqueResult();
                    if (ifaceRecord != null) {
                        String statsInsertQuery = "";
                        statsInsertQuery += "INSERT INTO `nm_interface_stats` ";
                        statsInsertQuery += "(`interface_id`, `interface_admin_status`, `interface_operational_status`, `interface_rx_bytes`, `interface_tx_bytes`, `interface_rx_packets`, `interface_tx_packets`, `interface_rx_discards_drops`, `interface_tx_discards_drops`) ";
                        statsInsertQuery += "VALUES ";
                        statsInsertQuery += "('" + ifaceRecord.getId() + "', '" + snmpInterface.getAdminStatus() + "', '" + snmpInterface.getOperationalStatus() + "', '" + snmpInterface.getRxBytes() + "', '" + snmpInterface.getTxBytes() + "', '" + snmpInterface.getRxPackets() + "', '" + snmpInterface.getTxPackets() + "', '" + snmpInterface.getRxDiscardsDrops() + "', '" + snmpInterface.getTxDiscardsDrops() + "')";
                        mysqlSession.createSQLQuery(statsInsertQuery).executeUpdate();
                        ifStatsCount++;
                    } else {
                        throw new ProbeException("Internal error: interface record not present");
                    }
                }
                this.logMessage(LogSeverity.DEBUG, "Interface stats update finished; created=" + ifStatsCount);

                String aggregatedStatsQuery
                        = "INSERT INTO `nm_interface_aggregated_stats` (`probe_id`, `interface_count`, `rx_bytes`, `tx_bytes`, `rx_packets`, `tx_packets`)\n"
                        + "SELECT `ta`.`probe_id` as `probe_id`, COUNT(`ta`.`id`) as `interface_count`,\n"
                        + "	   `tb`.`rx_bytes` as `rx_bytes`,\n"
                        + "	   `tb`.`tx_bytes` as `tx_bytes`,\n"
                        + "	   `tb`.`rx_packets` as `rx_packets`,\n"
                        + "	   `tb`.`tx_packets` as `tx_packets`\n"
                        + "FROM `nm_interface` as `ta`\n"
                        + "JOIN (\n"
                        + "		SELECT `tba`.`probe_id` as `probe_id`,\n"
                        + "			   SUM(`tbb`.`interface_rx_bytes`) as `rx_bytes`,\n"
                        + "			   SUM(`tbb`.`interface_tx_bytes`) as `tx_bytes`,\n"
                        + "			   SUM(`tbb`.`interface_rx_packets`) as `rx_packets`,\n"
                        + "			   SUM(`tbb`.`interface_tx_packets`) as `tx_packets`\n"
                        + "		FROM `nm_interface` as `tba`\n"
                        + "		\n"
                        + "		JOIN (\n"
                        + "				SELECT *\n"
                        + "				FROM `nm_interface_stats` as `tbba`\n"
                        + "				WHERE `tbba`.`create_date` = (SELECT MAX(`create_date`) FROM `nm_interface_stats` WHERE `interface_id` = `tbba`.`interface_id`)\n"
                        + "		) as `tbb` ON `tbb`.`interface_id` = `tba`.`id`\n"
                        + "		\n"
                        + "		GROUP BY `tba`.`probe_id`\n"
                        + ") as `tb` ON `ta`.`probe_id` = `tb`.`probe_id`\n"
                        + "WHERE `ta`.`probe_id` = '" + this.probeRecord.getId() + "'\n"
                        + "GROUP BY `ta`.`probe_id`;";
                mysqlSession.createSQLQuery(aggregatedStatsQuery).executeUpdate();
                this.logMessage(LogSeverity.DEBUG, "Aggregated stats has been generated for " + ifStatsCount + " interfaces");
            }
            mysqlSession.createSQLQuery("UPDATE `nm_probe` SET `probe_status` = '" + probeStatus + "', `last_update_date` = CURRENT_TIMESTAMP WHERE `id` = '" + this.probeRecord.getId() + "'").executeUpdate();
            mysqlSession.getTransaction().commit();
            if (acquireException != null) {
                throw acquireException;
            }
        } catch (SnmpException ex) {
            ProbeException finalEx = new ProbeException("SNMP operation failed for " + this.deviceRecord.getDeviceName() + " (" + this.deviceRecord.getIpv4Address() + "): " + ex.getMessage());
            finalEx.addSuppressed(ex);
            throw finalEx;
        } finally {
            mysqlSession.disconnect();
        }
    }

    public static String addSlashes(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\\n", "\\\\n");
        s = s.replaceAll("\\r", "\\\\r");
        s = s.replaceAll("\\00", "\\\\0");
        s = s.replaceAll("'", "\\\\'");
        return s;
    }

    private void logMessage(LogSeverity severity, String message) {
        this.server.getLog().message(severity, "[" + this.getClass().getSimpleName() + "] [" + this.deviceRecord.getDeviceName() + "] " + message);
    }
}
