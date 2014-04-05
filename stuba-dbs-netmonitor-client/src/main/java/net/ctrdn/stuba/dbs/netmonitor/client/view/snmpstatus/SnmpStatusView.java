package net.ctrdn.stuba.dbs.netmonitor.client.view.snmpstatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeStats;
import org.hibernate.Session;

@NetmonitorView(
        author = "Lubomir Kaplan",
        name = "SNMP Status View",
        description = "View to display basic SNMP data acquired from device",
        version = "1.0.0",
        displayName = "SNMP Status",
        orderKey = 210
)
public class SnmpStatusView implements View {

    protected interface SnmpStatusRecord {

        public NmDevice getDeviceRecord();

        public NmProbe getProbeRecord();

        public String getSnmpSysDescr();

        public String getSnmpSysName();

        public String getSnmpSysLocation();

        public String getSnmpSysUptime();
    }

    private final Client client;
    private final SnmpStatusPanel panel;
    private Session mysqlSession = null;

    public SnmpStatusView(Client client) {
        this.client = client;
        this.panel = new SnmpStatusPanel(this);
    }

    @Override
    public JPanel getPanel() {
        return this.panel;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public void refresh() {
        if (this.mysqlSession != null) {
            this.mysqlSession.close();
        }
        this.mysqlSession = this.getClient().getMysqlSessionFactory().openSession();
        this.panel.refresh();
    }

    public List<SnmpStatusRecord> getStatusRecords() {
        List<SnmpStatusRecord> recordList = new ArrayList<>();
        this.mysqlSession.beginTransaction();
        List<NmDevice> deviceList = this.mysqlSession.createCriteria(NmDevice.class).list();
        for (NmDevice deviceRecord : deviceList) {
            NmProbe statusProbeRecord = null;
            for (NmProbe probe : (Set<NmProbe>) deviceRecord.getNmProbes()) {
                if (probe.getNmProbeType().getProbeClasspath().equals("net.ctrdn.stuba.dbs.netmonitor.server.probe.status.SnmpStatusProbe")) {
                    statusProbeRecord = probe;
                }
            }
            final NmDevice finalDeviceRecord = deviceRecord;
            final NmProbe finalProbeRecord = statusProbeRecord;
            final String finalNotAvailableString = "-";
            if (statusProbeRecord == null) {
                recordList.add(new SnmpStatusRecord() {

                    @Override
                    public NmDevice getDeviceRecord() {
                        return finalDeviceRecord;
                    }

                    @Override
                    public NmProbe getProbeRecord() {
                        return finalProbeRecord;
                    }

                    @Override
                    public String getSnmpSysDescr() {
                        return finalNotAvailableString;
                    }

                    @Override
                    public String getSnmpSysName() {
                        return finalNotAvailableString;
                    }

                    @Override
                    public String getSnmpSysLocation() {
                        return finalNotAvailableString;
                    }

                    @Override
                    public String getSnmpSysUptime() {
                        return finalNotAvailableString;
                    }
                });
            } else {
                String tempSysDescr = finalNotAvailableString, tempSysName = finalNotAvailableString, tempSysUptime = finalNotAvailableString, tempSysLocation = finalNotAvailableString;

                for (NmProbeStats statRecord : (Set<NmProbeStats>) statusProbeRecord.getNmProbeStatses()) {
                    switch (statRecord.getAttributeName()) {
                        case "sysDescr": {
                            tempSysDescr = statRecord.getAttributeValue();
                            break;
                        }
                        case "sysName": {
                            tempSysName = statRecord.getAttributeValue();
                            break;
                        }
                        case "sysLocation": {
                            tempSysLocation = statRecord.getAttributeValue();
                            break;
                        }
                        case "sysUptime": {
                            tempSysUptime = statRecord.getAttributeValue();
                            break;
                        }
                    }
                }

                final String finalSysDescrString = tempSysDescr;
                final String finalSysNameString = tempSysName;
                final String finalSysUptimeString = tempSysUptime;
                final String finalSysLocationString = tempSysLocation;
                recordList.add(new SnmpStatusRecord() {

                    @Override
                    public NmDevice getDeviceRecord() {
                        return finalDeviceRecord;
                    }

                    @Override
                    public NmProbe getProbeRecord() {
                        return finalProbeRecord;
                    }

                    @Override
                    public String getSnmpSysDescr() {
                        return finalSysDescrString;
                    }

                    @Override
                    public String getSnmpSysName() {
                        return finalSysNameString;
                    }

                    @Override
                    public String getSnmpSysLocation() {
                        return finalSysLocationString;
                    }

                    @Override
                    public String getSnmpSysUptime() {
                        return finalSysUptimeString;
                    }
                });
            }
        }
        this.mysqlSession.getTransaction().commit();
        return recordList;
    }

}
